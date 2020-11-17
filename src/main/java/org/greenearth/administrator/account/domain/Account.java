package org.greenearth.administrator.account.domain;

import lombok.*;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Account {

    @Id @GeneratedValue(generator = "account_seq")
    @Column(name = "ACCOUNT_ID")
    private Long id;

    @Column(unique = true) @Email
    private String email;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime emailCheckTokenConfirmAt;

    private String userName;

    @Pattern(regexp = "^(\\d{3})[.](\\d|\\d{2}|\\d{3})[.](\\d|\\d{2}|\\d{3})[.](\\d|\\d{2}|\\d{3})")
    private String userIp;

    private String password;

    private int passwordErrorCount = 0;

    private boolean accountLock = false;

    private LocalDateTime passwordChangeAt;

    private LocalDateTime registerAt;

    private LocalDateTime updateAt;

    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountRole> accountRoles = new ArrayList<>();

    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "DEPT_NO")
    private Department department;

    // 생성자 메서드
    public static Account createAccount(JoinAccount joinAccount, Department department, AccountRole... accountRoles) {
        Account account = new Account();
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.map(joinAccount, account);
        account.setDepartment(department);
        for (AccountRole accountRole: accountRoles) {
            account.addAccountRole(accountRole);
        }
        account.setRegisterAt(LocalDateTime.now());
        account.setUpdateAt(LocalDateTime.now());

        return account;
    }

    // 비즈니스 로직
   public void addAccountRole(AccountRole accountRole) {
        this.accountRoles.add(accountRole);
        accountRole.setAccount(this);
    }

   public void generateEmailCheckToken() {
       this.emailCheckToken = UUID.randomUUID().toString();
       this.emailCheckTokenGeneratedAt = LocalDateTime.now();
   }

   public boolean isValidToken(String token) {
       if(this.emailCheckToken != null){
           if(this.emailCheckTokenConfirmAt != null) {
               return this.emailCheckToken.equals(token) && this.emailCheckTokenGeneratedAt.isAfter(emailCheckTokenConfirmAt);
           } else {
               return this.emailCheckToken.equals(token);
           }
       } else {
           return false;
       }
   }
}
