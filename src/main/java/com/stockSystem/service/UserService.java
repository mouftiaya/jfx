package com.stockSystem.service;

import com.stockSystem.dao.UserDAO;
import com.stockSystem.dao.UserDAOImpl;
import com.stockSystem.model.User;

import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    public void save(User user) {
        if (user.getId() == null) {
            userDAO.save(user);
        } else {
            userDAO.update(user);
        }
    }

    public void delete(User user) {
        userDAO.delete(user);
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    // Placeholder for authentication Logic
    public User authenticate(String username, String password) {
        // Simple check for now since we haven't implemented hashing or complex queries yet in specific DAO
        // In a real app, use UserDAO.findByUsername(username)
        List<User> users = userDAO.findAll();
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void createDefaultAdminIfNoUsers() {
        if (userDAO.findAll().isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin"); // In real app, hash this
            admin.setRole("ADMIN");
            save(admin);
            System.out.println("Default Admin created: admin/admin");
        }
    }
}
