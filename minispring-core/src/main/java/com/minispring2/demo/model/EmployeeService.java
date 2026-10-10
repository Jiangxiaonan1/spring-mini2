package com.minispring2.demo.model;

import com.minispring2.annotation.Component;
import com.minispring2.demo.dao.UserDao;

/**
 *
 * @since 2026-10-09 15:11:18
 **/
@Component
public class EmployeeService implements UserInterface{

    /**
     * @return
     */
    @Override
    public String getName() {
        return "获取雇员名字";
    }

    /**
     * @param name
     */
    @Override
    public void setName(String name) {
    }

    /**
     * @param id
     * @return
     */
    @Override
    public UserDao getUser(Long id) {
        UserDao userDao = new UserDao();
        userDao.setId(id);
        userDao.setName("getUser");
        return userDao;
    }

    /**
     * @param userDao
     * @return
     */
    @Override
    public UserDao addUser(UserDao userDao) {
        userDao.setId(88888L);
        return userDao;
    }
}
