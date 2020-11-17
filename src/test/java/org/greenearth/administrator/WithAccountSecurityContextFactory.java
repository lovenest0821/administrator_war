package org.greenearth.administrator;

import com.google.common.collect.ImmutableList;
import org.greenearth.administrator.account.domain.Department;
import org.greenearth.administrator.account.domain.JoinAccount;
import org.greenearth.administrator.account.domain.Role;
import org.greenearth.administrator.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String email = withAccount.value();

        Department.builder().deptNo(1L).deptName("투자컨텐츠").build();
        Role.builder().id(1L).roleName("KDRM").build();

        JoinAccount joinAccount = JoinAccount.builder()
                .email("test@gmail.com")
                .password(passwordEncoder.encode("pentium1"))
                .userName("테스트")
                .userIp("127.0.0.1")
                .deptId(1L)
                .roleIds(ImmutableList.of(1L))
                .build();
        accountService.createAccount(joinAccount);

        UserDetails principal = accountService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
