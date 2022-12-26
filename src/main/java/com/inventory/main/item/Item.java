package com.inventory.main.item;

import com.inventory.main.movement.Movement;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "prefix", "number"
        })
})
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Item {

    @Id
    private Integer id;

    @Size(max = 4)
    private String prefix;

    @NotBlank
    private Long number;

    @NotBlank
    private String title;

    private Integer quantity = 1;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

}
