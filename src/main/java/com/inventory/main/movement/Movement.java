package com.inventory.main.movement;

import com.inventory.main.item.Item;
import com.inventory.main.location.Location;
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

    @Enumerated(EnumType.STRING)
    @NotBlank
    private Type type;

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

    @Column(name = "requested_user_id")
    @NotBlank
    private Integer requestedUserId;

    @Column
    @Enumerated(EnumType.STRING)
    @NotBlank
    private Status status = Status.UNDER_APPROVAL;

    private String comment;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @OneToMany(mappedBy = "movement")
    private Set<Coordination> coordinations;

    @ManyToOne
    @JoinColumn(name = "requested_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "location_from")
    private Location LocationFrom;

    @OneToOne
    @JoinColumn(name = "location_to")
    private Location LocationTo;

    public enum Type {
        MOVEMENT, WRITE_OFF
    }

    public enum Status {
        UNDER_APPROVAL, SENT, ACCEPTED, CANCELLED
    }

}
