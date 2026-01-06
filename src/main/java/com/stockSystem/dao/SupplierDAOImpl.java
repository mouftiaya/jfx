package com.stockSystem.dao;

import com.stockSystem.model.Supplier;

public class SupplierDAOImpl extends GenericDAOImpl<Supplier> implements SupplierDAO {
    public SupplierDAOImpl() {
        super(Supplier.class);
    }
}
