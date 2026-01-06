package com.stockSystem.service;

import com.stockSystem.dao.SupplierDAO;
import com.stockSystem.dao.SupplierDAOImpl;
import com.stockSystem.model.Supplier;

import java.util.List;

public class SupplierService {
    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAOImpl();
    }

    public void save(Supplier supplier) {
        if (supplier.getId() == null) {
            supplierDAO.save(supplier);
        } else {
            supplierDAO.update(supplier);
        }
    }

    public void delete(Supplier supplier) {
        supplierDAO.delete(supplier);
    }

    public List<Supplier> findAll() {
        return supplierDAO.findAll();
    }
}
