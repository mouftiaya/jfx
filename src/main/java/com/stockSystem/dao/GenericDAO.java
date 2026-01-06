package com.stockSystem.dao;

import java.util.List;

public interface GenericDAO<T> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    T findById(Long id);
    List<T> findAll();
}
