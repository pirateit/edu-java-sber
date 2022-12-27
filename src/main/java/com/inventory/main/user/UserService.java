package com.inventory.main.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }



}
