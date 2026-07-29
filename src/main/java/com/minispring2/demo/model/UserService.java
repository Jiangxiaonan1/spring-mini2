package com.minispring2.demo.model;

import com.minispring2.demo.dao.UserDao;

/**
 * @description
 * @create 2026-07-28 16:02:15
 **/
public class UserService {
    UserDao UserDao;

    public UserDao getUserDao() {
        return UserDao;
    }

    public void setUserDao(UserDao userDao) {
        UserDao = userDao;
    }
}
