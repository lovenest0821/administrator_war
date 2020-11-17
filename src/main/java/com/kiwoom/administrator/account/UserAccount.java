package com.kiwoom.administrator.account;

import com.kiwoom.administrator.account.domain.Account;
import com.kiwoom.administrator.account.domain.AccountRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class UserAccount extends User {

    private Account account;

    public UserAccount(Account account) {
        super(account.getEmail(), account.getPassword(), makeGrantedAuthority(account.getAccountRoles()));
        this.account = account;
    }

    private static List<GrantedAuthority> makeGrantedAuthority(List<AccountRole> roles) {
        List<GrantedAuthority> list = new ArrayList<>();

        roles.forEach(role -> list.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().getRoleName())));

        return list;
    }
}
