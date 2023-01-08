package com.inventory.main.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    private String prefix;

    @Column(name = "parent_id")
    private Integer parentId;

    private Integer depth;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Item> items;

}
