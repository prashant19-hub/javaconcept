package com.prashant.companyms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyMsWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersDashboardAndManagementPages() throws Exception {
        mockMvc.perform(get("/companyms/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
        mockMvc.perform(get("/companyms/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"));
        mockMvc.perform(get("/companyms/employees/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-employee"));
        mockMvc.perform(get("/companyms/attendance"))
                .andExpect(status().isOk())
                .andExpect(view().name("attendance"));
        mockMvc.perform(get("/companyms/payroll"))
                .andExpect(status().isOk())
                .andExpect(view().name("payroll"));
    }

    @Test
    void createsEmployeeThroughRestApi() throws Exception {
        mockMvc.perform(post("/api/companyms/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Asha Sharma",
                                  "email": "asha@example.com",
                                  "department": "Engineering"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
