package com.stockSystem.dao;

import com.stockSystem.model.User;

public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO {
    public UserDAOImpl() {
        super(User.class);
    }
}
