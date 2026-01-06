package com.stockSystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Type {
        IN, OUT
    }

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer quantity;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product relatedProduct;

    public Transaction() {}

    public Transaction(Type type, Integer quantity, Product relatedProduct) {
        this.type = type;
        this.quantity = quantity;
        this.relatedProduct = relatedProduct;
        this.date = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public Product getRelatedProduct() { return relatedProduct; }
    public void setRelatedProduct(Product relatedProduct) { this.relatedProduct = relatedProduct; }
}
