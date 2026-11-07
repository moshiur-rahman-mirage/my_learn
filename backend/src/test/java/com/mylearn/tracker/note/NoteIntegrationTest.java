package com.mylearn.tracker.note;

import com.mylearn.tracker.common.exception.NotFoundException;
import com.mylearn.tracker.item.ItemService;
import com.mylearn.tracker.item.ItemType;
import com.mylearn.tracker.item.dto.CreateItemRequest;
import com.mylearn.tracker.item.dto.ItemResponse;
import com.mylearn.tracker.item.dto.TreeNode;
import com.mylearn.tracker.plan.PlanService;
import com.mylearn.tracker.plan.dto.CreatePlanRequest;
import com.mylearn.tracker.support.IntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class NoteIntegrationTest extends IntegrationTest {

    @Autowired
    NoteService notes;
    @Autowired
    ItemService items;
    @Autowired
    PlanService plans;
    @Autowired
    ItemNoteRepository noteRepository;
    @Autowired
    EntityManager em;
    @Autowired
    MockMvc mvc;

    private long planId;
    private ItemResponse module;
    private ItemResponse topic;

    private void seed() {
        planId = plans.create(new CreatePlanRequest("Notes plan", null)).id();
        module = items.create(planId, new CreateItemRequest("Module", ItemType.MODULE, null, null, null, null));
        topic = items.create(planId, new CreateItemRequest("Topic", ItemType.TOPIC, module.id(), null, null, null));
    }

    @Test
    void saveReadUpdateAndClear_withoutBumpingTheItemVersion() {
        seed();
        assertThat(notes.get(topic.id()).content()).isNull();

        String html = "<h2>Summary</h2><p><strong>DI</strong> = objects get their dependencies</p><ul><li>a</li></ul>";
        assertThat(notes.save(topic.id(), html).content()).isEqualTo(html);
        assertThat(notes.save(topic.id(), "<p>v2</p>").content()).isEqualTo("<p>v2</p>");
        em.flush();
        assertThat(items.detail(topic.id()).item().version()).isEqualTo(topic.version()); // edit dialog not stale

        assertThat(notes.save(topic.id(), "<p></p>").content()).isNull();
        assertThat(noteRepository.findById(topic.id())).isEmpty();
    }

    @Test
    void treeMarksItemsWithNotes_modulesToo() {
        seed();
        notes.save(module.id(), "<p>overview</p>");
        TreeNode root = items.tree(planId).getFirst();
        assertThat(root.hasNotes()).isTrue();
        assertThat(root.children().getFirst().hasNotes()).isFalse();
    }

    @Test
    void deletingAnItemOrItsPlanDeletesTheNote() {
        seed();
        notes.save(topic.id(), "<p>x</p>");
        notes.save(module.id(), "<p>y</p>");
        em.flush();
        items.delete(topic.id());
        em.flush();
        em.clear();
        assertThat(noteRepository.findById(topic.id())).isEmpty();

        plans.delete(planId);
        em.flush();
        em.clear();
        assertThat(noteRepository.findById(module.id())).isEmpty();
    }

    @Test
    void emptinessDetection() {
        assertThat(NoteService.isEmpty(null)).isTrue();
        assertThat(NoteService.isEmpty("<p>&nbsp;</p><p> </p>")).isTrue();
        assertThat(NoteService.isEmpty("<p>a</p>")).isFalse();
        assertThat(NoteService.isEmpty("<hr>")).isFalse();
    }

    @Test
    void markdownNotesKeepTheirSourceAndFormat() {
        seed();
        String md = "## Summary\n\n- `List<String>` is **generic**\n- [ ] review";
        var saved = notes.save(topic.id(), md, NoteFormat.MARKDOWN);
        assertThat(saved.content()).isEqualTo(md);
        assertThat(saved.format()).isEqualTo(NoteFormat.MARKDOWN);
        assertThat(notes.get(topic.id()).format()).isEqualTo(NoteFormat.MARKDOWN);

        // switching formats is allowed (the UI converts the content)
        assertThat(notes.save(topic.id(), "<p>rich</p>", NoteFormat.HTML).format()).isEqualTo(NoteFormat.HTML);

        assertThat(notes.save(topic.id(), "  \n\n ", NoteFormat.MARKDOWN).content()).isNull();
        assertThat(noteRepository.findById(topic.id())).isEmpty();
        assertThat(NoteService.isEmpty("<p></p>", NoteFormat.MARKDOWN)).isFalse(); // literal text in Markdown
    }

    @Test
    void markdownHttpRoundTrip_formatDefaultsToHtml() throws Exception {
        seed();
        mvc.perform(put("/api/items/" + topic.id() + "/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"# Title\\n\\n*hi*\",\"format\":\"MARKDOWN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.format").value("MARKDOWN"));
        mvc.perform(get("/api/items/" + topic.id() + "/notes"))
                .andExpect(jsonPath("$.content").value("# Title\n\n*hi*"))
                .andExpect(jsonPath("$.format").value("MARKDOWN"));
        mvc.perform(put("/api/items/" + topic.id() + "/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"<p>x</p>\"}"))
                .andExpect(jsonPath("$.format").value("HTML"));
    }

    @Test
    void unknownItemIs404() throws Exception {
        assertThatThrownBy(() -> notes.get(999_999L)).isInstanceOf(NotFoundException.class);
        mvc.perform(get("/api/items/999999/notes")).andExpect(status().isNotFound());
    }

    @Test
    void httpRoundTrip() throws Exception {
        seed();
        mvc.perform(put("/api/items/" + topic.id() + "/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"<p>Hello <em>notes</em> \\u2014 \\u09AC\\u09BE\\u0982\\u09B2\\u09BE</p>\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
        mvc.perform(get("/api/items/" + topic.id() + "/notes"))
                .andExpect(jsonPath("$.content").value("<p>Hello <em>notes</em> — বাংলা</p>"));
    }
}
