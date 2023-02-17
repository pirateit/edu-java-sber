package com.inventory.main.user;

import com.inventory.main.location.Location;
import com.inventory.main.movement.Coordination;
import com.inventory.main.movement.Movement;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_active = false, deleted_at = NOW() WHERE id = ? ;")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "last_name")
  @NotBlank(message = "Обязательное поле")
  @Size(min = 2, max = 35, message = "Длина от 2 до 35 символов")
  private String lastName;

  @Column(name = "first_name")
  @NotBlank(message = "Обязательное поле")
  @Size(min = 2, max = 35, message = "Длина от 2 до 35 символов")
  private String firstName;

  @Column(nullable = false, unique = true)
  @NotBlank(message = "Обязательное поле")
  @Email(message = "Некорректный формат")
  private String email;

  @Size(max=11, min=11, message = "Номер должен состоять из 11 цифр")
  @Column(unique = true)
  private String phone;

  @Column(nullable = false)
  private String password;

  @Column(name = "location_id", nullable = false)
  @NotNull(message = "Обязательное поле")
  private Integer locationId;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @Transient
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

  @ManyToOne
  @JoinColumn(name = "location_id", insertable = false, updatable = false)
  private Location location;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "responsibleUser")
  private Set<Location> locations;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "chief")
  private Set<Coordination> coordinations;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
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
