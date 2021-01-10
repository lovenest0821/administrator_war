package org.greenearth.administrator.account;

import lombok.RequiredArgsConstructor;
import org.greenearth.administrator.account.domain.Account;
import org.greenearth.administrator.account.domain.PasswordChangeForm;
import org.greenearth.administrator.account.service.AccountService;
import org.greenearth.administrator.account.validator.PasswordChangeValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final PasswordChangeValidator passwordChangeValidator;

    public final static String PASSWORD_INIT_URL = "/password-init";
    public final static String PASSWORD_INIT_VIEW = "account/password-init";

    public final static String EMAIL_AUTHENTICATION_URL = "/email-by-passwordInit";

    public final static String PASSWORD_CHANGE_URL = "/password-change";
    public final static String PASSWORD_CHANGE_VIEW = "account/password";

    @InitBinder("passwordChangeForm")
    public void passwordChangeFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordChangeValidator);
    }

    // email 인증 폼
    @GetMapping(PASSWORD_INIT_URL)
    public String passwordInit() {
        return "account/password-init";
    }

    // email 인증 토근 발급
    @PostMapping(PASSWORD_INIT_URL)
    public String passwordInitSendEmail(String email, Model model) {
        Account account = accountService.findByEmail(email);
        if(account == null) {
            model.addAttribute("error", "입력하신 이메일 계정이 존재하지 않습니다.");
            return PASSWORD_INIT_VIEW;
        }

        accountService.sendPasswordInitEmail(account);
        model.addAttribute("message", "패스워드 초기화 이메일이 발송되었습니다.");
        return PASSWORD_INIT_VIEW;
    }

    // email 인증
    @GetMapping(EMAIL_AUTHENTICATION_URL)
    public String emailByPasswordInit(String token, String email, RedirectAttributes attributes) {
        Account account = accountService.findByEmail(email);
        if(account == null || !accountService.isValidEmailToken(account, token)) {
            accountService.logout();
            attributes.addFlashAttribute("error", "유효한 인증요청이 아닙니다. 확인 후 다시 시도해 주세요.");
            return "redirect:" + PASSWORD_INIT_URL;
        }
        System.out.println("account = " + account);
        accountService.login(account);
        attributes.addFlashAttribute("message", "패스워드 변경 후 로그인 해주세요.");
        return "redirect:" + PASSWORD_CHANGE_URL;
    }

    // 패스워드 변경 폼
    @GetMapping(PASSWORD_CHANGE_URL)
    public String passwordChangeForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordChangeForm());
        return PASSWORD_CHANGE_VIEW;
    }

    // 패스워드 변경
    @PostMapping(PASSWORD_CHANGE_URL)
    public String passwordChange(@CurrentAccount Account account, @Valid PasswordChangeForm passwordChangeForm,
                                 Errors errors, Model model) {
        if(!account.getEmail().equals(passwordChangeForm.getEmail())) {
            model.addAttribute("error", "인증받은 계정의 패스워드 변경만 가능합니다.");
            return PASSWORD_CHANGE_VIEW;
        }

        if(errors.hasErrors()) {
            model.addAttribute(passwordChangeForm);
            return PASSWORD_CHANGE_VIEW;
        }

        model.addAttribute("message", "패스워드 변경이 완료되었습니다. 로그인 해주세요.");
        accountService.setPasswordChange(account, passwordChangeForm);
        accountService.logout();
        return PASSWORD_CHANGE_VIEW;
    }
}
