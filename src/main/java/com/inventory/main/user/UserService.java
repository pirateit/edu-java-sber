package com.inventory.main.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService {

    public UserService userService(UserRepository userRepo) {
        return login -> {
            User user = userRepo.findByEmailOrPhone(login);

            if (user != null) {
                return user;
            }

            throw new UsernameNotFoundException("Пользователь с Email или номером телефона " + login + " не найден");
        };
    }

}
