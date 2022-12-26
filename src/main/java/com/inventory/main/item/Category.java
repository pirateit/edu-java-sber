package com.inventory.main.item;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Data
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Category {

    @Id
    private Integer id;

    @NotBlank
    private String name;

    private String prefix;

    @Column(name = "parent_id")
    private Integer parentId;

    private Integer depth;

}
