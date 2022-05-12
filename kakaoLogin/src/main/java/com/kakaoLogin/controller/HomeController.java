package com.kakaoLogin.controller;


import com.kakaoLogin.service.KaKaoApi;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;


@Controller
public class HomeController {
    
    @Autowired
    private KaKaoApi kakao;
    
    @RequestMapping(value="/")
    public String index() {
        return "index";
    }

    @RequestMapping(value="/login")
    public String login(@RequestParam("code") String code, HttpSession session) {
        System.out.println("code: " + code);
        String access_token = kakao.getAccessToken(code);
        HashMap<String ,Object> userInfo = kakao.getUserInfo(access_token);
        System.out.println("login Controller = " + userInfo);
//        System.out.println("access_token = " + access_token);

        if(userInfo.get("email") != null) {
            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("access_Token", access_token);
        }
        return "index";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        kakao.kakaoLogout((String)session.getAttribute("access_Token"));
        session.removeAttribute("access_Token");
        session.removeAttribute("userId");
        return "logout";
    }
}
