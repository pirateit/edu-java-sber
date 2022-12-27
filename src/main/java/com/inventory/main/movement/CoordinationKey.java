package com.inventory.main.movement;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Embeddable
public class CoordinationKey implements Serializable {

    @Column(name = "movement_id")
    private Integer movementId;

    @Column(name = "chief_user_id")
    private Integer chiefUserId;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CoordinationKey))
            return false;
        CoordinationKey other = (CoordinationKey) o;
        return (this.movementId != null && this.movementId.equals(other.movementId)
                || (this.chiefUserId != null && this.chiefUserId.equals(other.chiefUserId))
                || (this.createdAt != null && this.createdAt.equals(other.createdAt)));
    }

    // TODO: make this method
//    @Override
//    public int hashCode() {
//
//    }

}
