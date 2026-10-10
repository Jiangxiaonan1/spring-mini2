package com.minispring2.demo.model;

import com.minispring2.demo.dao.UserDao;

public interface UserInterface {
    String getName();
    void setName(String name);

    default UserDao getUser(Long id) {
        return null;
    };

    default UserDao addUser(UserDao userDao) {
        return null;
    };
}
