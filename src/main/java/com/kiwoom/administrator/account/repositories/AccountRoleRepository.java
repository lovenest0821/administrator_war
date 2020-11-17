package com.kiwoom.administrator.account.repositories;

import com.kiwoom.administrator.account.domain.AccountRole;
import com.kiwoom.administrator.config.database.KwsvcConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
}
