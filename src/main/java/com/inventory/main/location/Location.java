package com.inventory.main.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventory.main.movement.Movement;
import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 2, max = 50, message = "Длина названия должна быть от 2 до 50 символов")
    private String title;

    @Column(name = "parent_id")
    private Integer parentId;

    private Integer depth;

    @Column(name = "responsible_user_id")
    private Integer responsibleUserId;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_user_id", insertable = false, updatable = false)
    private User responsibleUser;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<User> users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Location parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("title ASC")

    private Set<Location> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "locationFrom")
    private Set<Movement> movementsFrom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "locationTo")
    private Set<Movement> movementsTo;

}
