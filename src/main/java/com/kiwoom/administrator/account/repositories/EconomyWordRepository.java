package com.kiwoom.administrator.account.repositories;

import com.kiwoom.administrator.account.domain.EconomyWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EconomyWordRepository extends JpaRepository<EconomyWord, Long> {
}
