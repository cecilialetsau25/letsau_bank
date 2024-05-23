package com.webapp.letsau_bank.account;

import com.webapp.letsau_bank.account.Transaction;
import com.webapp.letsau_bank.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;  // Assume you have this interface for database operations

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
