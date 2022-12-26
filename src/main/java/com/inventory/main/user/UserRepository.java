package com.inventory.main.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Iterable<User> findAll();

    Optional<User> findById(Integer id);

    User findByEmail(String login);

    User save(User user);

}
