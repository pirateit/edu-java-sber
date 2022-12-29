package com.inventory.main.movement;

import com.inventory.main.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "coordinations")
@AllArgsConstructor
@NoArgsConstructor
public class Coordination {

    @EmbeddedId
    private CoordinationKey id;

    @ManyToOne
    @JoinColumn(name = "movement_id", insertable = false, updatable = false)
    private Movement movement;

    @ManyToOne
    @JoinColumn(name = "chief_user_id", insertable = false, updatable = false)
    private User chief;

}
