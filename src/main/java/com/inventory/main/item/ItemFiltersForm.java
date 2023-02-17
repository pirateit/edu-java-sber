package com.inventory.main.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ItemFiltersForm {

  private String title;
  private Set<Integer> locations;
  private Set<Integer> categories;

  ItemFiltersForm(String title, Set<Integer> locations, Set<Integer> categories) {
    this.title = title;
    this.locations = locations;
    this.categories = categories;
  }

  String toQueryString() {
    List<String> queryList = new ArrayList<>();

    if (title != null && !title.isBlank()) {
      queryList.add("title=" + title);
    }

    if (locations != null && locations.size() > 0) {
      queryList.add("locations=" + String.join(",", locations.stream().map(Object::toString).collect(Collectors.toSet())));
    }

    if (categories != null && categories.size() > 0) {
      queryList.add("categories=" + String.join(",", categories.stream().map(Object::toString).collect(Collectors.toSet())));
    }

    return String.join("&", queryList.stream().map(Object::toString).collect(Collectors.toSet()));
  }

}
