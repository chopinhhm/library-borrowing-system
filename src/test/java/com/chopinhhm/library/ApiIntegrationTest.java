package com.chopinhhm.library;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test
    void authenticationAndRolesAreEnforced() throws Exception {
        mvc.perform(get("/api/books")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/books").with(httpBasic("reader", "reader123"))).andExpect(status().isOk());
        mvc.perform(get("/api/admin/statistics").with(httpBasic("reader", "reader123"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/circulation/borrow").param("readerId", "1").param("bookId", "1")
            .with(httpBasic("reader", "reader123"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/statistics").with(httpBasic("admin", "admin123"))).andExpect(status().isOk());
    }

    @Test
    void borrowRenewAndReturnFlowWorks() throws Exception {
        String loanJson = mvc.perform(post("/api/circulation/borrow").param("readerId", "1").param("bookId", "1")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("BORROWED"))
            .andReturn().getResponse().getContentAsString();
        long loanId = new com.fasterxml.jackson.databind.ObjectMapper().readTree(loanJson).get("id").asLong();
        mvc.perform(post("/api/circulation/loans/{id}/renew", loanId).with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.renewCount").value(1));
        mvc.perform(post("/api/circulation/loans/{id}/return", loanId).with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("RETURNED"));
    }
}
