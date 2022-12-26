package com.inventory.main.location;

import com.inventory.main.user.User;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Location {

    @Id
    private Integer id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

    @Column(name = "parent_id")
    private Integer parentId;

    private Integer depth;

    @Column(name = "responsible_user_id")
    private Integer responsibleUserId;

    @Column(name = "created_at")
    private final Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    private List<User> users;

}
