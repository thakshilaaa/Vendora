package com.vendora.epic1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke: a page that is allowed while the platform is in setup mode (no admin user yet).
 * See {@link com.vendora.epic1.security.SetupInterceptor} — most routes redirect to /admin-signup.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicPageSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminSignupPageIsOkInSetupMode() throws Exception {
        mockMvc.perform(get("/admin-signup"))
                .andExpect(status().isOk());
    }
}
