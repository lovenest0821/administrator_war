package com.kiwoom.administrator.account.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Getter @Setter @Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
public class EconomyWord {

    @Id
    Long id;

    String word;

    @Lob
    String description;
}
