package com.kiwoom.administrator.account.service;

import com.kiwoom.administrator.account.domain.Department;
import com.kiwoom.administrator.account.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public void createDepartment(Department department) {
        departmentRepository.save(department);
    }
}
