package org.greenearth.administrator.account.repositories;

import org.greenearth.administrator.account.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
