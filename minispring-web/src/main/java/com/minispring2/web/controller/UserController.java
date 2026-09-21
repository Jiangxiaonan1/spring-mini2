package com.minispring2.web.controller;

import com.minispring2.annotation.Autowired;
import com.minispring2.demo.dao.UserDao;
import com.minispring2.demo.model.UserService;
import com.minispring2.web.annotation.Controller;
import com.minispring2.web.annotation.RequestBody;
import com.minispring2.web.annotation.RequestMapping;
import com.minispring2.web.annotation.RequestParam;

/**
 *
 * @since 2026-09-09 17:21:17
 **/
@Controller
public class UserController {

    @Autowired
    UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/hello")
    public String hello () {
        userService.setName(" world");
        return "hello" + userService.getName();
    }

    @RequestMapping("/getUser")
    public String getUser(@RequestParam("userId") Long userId) {
        return userService.getUser(userId).toString();
    }

    @RequestMapping("/addUser")
    public UserDao addUser(@RequestBody UserDao userDao) {
        return userService.addUser(userDao);
    }
}
