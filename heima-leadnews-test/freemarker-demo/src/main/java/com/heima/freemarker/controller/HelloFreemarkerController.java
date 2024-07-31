package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Controller
public class HelloFreemarkerController {


    @GetMapping("/basic")
    public String hello(Model model) {
        model.addAttribute("name","FreeMarker");

        Student student = new Student();
        student.setAge(1);
        student.setName("burtry");
        student.setBirthday(new Date());
        model.addAttribute("stu",student);
        return "basic";
    }
}
