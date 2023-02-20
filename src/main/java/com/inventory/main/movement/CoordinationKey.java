package com.inventory.main.movement;

import lombok.Getter;

import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Getter
public class CoordinationKey implements Serializable {

  private Integer movementId;

  private Integer chiefUserId;

  private final Timestamp createdAt = new Timestamp(new Date().getTime());

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof CoordinationKey)) return false;
    CoordinationKey other = (CoordinationKey) o;
    boolean movementIdEquals = (this.movementId == null && other.movementId == null)
      || (this.movementId != null && this.movementId.equals(other.movementId));
    boolean chiefUserIdEquals = (this.chiefUserId == null && other.chiefUserId == null)
      || (this.chiefUserId != null && this.chiefUserId.equals(other.chiefUserId));
    boolean createdAtEquals = this.createdAt.equals(other.createdAt);
    return movementIdEquals && chiefUserIdEquals && createdAtEquals;
  }

  public final int hashCode() {
    int result = 17;
    if (movementId != null) {
      result = 31 * result + movementId.hashCode();
    }
    if (chiefUserId != null) {
      result = 31 * result + chiefUserId.hashCode();
    }
    if (chiefUserId != null) {
      result = 45 * result + createdAt.hashCode();
    }
    return result;
  }

}
