package com.webapp.letsau_bank.account;

import com.webapp.letsau_bank.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired private AccountRepository accountRepository;

    public void save(Account account) {
        accountRepository.save(account);
    }

    public Account getAccount(User user) {
         Account account = accountRepository.findByCreatedBy(user);

         return account;
    }
}
