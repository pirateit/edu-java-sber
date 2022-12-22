package com.inventory.main;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
public class Location {

    private final Integer id;

    @NotBlank
    @Size(min = 2, max = 35)
    private String title;

    private Integer parentId;

    @NotBlank
    private final LocalDate createdAt = LocalDate.now();

    private LocalDate deletedAt;
    private List<User> users;

}
