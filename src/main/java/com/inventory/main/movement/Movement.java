package com.inventory.main.movement;

import com.inventory.main.item.Item;
import com.inventory.main.location.Location;
import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "movements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;

    @Column(name = "item_id")
    private Integer itemId;

    private Integer quantity = 1;

    @Column(name = "location_from_id")
    private Integer locationFromId;

    @Column(name = "location_to_id")
    @NotNull
    private Integer locationToId;

    @Column(name = "requested_user_id")
    private Integer requestedUserId;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status = Status.UNDER_APPROVAL;

    private String comment;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "movement")
    private Set<Coordination> coordinations;

    @ManyToOne
    @JoinColumn(name = "requested_user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_from_id", insertable = false, updatable = false)
    private Location locationFrom;

    @ManyToOne
    @JoinColumn(name = "location_to_id", insertable = false, updatable = false)
    private Location locationTo;

    public enum Type {
        MOVEMENT {
            public String toString() {
                return "Перемещение";
            }
        },
        WRITE_OFF {
            public String toString() {
                return "Списание";
            }
        }
    }

    public enum Status {
        UNDER_APPROVAL {
            public String toString() {
                return "На согласовании";
            }
        },
        SENT {
            public String toString() {
                return "Отправлено";
            }
        },
        ACCEPTED {
            public String toString() {
                return "Принято";
            }
        },
        CANCELLED {
            public String toString() {
                return "Отменено";
            }
        }
    }

}
