package org.greenearth.administrator.account.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Department {

    @Id @GeneratedValue(generator = "department_seq")
    @Column(name = "DEPT_NO")
    private Long deptNo;

    @Column(unique = true, name="DEPT_NAME")
    private String deptName;

    @OneToMany(mappedBy = "department")
    List<Account> accounts = new ArrayList<>();
}
