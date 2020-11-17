package org.greenearth.administrator.config;

import com.google.common.collect.ImmutableList;
import org.greenearth.administrator.account.domain.Department;
import org.greenearth.administrator.account.domain.JoinAccount;
import org.greenearth.administrator.account.domain.Role;
import org.greenearth.administrator.account.repositories.AccountRoleRepository;
import org.greenearth.administrator.account.repositories.DepartmentRepository;
import org.greenearth.administrator.account.repositories.EconomyWordRepository;
import org.greenearth.administrator.account.repositories.RoleRepository;
import org.greenearth.administrator.account.service.AccountService;
import org.greenearth.administrator.account.service.DepartmentService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired AccountService accountService;
            @Autowired DepartmentService departmentService;
            @Autowired DepartmentRepository departmentRepository;
            @Autowired AccountRoleRepository accountRoleRepository;
            @Autowired RoleRepository roleRepository;
            @Autowired EconomyWordRepository economyWordRepository;

            @Override
            public void run(ApplicationArguments args) throws IOException {
                Department department = Department.builder().deptName("투자컨텐츠").build();
                Department department1 = Department.builder().deptName("금융상품").build();
                Department department2 = Department.builder().deptName("리테일금융").build();

                departmentService.createDepartment(department);
                departmentService.createDepartment(department1);
                departmentService.createDepartment(department2);

                Role role = Role.builder().roleName("KDRM").build();
                Role role2 = Role.builder().roleName("EDU").build();
                Role role3 = Role.builder().roleName("CHK").build();
                Role role4 = Role.builder().roleName("HOWT").build();
                Role role1 = Role.builder().roleName("WM").build();

                roleRepository.save(role);
                roleRepository.save(role2);
                roleRepository.save(role3);
                roleRepository.save(role4);
                roleRepository.save(role1);

                JoinAccount joinAccount = JoinAccount.builder()
                        .email("becho08@gmail.com")
                        .password(passwordEncoder().encode("pentium1"))
                        .userName("조병은")
                        .deptId(1L)
                        .userIp("127.0.0.1")
                        .roleIds(ImmutableList.of(1L, 2L))
                        .build();

                accountService.createAccount(joinAccount);

                Resource resource = new ClassPathResource("economyWord_200424.xls");
                HSSFWorkbook workbook = new HSSFWorkbook(Files.newInputStream(Paths.get(resource.getFile().getPath())));
                HSSFSheet sheetAt = workbook.getSheetAt(0);
/*
                for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++){
                    HSSFRow row = sheetAt.getRow(i);

                    EconomyWord economyWord = EconomyWord.builder()
                            .id(Long.parseLong(row.getCell(0).getStringCellValue().trim()))
                            .word(row.getCell(1).getStringCellValue().trim())
                            .description(row.getCell(2).getStringCellValue().trim())
                            .build();
                    economyWordRepository.save(economyWord);
                }
*/
            }
        };
    }
}
