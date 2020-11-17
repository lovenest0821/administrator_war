package com.kiwoom.administrator.account.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Getter @Setter
public class PasswordChangeForm {

    @Email
    private String email;

    @Length(min = 8, max = 50)
    private String newPassword;

    @Length(min = 8, max = 50)
    private String newPasswordConfirm;

}
