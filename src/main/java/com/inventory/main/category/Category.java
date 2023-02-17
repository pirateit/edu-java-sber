package com.inventory.main.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventory.main.item.Item;
import com.inventory.main.location.Location;
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
  private String title;

  private String prefix;

  @Column(name = "parent_id")
  private Integer parentId;

  private Integer depth;

  @OneToMany(mappedBy = "category")
  @JsonIgnore
  private Set<Item> items;

  @ManyToOne
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  private Category parent;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  @OrderBy("title ASC")
  private Set<Category> children;

}
