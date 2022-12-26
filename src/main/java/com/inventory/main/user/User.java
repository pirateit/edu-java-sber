package com.inventory.main.user;

import com.inventory.main.movement.Movement;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    private Integer id;

    @Column(name = "last_name")
    @NotBlank
    @Size(min = 2, max = 35)
    private String lastName;

    @Column(name = "first_name")
    @NotBlank
    @Size(min = 2, max = 35)
    private String firstName;

    @NotBlank
    @Size(min = 5, max = 100)
    private String email;

    @Digits(integer = 11, fraction = 0)
    private Long phone;

    @NotBlank
    private String password;

    @Column(name = "location_id")
    @NotBlank
    private Integer locationId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column
    private Role role = Role.USER;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return this.isActive;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return this.isActive;
//    }

    @ManyToMany
    @JoinTable(
            name = "coordinations",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "movement_id")
    )
    Set<Movement> movements;

    public enum Role {
        OWNER, ADMIN, USER
    }

}
