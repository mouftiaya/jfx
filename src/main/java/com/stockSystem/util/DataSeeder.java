package com.stockSystem.util;

import com.stockSystem.dao.GenericDAOImpl;
import com.stockSystem.model.Category;
import com.stockSystem.model.Product;
import com.stockSystem.model.Supplier;
import java.math.BigDecimal;
import java.util.List;

public class DataSeeder {
    public static void seed() {
        GenericDAOImpl<Category> categoryDAO = new GenericDAOImpl<>(Category.class);
        GenericDAOImpl<Supplier> supplierDAO = new GenericDAOImpl<>(Supplier.class);
        GenericDAOImpl<Product> productDAO = new GenericDAOImpl<>(Product.class);

        if (!productDAO.findAll().isEmpty()) {
            System.out.println("Database already seeded.");
            return;
        }

        System.out.println("Seeding database with initial data...");

        // Create Categories
        Category electronics = new Category("Electronics", "Gadgets and devices");
        Category furniture = new Category("Furniture", "Office furniture");
        categoryDAO.save(electronics);
        categoryDAO.save(furniture);

        // Create Suppliers
        Supplier techCorp = new Supplier("TechCorp", "contact@techcorp.com");
        Supplier officeWorld = new Supplier("OfficeWorld", "sales@officeworld.com");
        supplierDAO.save(techCorp);
        supplierDAO.save(officeWorld);

        // Create Products
        Product p1 = new Product("Gaming Laptop", "LAP-001", new BigDecimal("800.00"), new BigDecimal("1200.00"), 10);
        p1.setCategory(electronics);
        p1.setSupplier(techCorp);
        productDAO.save(p1);

        Product p2 = new Product("Wireless Mouse", "MOU-002", new BigDecimal("15.00"), new BigDecimal("35.00"), 50);
        p2.setCategory(electronics);
        p2.setSupplier(techCorp);
        productDAO.save(p2);

        Product p3 = new Product("Office Chair", "CHR-003", new BigDecimal("80.00"), new BigDecimal("150.00"), 20);
        p3.setCategory(furniture);
        p3.setSupplier(officeWorld);
        productDAO.save(p3);

        System.out.println("Seeding completed.");
    }
}
