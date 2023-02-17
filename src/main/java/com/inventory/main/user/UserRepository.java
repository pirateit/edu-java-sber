package com.inventory.main.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  Set<User> findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc();

  Set<User> findAllByDeletedAtIsNotNullOrderByLastNameAscFirstNameAsc();

  Set<User> findByLocationIdInAndDeletedAtIsNullOrderByLastNameAscFirstNameAsc(Set<Integer> locations);

  @Query(value = "SELECT * FROM users WHERE email = :login OR phone = :login ;", nativeQuery = true)
  User findByEmailOrPhone(@Param("login") String login);

  @Modifying
  @Query(value = "UPDATE users SET deleted_at = NULL, is_active = true WHERE id = :id ;", nativeQuery = true)
  void restoreById(@Param("id") int id);

}
