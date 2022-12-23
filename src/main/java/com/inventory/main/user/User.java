package com.inventory.main.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Table("users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class User {

    @Id
    private Integer id;

    @Column("last_name")
    @NotBlank
    @Size(min = 2, max = 35)
    private String lastName;

    @Column("first_name")
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

    @Column("location_id")
    @NotBlank
    private Integer locationId;

    @Column("is_active")
    private Boolean isActive = true;

    @Column("created_at")
    @NotBlank
    private LocalDate createdAt = LocalDate.now();

    @Column("deleted_at")
    private LocalDate deletedAt;

}
