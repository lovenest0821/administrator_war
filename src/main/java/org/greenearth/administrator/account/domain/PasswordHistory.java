package org.greenearth.administrator.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class PasswordHistory {

    @Id @GeneratedValue
    private Long passwordHistoryId;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Account account;

    private LocalDateTime registerAt;

    public static PasswordHistory createPasswordHistory(Account account) {
        PasswordHistory passwordHistory = new PasswordHistory();

        passwordHistory.setPassword(account.getPassword());
        passwordHistory.setAccount(account);
        passwordHistory.setRegisterAt(LocalDateTime.now());

        return passwordHistory;
    }
}
