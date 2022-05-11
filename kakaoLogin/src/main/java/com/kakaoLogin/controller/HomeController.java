package com.kakaoLogin.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class HomeController {

    @RequestMapping(value="/")
    public String index() {
        return "index";
    }

    // login
//    @RequestMapping(value="/login")
//    public String login(@RequestParam("code") String code) {
//        System.out.println("code: " + code);
//        return "index";
//    }

    @RequestMapping(value="/login")
    public String login() {
        return "index";
    }

}
