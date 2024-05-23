package com.webapp.letsau_bank.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired private UserRespository userRespository;

    public void save(User user) {
        userRespository.save(user);
    }

    public User getByUsernameAndPassword(String username, String password) {
        return userRespository.findByUsernameAndPassword(username, password);
    }
}
