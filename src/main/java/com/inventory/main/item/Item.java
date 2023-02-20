package com.inventory.main.item;

import com.inventory.main.category.Category;
import com.inventory.main.location.Location;
import com.inventory.main.movement.Movement;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "items", uniqueConstraints = { @UniqueConstraint(columnNames = { "prefix", "number" })
})
@SQLDelete(sql = "UPDATE items SET deleted_at = NOW() WHERE id = ? ;")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Size(max = 4)
  private String prefix = "";

  @NotNull(message = "Обязательное поле")
  private Long number;

  @NotBlank(message = "Обязательное поле")
  private String title;

  private Integer quantity = 1;

  @Column(name = "category_id")
  private Integer categoryId;

  @Column(name = "location_id")
  private Integer locationId;

  @Transient
  @Column(name = "created_at")
  private final Timestamp createdAt = new Timestamp(new Date().getTime());

  @Column(name = "deleted_at")
  private Timestamp deletedAt;

  @ManyToOne
  @JoinColumn(name = "category_id", insertable = false, updatable = false)
  private Category category;

  @ManyToOne
  @JoinColumn(name = "location_id", insertable = false, updatable = false)
  private Location location;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
  @OrderBy("created_at ASC")
  private Set<Movement> movements;

  public Item(String prefix, long number, String title, int quantity, int categoryId, int locationId) {
    this.prefix = prefix;
    this.number = number;
    this.title = title;
    this.quantity = quantity;
    this.categoryId = categoryId;
    this.locationId = locationId;
  }

}
