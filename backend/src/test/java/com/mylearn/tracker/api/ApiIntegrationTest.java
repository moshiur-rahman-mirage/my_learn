package com.mylearn.tracker.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.tracker.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HTTP contract: status codes, JSON shapes and the common error body. */
@AutoConfigureMockMvc
class ApiIntegrationTest extends IntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;

    @Test
    void planCrudLifecycle() throws Exception {
        JsonNode plan = body(postJson("/api/plans", "{\"name\":\"Core Java\",\"description\":\"JVM\"}")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/plans/")))
                .andExpect(jsonPath("$.progress.percent").isEmpty()));
        long id = plan.get("id").asLong();

        mvc.perform(get("/api/plans").param("search", "core")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(put("/api/plans/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Core Java 21\",\"status\":\"ARCHIVED\",\"version\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"))
                .andExpect(jsonPath("$.version").value(1));
        mvc.perform(delete("/api/plans/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/plans/" + id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Plan " + id + " not found"))
                .andExpect(jsonPath("$.path").value("/api/plans/" + id));
    }

    @Test
    void itemTreeCompletionMoveAndProgress() throws Exception {
        long plan = createPlan("Odoo");
        long module = createItem(plan, "{\"name\":\"Phase 0\",\"type\":\"MODULE\"}");
        long leaf = createItem(plan, "{\"name\":\"Editions\",\"type\":\"TOPIC\",\"parentId\":" + module + ",\"priority\":\"MUST\"}");
        createItem(plan, "{\"name\":\"Versioning\",\"type\":\"TOPIC\",\"parentId\":" + module + "}");

        mvc.perform(patch("/api/items/" + leaf + "/completion").contentType(MediaType.APPLICATION_JSON)
                .content("{\"completed\":true}")).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true));

        mvc.perform(get("/api/plans/" + plan + "/items")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Phase 0"))
                .andExpect(jsonPath("$[0].checkable").value(false))
                .andExpect(jsonPath("$[0].descendantCount").value(2))
                .andExpect(jsonPath("$[0].progress.percent").value(50.0))
                .andExpect(jsonPath("$[0].children[0].checkable").value(true))
                .andExpect(jsonPath("$[0].children[0].priority").value("MUST"));
        mvc.perform(get("/api/plans/" + plan + "/progress"))
                .andExpect(jsonPath("$.completed").value(1)).andExpect(jsonPath("$.total").value(2));
        mvc.perform(get("/api/items/" + module + "/progress")).andExpect(jsonPath("$.percent").value(50.0));
        mvc.perform(get("/api/items/" + leaf)).andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name").value("Editions"))
                .andExpect(jsonPath("$.path[0].name").value("Phase 0"));

        mvc.perform(patch("/api/items/" + leaf + "/move").contentType(MediaType.APPLICATION_JSON)
                .content("{\"parentId\":null,\"position\":0}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.parentId").isEmpty());
        mvc.perform(patch("/api/items/" + leaf + "/reorder").contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\":1}")).andExpect(status().isOk()).andExpect(jsonPath("$.displayOrder").value(1));
        mvc.perform(get("/api/dashboard/summary")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLeaves").value(2))
                .andExpect(jsonPath("$.completedLeaves").value(1))
                .andExpect(jsonPath("$.recentlyCompleted[0].itemName").value("Editions"));

        mvc.perform(delete("/api/items/" + module)).andExpect(status().isNoContent());
        mvc.perform(get("/api/plans/" + plan + "/items")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void validationErrorsListTheFields() throws Exception {
        postJson("/api/plans", "{\"name\":\"  \"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
        long plan = createPlan("P");
        postJson("/api/plans/" + plan + "/items", "{\"name\":\"X\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("type"));
        postJson("/api/plans/" + plan + "/items", "{\"name\":\"X\",\"type\":\"PHASE\"}")
                .andExpect(status().isBadRequest()); // unknown enum value
        postJson("/api/plans", "{not json").andExpect(status().isBadRequest());
        mvc.perform(get("/api/plans/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void conflictsAndRuleViolations() throws Exception {
        createPlan("Same");
        postJson("/api/plans", "{\"name\":\"same\"}").andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A plan named 'same' already exists"));

        long plan = createPlan("Tree");
        long module = createItem(plan, "{\"name\":\"M\",\"type\":\"MODULE\"}");
        long child = createItem(plan, "{\"name\":\"C\",\"type\":\"MODULE\",\"parentId\":" + module + "}");

        // completing a module -> 409
        mvc.perform(patch("/api/items/" + module + "/completion").contentType(MediaType.APPLICATION_JSON)
                .content("{\"completed\":true}")).andExpect(status().isConflict());
        // moving into own descendant -> 409
        mvc.perform(patch("/api/items/" + module + "/move").contentType(MediaType.APPLICATION_JSON)
                .content("{\"parentId\":" + child + "}")).andExpect(status().isConflict());
        // stale edit -> 409
        mvc.perform(put("/api/items/" + module).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"M2\",\"type\":\"MODULE\",\"version\":0}")).andExpect(status().isOk());
        mvc.perform(put("/api/items/" + module).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"M3\",\"type\":\"MODULE\",\"version\":0}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(startsWith("Item " + module + " was changed elsewhere")));
        // position out of range -> 422
        mvc.perform(patch("/api/items/" + child + "/reorder").contentType(MediaType.APPLICATION_JSON)
                .content("{\"position\":5}")).andExpect(status().isUnprocessableEntity());
        // unknown endpoint -> 404 JSON
        mvc.perform(get("/api/nope")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }

    // --- helpers ----------------------------------------------------------------------------

    private ResultActions postJson(String url, String body) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private long createPlan(String name) throws Exception {
        return body(postJson("/api/plans", "{\"name\":\"" + name + "\"}").andExpect(status().isCreated())).get("id").asLong();
    }

    private long createItem(long plan, String body) throws Exception {
        return body(postJson("/api/plans/" + plan + "/items", body).andExpect(status().isCreated())).get("id").asLong();
    }

    private JsonNode body(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsString());
    }
}
