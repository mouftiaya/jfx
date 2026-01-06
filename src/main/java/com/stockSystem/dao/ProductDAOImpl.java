package com.stockSystem.dao;

import com.stockSystem.model.Product;

public class ProductDAOImpl extends GenericDAOImpl<Product> implements ProductDAO {
    public ProductDAOImpl() {
        super(Product.class);
    }
}
