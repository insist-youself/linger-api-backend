package com.yupi.lingerinterface.controller;
import com.yupi.lingerapiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 * @author linger
 * @date 2024/2/12 22:12
 */

@RestController()
public class NameController {
//    @GetMapping("/get")
//    public String getNameByGet(String name, HttpServletRequest request) {
//        System.out.println(request.getHeader("yupi"));
//        return "GET 你的名字是" + name;
//    }
//
//    @PostMapping("/post")
//    public String getNameByPost(@RequestParam String name) {
//        return "POST 你的名字是" + name;
//    }

    @PostMapping("/api/name/user")
    public String getUserNameByPost(@RequestBody User use, HttpServletRequest request) {
        return "POST 用户名字是" + use.getUsername();
    }
}
