package com.stockSystem.service;

import com.stockSystem.dao.ProductDAO;
import com.stockSystem.dao.ProductDAOImpl;
import com.stockSystem.dao.TransactionDAO;
import com.stockSystem.dao.TransactionDAOImpl;
import com.stockSystem.model.Product;
import com.stockSystem.model.Transaction;

import java.util.List;

public class StockService {
    private final ProductDAO productDAO;
    private final TransactionDAO transactionDAO;

    public StockService() {
        this.productDAO = new ProductDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
    }

    public void addProduct(Product product) {
        productDAO.save(product);
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public void removeProduct(Product product) {
        try {
            // Get the product from database to ensure it's managed
            Product productToDelete = productDAO.findById(product.getId());
            if (productToDelete == null) {
                throw new RuntimeException("Product not found");
            }
            
            // First, delete all transactions associated with this product
            List<Transaction> transactions = transactionDAO.findAll();
            for (Transaction transaction : transactions) {
                if (transaction.getRelatedProduct() != null && 
                    transaction.getRelatedProduct().getId() != null &&
                    transaction.getRelatedProduct().getId().equals(productToDelete.getId())) {
                    transactionDAO.delete(transaction);
                }
            }
            
            // Remove product from category and supplier relationships
            productToDelete.setCategory(null);
            productToDelete.setSupplier(null);
            productDAO.update(productToDelete);
            
            // Then delete the product
            productDAO.delete(productToDelete);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }

    public void recordTransaction(Product product, Transaction.Type type, int quantity) {
        if (type == Transaction.Type.OUT) {
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - quantity);
        } else {
            product.setQuantity(product.getQuantity() + quantity);
        }

        // Update product stock
        productDAO.update(product);

        // Record transaction
        Transaction transaction = new Transaction(type, quantity, product);
        transaction.setStatus(Transaction.Status.COMPLETED); // Set default status
        transactionDAO.save(transaction);
    }
}
