package com.stockSystem.service;

import com.stockSystem.dao.CategoryDAO;
import com.stockSystem.dao.CategoryDAOImpl;
import com.stockSystem.dao.ProductDAO;
import com.stockSystem.dao.ProductDAOImpl;
import com.stockSystem.model.Category;
import com.stockSystem.model.Product;

import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;
    private final ProductDAO productDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    public void save(Category category) {
        if (category.getId() == null) {
            categoryDAO.save(category);
        } else {
            categoryDAO.update(category);
        }
    }

    public void delete(Category category) {
        // First, remove category reference from all products that use this category
        List<Product> products = productDAO.findAll();
        products.stream()
            .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(category.getId()))
            .forEach(product -> {
                product.setCategory(null);
                productDAO.update(product);
            });
        
        // Then delete the category
        categoryDAO.delete(category);
    }

    public List<Category> findAll() {
        return categoryDAO.findAll();
    }
}
