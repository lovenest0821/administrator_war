package org.greenearth.administrator.account.service;

import org.greenearth.administrator.account.domain.Department;
import org.greenearth.administrator.account.repositories.DepartmentRepository;
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
