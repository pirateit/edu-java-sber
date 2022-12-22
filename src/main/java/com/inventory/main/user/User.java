package com.inventory.main.user;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class User {

    @Id
    private final Integer id;

    @NotBlank
    @Size(min = 2, max = 35)
    private String lastName;

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

    @NotBlank
    private Integer locationId;

    @NotBlank
    private LocalDate createdAt = LocalDate.now();

    private LocalDate deletedAt;

}
