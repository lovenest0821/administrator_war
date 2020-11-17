package com.kiwoom.administrator.account.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class AccountRole {

    @Id @GeneratedValue(generator = "account_role_seq")
    @Column(name = "ACCOUNT_ROLE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    @Column(name = "RGST_DT")
    private LocalDateTime registeredAt;

    @Column(name = "MDFY_DT")
    private LocalDateTime changedAt;

    // 생성 메서드
    public static AccountRole createAccountRole(Role role) {
        AccountRole accountRole = new AccountRole();
        accountRole.setRole(role);
        accountRole.setRegisteredAt(LocalDateTime.now());
        accountRole.setChangedAt(LocalDateTime.now());

        return accountRole;
    }
}
