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

}
