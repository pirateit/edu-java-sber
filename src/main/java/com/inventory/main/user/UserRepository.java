package com.inventory.main.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Iterable<User> findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc();

    Iterable<User> findAllByDeletedAtIsNotNullOrderByLastNameAscFirstNameAsc();

    User findByEmail(String email);

    @Override
    @Modifying
    @Query(value = "UPDATE users SET deleted_at = now(), is_active = false WHERE id = :id ;", nativeQuery = true)
    void deleteById(@Param("id") Integer id);

    @Modifying
    @Query(value = "UPDATE users SET deleted_at = NULL, is_active = true WHERE id = :id ;", nativeQuery = true)
    void restoreById(@Param("id") int id);

}
