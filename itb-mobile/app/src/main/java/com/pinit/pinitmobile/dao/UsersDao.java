package com.pinit.pinitmobile.dao;


import com.pinit.pinitmobile.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersDao {

    public static UsersDao instance = null;
    private List<User> users = new ArrayList<>();

    private UsersDao() {
    }

    public static UsersDao getInstance() {
        if (instance == null) {
            instance = new UsersDao();
        }
        return instance;
    }

    public void addAllUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser(long userId) {
        User user = new User();
        user.setUserId(userId);
        int position = users.indexOf(user);
        if (position != -1)
            return users.get(position);
        else return null;
    }

    public void clear(){
        users.clear();
    }
}
