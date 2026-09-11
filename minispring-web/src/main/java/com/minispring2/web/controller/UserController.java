package com.minispring2.web.controller;

import com.minispring2.web.annotation.Controller;
import com.minispring2.web.annotation.RequestMapping;

/**
 *
 * @since 2026-09-09 17:21:17
 **/
@Controller
public class UserController {

    @RequestMapping("/hello")
    public String hello () {
        return "hello";
    }
}
