package com.webapp.letsau_bank.account;

import com.webapp.letsau_bank.user.User;
import org.springframework.data.repository.CrudRepository;


public interface AccountRepository extends CrudRepository<Account, Integer> {
    Account findByCreatedBy(User user);

}
