package org.greenearth.administrator.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenearth.administrator.account.UserAccount;
import org.greenearth.administrator.account.domain.Account;
import org.greenearth.administrator.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String name = authentication.getName();
        String credentials = authentication.getCredentials().toString();
        String[] username = name.split("\\|");

        UserDetails userDetails = accountService.loadUserByUsername(username[0]);

        UserAccount userAccount = (UserAccount) userDetails;
        Account account = userAccount.getAccount();

        // 비밀번호 일치 여부
        if(passwordEncoder.matches(credentials, userDetails.getPassword())) {
            // 로그인 한 IP 동일 여부 체크
            if(!account.getUserIp().equals(username[1])) {
                logger.info("[" + username[0] + "] 로그인이 허용된 IP가 아닙니다. 접속시도IP: " + username[1]);
                throw new PermitIpException("로그인이 허용된 IP가 아닙니다.");
            }

            // 비밀번호 변경 기간 만료
            if(account.getPasswordChangeAt().plusMonths(3).isBefore(LocalDateTime.now())){
                logger.info("[" + username[0] + "] 비밀번호 유효기간 만료. 이전 변경일자: " + account.getPasswordChangeAt() +
                        ", 현재일자: "+ LocalDateTime.now() + ", 접속시도IP: " + username[1]);
                throw new CredentialsExpiredException("비밀번호 유효기간이 만료되었습니다. 비밀번호를 변경하여 주시기 바랍니다.");
            }

            logger.info("["+ username[0] +"] 로그인에 성공하였습니다. " + "접속시도IP: " + username[1]);
            accountService.initializePasswordErrorCount(account);
        } else {
            Account updatePasswordErrorCountAndAccountLock = accountService.updatePasswordErrorCountAndAccountLock(account);
            int errorCount = updatePasswordErrorCountAndAccountLock.getPasswordErrorCount();

            if(errorCount == 5){
                logger.info("["+ username[0] +"] 비밀번호 "+ errorCount +"회 오류로 계정이 잠겼습니다. 관리자에게 문의하세요. "
                        + "접속시도IP: " + username[1]);
                throw new BadCredentialsException("비밀번호 "+ errorCount +"회 오류로 계정이 잠겼습니다. 관리자에게 문의하세요.");
            } else {
                logger.info("["+ username[0] +"] 비밀번호 "+ errorCount +"회 오류로 입니다. "
                        + "접속시도IP: " + username[1]);
                throw new BadCredentialsException("비밀번호 "+ errorCount +"회 오류 입니다.");
            }
        }

        return new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
