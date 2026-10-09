package com.minispring2.web.controller;

import com.minispring2.annotation.Autowired;
import com.minispring2.demo.dao.UserDao;
import com.minispring2.demo.model.UserService;
import com.minispring2.web.annotation.*;
import com.minispring2.web.core.RequestMethod;

/**
 *
 * @since 2026-09-09 17:21:17
 **/
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserController() {
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/hello", requestMethod = RequestMethod.GET)
    public String hello () {
        userService.setName(" world");
        return "hello" + userService.getName();
    }

    @RequestMapping("/getUser")
    public String getUser(@RequestParam("userId") Long userId) {
        return userService.getUser(userId).toString();
    }

    @RequestMapping("/getUserByPath/{id}")
    public String getUserWithPath(@PathVariable Long userId) {
        return userService.getUser(userId).toString();
    }

    @RequestMapping(value = "/addUser", requestMethod = RequestMethod.POST)
    public UserDao addUser(@RequestBody UserDao userDao) {
        return userService.addUser(userDao);
    }
}
