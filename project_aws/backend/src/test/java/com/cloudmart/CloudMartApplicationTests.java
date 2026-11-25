package com.cloudmart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        "cloudmart.local.image-dir=target/test-images"
})
@AutoConfigureMockMvc
class CloudMartApplicationTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void catalogIsSeededAndCacheWorks() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(6)));
        mvc.perform(get("/api/products/1")).andExpect(jsonPath("$.cacheHit").value(false));
        mvc.perform(get("/api/products/1")).andExpect(jsonPath("$.cacheHit").value(true));
    }

    @Test
    void createProductWithImageAndServeIt() throws Exception {
        var image = new MockMultipartFile("image", "pic.png", "image/png", new byte[]{1, 2, 3});
        String body = mvc.perform(multipart("/api/products").file(image)
                        .param("name", "Test").param("price", "9.99"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String url = json.readTree(body).get("imageUrl").asText();
        assertThat(url).startsWith("/api/images/products/");
        mvc.perform(get(url)).andExpect(status().isOk());
        mvc.perform(post("/api/products/upload-url")).andExpect(status().isNotImplemented());
    }

    @Test
    void checkoutIsProcessedInBackground() throws Exception {
        mvc.perform(post("/api/cart/s1/items").contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":2,\"quantity\":2}")).andExpect(jsonPath("$.items.length()").value(1));
        String body = mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"s1\",\"email\":\"a@b.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();

        String status = "NEW";
        for (int i = 0; i < 30 && status.equals("NEW"); i++) {
            Thread.sleep(250);
            JsonNode o = json.readTree(mvc.perform(get("/api/orders/" + id)).andReturn().getResponse().getContentAsString());
            status = o.get("status").asText();
        }
        assertThat(status).isEqualTo("PROCESSED");
    }

    @Test
    void labEndpoints() throws Exception {
        mvc.perform(get("/api/lab/whoami")).andExpect(jsonPath("$.instanceId").value("local"));
        mvc.perform(post("/api/lab/health/down"));
        mvc.perform(get("/api/lab/health")).andExpect(status().isServiceUnavailable());
        mvc.perform(post("/api/lab/health/up"));
        mvc.perform(get("/api/lab/health")).andExpect(status().isOk());
        mvc.perform(get("/api/lab/config")).andExpect(jsonPath("$.storage").value("local"));
    }
}
