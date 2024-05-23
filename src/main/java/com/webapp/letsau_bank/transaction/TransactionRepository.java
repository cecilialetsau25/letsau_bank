package com.webapp.letsau_bank.transaction;

import com.webapp.letsau_bank.account.Account;
import com.webapp.letsau_bank.account.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findByAccount(Account account);
}
