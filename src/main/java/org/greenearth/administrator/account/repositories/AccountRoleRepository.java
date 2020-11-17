package org.greenearth.administrator.account.repositories;

import org.greenearth.administrator.account.domain.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
}
