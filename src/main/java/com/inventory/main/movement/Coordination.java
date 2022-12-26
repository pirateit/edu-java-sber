package com.inventory.main.movement;

import com.inventory.main.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "coordinations")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Coordination {

    @Column(name = "movement_id")
    private Integer movementId;

    @Column(name = "chief_user_id")
    private Integer chiefUserId;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @ManyToOne
    @JoinColumn(name = "movement_id")
    private Movement movement;

    @ManyToOne
    @JoinColumn(name = "chief_id")
    private User chief;

}
