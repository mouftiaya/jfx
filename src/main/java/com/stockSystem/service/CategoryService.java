package com.stockSystem.service;

import com.stockSystem.dao.CategoryDAO;
import com.stockSystem.dao.CategoryDAOImpl;
import com.stockSystem.model.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAOImpl();
    }

    public void save(Category category) {
        if (category.getId() == null) {
            categoryDAO.save(category);
        } else {
            categoryDAO.update(category);
        }
    }

    public void delete(Category category) {
        categoryDAO.delete(category);
    }

    public List<Category> findAll() {
        return categoryDAO.findAll();
    }
}
