package com.inventory.main.movement;

import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "coordinations")
@IdClass(CoordinationKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Coordination implements Comparable<Coordination> {

  @Id
  @Column(name = "movement_id", nullable = false)
  private Integer movementId;

  @Id
  @Column(name = "chief_user_id", nullable = false)
  private Integer chiefUserId;

  @Id
  @Column(name = "created_at")
  private final Timestamp createdAt = new Timestamp(new Date().getTime());

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
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
    REFUSED {
      public String toString() {
        return "Отказано";
      }
    }
  }

  public Coordination(Status status, String comment) {
    this.status = status;
    this.comment = comment;
  }

  public Coordination(int movementId, int chiefUserId, Status status, String comment) {
    this.movementId = movementId;
    this.chiefUserId = chiefUserId;
    this.status = status;
    this.comment = comment;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Coordination)) return false;
    Coordination other = (Coordination) o;
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

  @Override
  public int compareTo(Coordination o) {
    if (this.createdAt.after(o.createdAt)) {
      return 1;
    } else if (this.createdAt.before(o.createdAt)) {
      return -1;
    } else {
      return 0;
    }
  }

}
