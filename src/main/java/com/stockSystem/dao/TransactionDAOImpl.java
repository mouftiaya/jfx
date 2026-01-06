package com.stockSystem.dao;

import com.stockSystem.model.Transaction;

public class TransactionDAOImpl extends GenericDAOImpl<Transaction> implements TransactionDAO {
    public TransactionDAOImpl() {
        super(Transaction.class);
    }
}
