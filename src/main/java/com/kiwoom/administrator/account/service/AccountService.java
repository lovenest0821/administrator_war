package com.kiwoom.administrator.account.service;

import com.kiwoom.administrator.account.UserAccount;
import com.kiwoom.administrator.account.domain.*;
import com.kiwoom.administrator.account.repositories.AccountRepository;
import com.kiwoom.administrator.account.repositories.DepartmentRepository;
import com.kiwoom.administrator.account.repositories.RoleRepository;
import com.kiwoom.administrator.config.AppProperties;
import com.kiwoom.administrator.mail.EmailMessage;
import com.kiwoom.administrator.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Account account = accountRepository.findByEmail(username);
        if(account == null){
            logger.info("[" + username + "] 등록된 email이 없습니다.");
            throw new UsernameNotFoundException("등록된 email이 없습니다. 확인 후 다시 로그인 하세요.");
        }

        if(account.isAccountLock()) {
            logger.info("[" + username +"] 비밀번호 5회 오류로 계정이 잠겼습니다.");
            throw new LockedException("비밀번호 5회 오류로 계정이 잠겼습니다.");
        }

        // Account VO객체 초기 세팅을 위한 호출
        Department department = account.getDepartment();
        department.getDeptName();

        return new UserAccount(account);
    }

    public Account updatePasswordErrorCountAndAccountLock(Account account) {
        if(account.getPasswordErrorCount() + 1 == 5){
            account.setAccountLock(true);
        }

        account.setPasswordErrorCount(account.getPasswordErrorCount() + 1);

        return accountRepository.save(account);
    }

    public void initializePasswordErrorCount(Account account) {
        account.setLastLoginAt(LocalDateTime.now());
        account.setPasswordErrorCount(0);
        accountRepository.save(account);
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public void sendPasswordInitEmail(Account account) {
        account.generateEmailCheckToken();
        accountRepository.save(account);

        Context context = new Context();
        context.setVariable("link", "/email-by-passwordInit?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getUserName());
        context.setVariable("linkName", " 패스워드 초기화 하기");
        context.setVariable("message", "패스워드를 초기화 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("키움증권 관리자, 패스워드 초기화 링크")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEMP")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public boolean isValidEmailToken(Account account, String token) {
        if(account.isValidToken(token)) {
            account.setEmailCheckTokenConfirmAt(LocalDateTime.now());
            accountRepository.save(account);
            return true;
        } else {
            return false;
        }
    }

    public void setPasswordChange(Account account, PasswordChangeForm passwordChangeForm) {
        account.setPassword(passwordEncoder.encode(passwordChangeForm.getNewPassword()));
        account.setPasswordChangeAt(LocalDateTime.now());
        account.setPasswordErrorCount(0);
        account.setAccountLock(false);
        accountRepository.save(account);
    }

    public Account createAccount(JoinAccount joinAccount) {
        int count = joinAccount.getRoleIds().size();
        AccountRole[] accountRoles = new AccountRole[count];
        Department department = new Department();

        for(int i=0; i < count; i++) {
            Optional<Role> roleOptional = roleRepository.findById(joinAccount.getRoleIds().get(i));
            if(roleOptional.isPresent()) {
                accountRoles[i] = AccountRole.createAccountRole(roleOptional.get());
            }
        }
        Optional<Department> departmentOptional = departmentRepository.findById(joinAccount.getDeptId());
        if(departmentOptional.isPresent()) {
            department = departmentOptional.get();
        }

        return accountRepository.save(Account.createAccount(joinAccount, department, accountRoles));
    }
}
