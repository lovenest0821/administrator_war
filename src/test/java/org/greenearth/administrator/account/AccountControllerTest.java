package org.greenearth.administrator.account;

import org.greenearth.administrator.account.domain.Account;
import org.greenearth.administrator.account.repositories.AccountRepository;
import org.greenearth.administrator.WithAccount;
import org.greenearth.administrator.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @MockBean
    EmailService emailService;

    @Test
    @DisplayName("패스워드 초기화 폼")
    void passwordInitForm() throws Exception {
        mockMvc.perform(get(AccountController.PASSWORD_INIT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.PASSWORD_INIT_VIEW))
        ;
    }

    @Test
    @WithAccount("test@gmail.com")
    @DisplayName("패스워드 초기화 이메일 발송")
    void passwordInitSendEmail() throws Exception {
        mockMvc.perform(post(AccountController.PASSWORD_INIT_URL)
                    .param("email", "test@gmail.com")
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountController.PASSWORD_INIT_VIEW))
                .andExpect(model().attributeExists("message"))
        ;

        Account account = accountRepository.findByEmail("test@gmail.com");
        assertNotNull(account.getEmailCheckToken());
        assertTrue(account.getEmailCheckTokenGeneratedAt().isBefore(LocalDateTime.now()));
    }
}