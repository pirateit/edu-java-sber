package com.inventory.main.movement;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CoordinationKey implements Serializable {

    @Column(name = "movement_id")
    private Integer movementId;

    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "chief_id")
    private Integer chiefId;

}