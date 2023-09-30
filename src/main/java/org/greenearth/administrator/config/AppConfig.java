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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
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
                        .password("pentium1")
                        .userName("조병은")
                        .deptId(1L)
                        .userIp("127.0.0.1")
                        .roleIds(ImmutableList.of(1L, 2L))
                        .build();

                accountService.createAccount(joinAccount);
/*
                Resource resource = new ClassPathResource("economyWord_20210203.xlsx");
                XSSFWorkbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(resource.getFile().getPath())));
                XSSFSheet sheetAt = workbook.getSheetAt(0);

                for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++){
                    XSSFRow row = sheetAt.getRow(i);

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
