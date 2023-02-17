package com.inventory.main.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class MovementFiltersForm {

  private Movement.Type type;
  private String itemTitle;
  private Set<Integer> locationsFromId;
  private Set<Integer> locationsToId;
  private Integer requestedUserId;
  private String status;

  MovementFiltersForm(Movement.Type type, String itemTitle, Set<Integer> locationsFromId, Set<Integer> locationsToId, Integer requestedUserId, String status) {
    this.type = type;
    this.itemTitle = itemTitle;
    this.locationsFromId = locationsFromId;
    this.locationsToId = locationsToId;
    this.requestedUserId = requestedUserId;
    this.status = status;
  }

  String toQueryString() {
    List<String> queryList = new ArrayList<>();

    if (type != null) {
      queryList.add("type=" + type.name());
    }

    if (itemTitle != null && !itemTitle.isBlank()) {
      queryList.add("title=" + itemTitle);
    }

    if (locationsFromId != null && locationsFromId.size() > 0) {
      queryList.add("locationsFrom=" + String.join(",", locationsFromId.stream().map(Object::toString).collect(Collectors.toSet())));
    }

    if (locationsToId != null && locationsToId.size() > 0) {
      queryList.add("locationsTo=" + String.join(",", locationsToId.stream().map(Object::toString).collect(Collectors.toSet())));
    }

    if (requestedUserId != null) {
      queryList.add("requestedUser=" + requestedUserId);
    }

    if (status != null && !status.isBlank()) {
      queryList.add("status=" + status);
    }

    return String.join("&", queryList.stream().map(Object::toString).collect(Collectors.toSet()));
  }

}
