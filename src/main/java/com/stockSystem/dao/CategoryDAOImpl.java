package com.stockSystem.dao;

import com.stockSystem.model.Category;

public class CategoryDAOImpl extends GenericDAOImpl<Category> implements CategoryDAO {
    public CategoryDAOImpl() {
        super(Category.class);
    }
}
