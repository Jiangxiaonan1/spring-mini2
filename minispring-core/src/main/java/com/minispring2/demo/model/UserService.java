package com.minispring2.demo.model;

import com.minispring2.annotation.Component;
import com.minispring2.demo.dao.UserDao;

/**
 * 演示用的用户服务 Bean，通过属性注入依赖 {@link com.minispring2.demo.dao.UserDao}。
 *
 * @since 2026-07-28
 */
@Component
public class UserService implements UserInterface {

    private String name;

    private UserDao UserDao;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDao getUserDao() {
        return UserDao;
    }

    public void setUserDao(UserDao userDao) {
        UserDao = userDao;
    }

    public UserDao getUser(Long id) {
        UserDao userDao = new UserDao();
        userDao.setId(id);
        userDao.setName("Tom");
        userDao.setAge(20);
        return userDao;
    }

    public UserDao addUser(UserDao userDao) {
        userDao.setId(19787263L);
        return userDao;
    }
}

