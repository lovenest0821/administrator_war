package com.kiwoom.administrator.account.repositories;

import com.kiwoom.administrator.account.domain.Department;
import com.kiwoom.administrator.config.database.KwsvcConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
