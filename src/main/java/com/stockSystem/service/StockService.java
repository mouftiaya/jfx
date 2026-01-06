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
        productDAO.delete(product);
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
        transactionDAO.save(transaction);
    }
}
