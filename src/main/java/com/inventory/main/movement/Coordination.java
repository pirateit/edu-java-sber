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
public class Coordination {

  @Id
  @Column(name = "movement_id", nullable = false)
  private int movementId;

  @Id
  @Column(name = "chief_user_id", nullable = false)
  private int chiefUserId;

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

  public Coordination(int movementId, int chiefUserId, Status status, String comment) {
    this.movementId = movementId;
    this.chiefUserId = chiefUserId;
    this.status = status;
    this.comment = comment;
  }

}
