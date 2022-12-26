package com.inventory.main.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserDetailsService userService(UserRepository userRepo) {
        return login -> {
            User user = userRepo.findByEmail(login);

            if (user != null) {
                return user;
            }

            throw new UsernameNotFoundException("Пользователь с указанным Email или номером телефона не найден");
        };
    }

}
