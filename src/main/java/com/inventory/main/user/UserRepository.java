package com.inventory.main.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Iterable<User> findAll();

    Optional<User> findById(Integer id);

    User findByEmailOrPhone(Object login);

    User save(User user);

}
