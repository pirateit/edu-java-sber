package com.inventory.main.movement;

import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Data
@Table(name = "movements")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Movement {

    @Id
    private Integer id;

    @Column(name = "item_id")
    @NotBlank
    private Integer itemId;

    @Column
    @NotBlank
    private Integer quantity;

    @Column(name = "location_from")
    @NotBlank
    private Integer locationFrom;

    @Column(name = "location_to")
    @NotBlank
    private Integer locationTo;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @ManyToMany
    @JoinTable(
            name = "coordinations",
            joinColumns = @JoinColumn(name = "movement_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    Set<User> users;

}
