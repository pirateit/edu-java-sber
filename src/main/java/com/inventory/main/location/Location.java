package com.inventory.main.location;

import com.inventory.main.item.Item;
import com.inventory.main.movement.Movement;
import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

    @Column(name = "parent_id")
    private Integer parentId;

    private Integer depth;

    @Column(name = "responsible_user_id")
    private Integer responsibleUserId;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_user_id", insertable = false, updatable = false)
    private User responsibleUser;

    @OneToMany(mappedBy = "location")
    private Set<Item> items;

    @OneToMany(mappedBy = "locationFrom")
    private Set<Movement> movementsFrom;

    @OneToMany(mappedBy = "locationTo")
    private Set<Movement> movementsTo;

}
