package com.inventory.main.movement;

import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "coordinations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coordination {

    @EmbeddedId
    private CoordinationKey id;

    private Status status = Status.WAITING;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "movement_id", insertable = false, updatable = false)
    private Movement movement;

    @ManyToOne
    @JoinColumn(name = "chief_user_id", insertable = false, updatable = false)
    private User chief;

    public enum Status {
        WAITING {
            public String toString() {
                return "В ожидании";
            }
        },
        COORDINATED {
            public String toString() {
                return "Согласовано";
            }
        },
        REFUSED {
            public String toString() {
                return "Отказано";
            }
        }

    }

}
