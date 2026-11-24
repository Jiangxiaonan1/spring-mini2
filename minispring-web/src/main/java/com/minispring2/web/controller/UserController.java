package com.minispring2.web.controller;

import com.minispring2.annotation.Autowired;
import com.minispring2.demo.dao.UserDao;
import com.minispring2.demo.model.EmployeeService;
import com.minispring2.demo.model.UserInterface;
import com.minispring2.demo.model.UserService;
import com.minispring2.web.annotation.*;
import com.minispring2.web.core.RequestMethod;

/**
 *
 * curl.exe http://localhost:8080/user/hello
 * curl.exe "http://localhost:8080/user/getUser?userId=12345"
 * curl.exe "http://localhost:8080/user/getEmployee?userId=12345"
 * curl.exe -X POST http://localhost:8080/user/addUser -H "Content-Type: application/json" -d '{\"name\":\"Jerry\",\"age\":19}'
 * @since 2026-09-09 17:21:17
 **/
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserInterface userService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserController() {
    }

    public UserInterface getUserService() {
        return userService;
    }

    public void setUserService(UserInterface userService) {
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

    @RequestMapping("/getEmployee")
    public String getEmployee(@RequestParam("userId") Long userId) {
        System.out.println("调用getEmployee方法");
        return employeeService.getUser(userId).toString();
    }
}
