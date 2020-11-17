package com.kiwoom.administrator.account.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter @Setter @Builder
public class JoinAccount {

    @Email(message = "이메일 주소를 입력해 주세요.")
    private String email;

    @NotNull(message = "사용자 이름을 입력해 주세요.")
    private String userName;

    @Pattern(regexp = "^(\\d{3})[.](\\d|\\d{2}|\\d{3})[.](\\d|\\d{2}|\\d{3})[.](\\d|\\d{2}|\\d{3})")
    @NotNull(message = "IP주소를 입력해 주세요.")
    private String userIp;

    @NotNull(message = "패스워드를 입력해 주세요.")
    private String password;

    @NotNull(message = "소속 부서를 선택해 주세요.")
    private Long deptId;

    private List<Long> roleIds;
}
