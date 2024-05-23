package com.webapp.letsau_bank.account;

import com.webapp.letsau_bank.user.User;
import jakarta.persistence.*;
import com.webapp.letsau_bank.account.Transaction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String accountNumber;

    private String accountType;

    private Double balance;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User createdBy;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Account() {

    }

    public Account(String accountNumber, int id, String accountType, Double balance, User createdBy) {
        this.accountNumber = accountNumber;
        this.id = id;
        this.accountType = accountType;
        this.balance = balance;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return createdBy;
    }

    public void setUser(User createdBy) {
        this.createdBy = createdBy;
    }
}
