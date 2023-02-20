package com.inventory.main.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventory.main.item.Item;
import com.inventory.main.movement.Movement;
import com.inventory.main.user.User;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "locations")
@SQLDelete(sql = "UPDATE locations SET deleted_at = NOW() WHERE id = ? ;")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Обязательное поле")
  @Size(min = 2, max = 50, message = "Длина названия должна быть от 2 до 50 символов")
  private String title;

  @Column(name = "parent_id")
  private Integer parentId;

  private Integer depth;

  @Column(name = "responsible_user_id")
  private Integer responsibleUserId;

  @Column(name = "deleted_at")
  private Timestamp deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "responsible_user_id", insertable = false, updatable = false)
  private User responsibleUser;

  @OneToMany(mappedBy = "location")
  @JsonIgnore
  private Set<User> users;

  @OneToMany(mappedBy = "location")
  @JsonIgnore
  private Set<Item> items;

  @ManyToOne
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  private Location parent;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  @OrderBy("title ASC")
  private Set<Location> children;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "locationFrom")
  private Set<Movement> movementsFrom;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "locationTo")
  private Set<Movement> movementsTo;

}
