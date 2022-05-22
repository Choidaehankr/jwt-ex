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
        // Access Token get
        String access_token = kakao.getAccessToken(code);
        // 사용자 정보 get
        HashMap<String ,Object> userInfo = kakao.getUserInfo(access_token);
        System.out.println("login Controller = " + userInfo);
        System.out.println("access_token = " + access_token);
        
//        System.out.println("access_token = " + access_token);
        // 아래에 원하는 사용자 정보를 session.setAttribute 로 저장..
        if(userInfo.get("email") != null) {
            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("nick_name", userInfo.get("nickname"));
            // 로그아웃 처리 시 사용할 토큰
            session.setAttribute("access_Token", access_token);
        }
//        System.out.println("session = " + session);
        return "index";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        kakao.kakaoLogout((String)session.getAttribute("access_Token"));
        session.removeAttribute("access_Token");
        session.removeAttribute("userId");
        System.out.println("로그아웃 성공!");
        return "index";
    }
}
