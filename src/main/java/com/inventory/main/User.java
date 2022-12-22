package com.inventory.main;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class User {

    private final Integer id;

    @NotBlank
    @Size(min = 2, max = 35)
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 35)
    private String firstName;

    @NotBlank
    @Size(min = 5, max = 62)
    private String email;

    @NotBlank
    @Digits(integer = 11, fraction = 0)
    private Long phone;

    @NotBlank
    private String password;

    @NotBlank
    private Integer locationId;

    @NotBlank
    private final LocalDate createdAt = LocalDate.now();

    private LocalDate deletedAt;

}
