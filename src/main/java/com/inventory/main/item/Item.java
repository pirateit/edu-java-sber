package com.inventory.main.item;

import com.inventory.main.location.Location;
import com.inventory.main.movement.Movement;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "prefix", "number"
        })
})
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 4)
    private String prefix;

    @NotBlank
    private Long number;

    @NotBlank
    private String title;

    private Integer quantity = 1;

//    @Column(name = "category_id")
//    private Integer categoryId;

//    @Column(name = "location_id")
//    private Integer locationId;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "item")
    private Set<Movement> movements;

}
