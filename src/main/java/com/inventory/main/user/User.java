package com.inventory.main.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventory.main.location.Location;
import com.inventory.main.movement.Coordination;
import com.inventory.main.movement.Movement;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private String password;

    @Column(name = "location_id")
    @NotNull
    private Integer locationId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActive && this.deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", insertable = false, updatable = false)
    private Location location;

    @OneToMany(mappedBy = "responsibleUser")
    @JsonIgnore
    private Set<Location> locations;

    @OneToMany(mappedBy = "chief")
    private Set<Coordination> coordinations;

    @OneToMany(mappedBy = "user")
    private Set<Movement> movements;

    public enum Role {
        OWNER {
            public String toString() {
                return "Владелец";
            }
        },
        ADMIN {
            public String toString() {
                return "Администратор";
            }
        },
        USER {
            public String toString() {
                return "Пользователь";
            }
        }
    }

}
