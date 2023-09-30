package org.greenearth.administrator.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadTestController {

    @GetMapping("/uploadEx")
    public String uploadEx() {
        return "uploadEx";
    }
}
