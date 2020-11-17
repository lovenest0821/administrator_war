package org.greenearth.administrator.account.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Builder @Getter @Setter
public class Role {

    @Id @GeneratedValue
    @Column(name = "ROLE_ID")
    private Long id;

    private String roleName;
}
