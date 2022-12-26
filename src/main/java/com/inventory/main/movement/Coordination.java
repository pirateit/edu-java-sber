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

    @Id
    private CoordinationKey id;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @ManyToOne
    @MapsId("movementId")
    @JoinColumn(name = "movement_id")
    private Movement movement;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private User subject;

}
