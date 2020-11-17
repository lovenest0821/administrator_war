package com.kiwoom.administrator.sample;

import com.kiwoom.administrator.account.CurrentAccount;
import com.kiwoom.administrator.account.domain.Account;
import com.kiwoom.administrator.sample.mapper.KfdMapper;
import com.kiwoom.administrator.sample.mapper.KwsvcMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;

@Controller
@AllArgsConstructor
public class SampleController {

    private final KwsvcMapper kwsvcMapper;
    private final KfdMapper kfdMapper;

    @GetMapping("/dashboard")
    public String dashboard(@CurrentAccount Account account, Model model) {
        System.out.println("email : " + account.getEmail());
        System.out.println("userIp : " + account.getUserIp());
        account.getAccountRoles().forEach(role -> System.out.print("role : " + role.getRole().getRoleName() + "\n"));
        System.out.println("department : " + account.getDepartment().getDeptName());
        String email = kwsvcMapper.getAccount(account.getEmail());
        System.out.println("sampleMapper.getEmail() = " + email);
        String localOfCityName = kfdMapper.getZone("Seoul");
        System.out.println("localOfCityName = " + localOfCityName);
        model.addAttribute(account);
        ArrayList<String> test = new ArrayList<>();
        test.add("50918955");
        test.add("50583089");
        test.add("45933471");
        test.add("45932471");
        test.add("50341597");

        for(String a : test) {
            System.out.println("a = " + a);
        }

        Collections.sort(test);

        test.forEach(a -> {
            System.out.println("b = " + a);
        });

        return "dashboard";
    }

    @GetMapping("/wm/dashboard")
    public String kdreamDashboard(@CurrentAccount Account account, Model model) {
        System.out.println("email : " + account.getEmail());
        System.out.println("userIp : " + account.getUserIp());
        account.getAccountRoles().forEach(role -> System.out.print("role : " + role.getRole().getRoleName() + "\n"));
        System.out.println("department : " + account.getDepartment().getDeptName());
        model.addAttribute(account);
        return "dashboard";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
