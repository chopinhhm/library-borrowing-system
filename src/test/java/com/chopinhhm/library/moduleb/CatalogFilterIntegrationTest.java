package com.chopinhhm.library.moduleb;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogFilterIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test
    void filtersBooksByCategoryShelfAndAvailability() throws Exception {
        mvc.perform(get("/api/books")
                .param("category", "文学")
                .param("shelfLocation", "B-02")
                .param("availableOnly", "true")
                .with(httpBasic("reader", "reader123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("红楼梦"));
    }
}
