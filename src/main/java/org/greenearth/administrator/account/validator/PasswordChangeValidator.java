package org.greenearth.administrator.account.validator;

import org.greenearth.administrator.account.repositories.AccountRepository;
import org.greenearth.administrator.account.domain.Account;
import org.greenearth.administrator.account.domain.PasswordChangeForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordChangeValidator implements Validator {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(PasswordChangeForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PasswordChangeForm passwordChangeForm = (PasswordChangeForm) o;
        if(!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong value", "입력한 패스워드가 일치하지 않습니다.");
        }

        Account account = accountRepository.findByEmail(passwordChangeForm.getEmail());
        if(passwordEncoder.matches(passwordChangeForm.getNewPassword(), account.getPassword())) {
            errors.rejectValue("newPassword", "same value", "만료된 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");
        }
    }
}
