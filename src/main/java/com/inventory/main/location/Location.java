package com.inventory.main.location;

import com.inventory.main.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Table(name = "locations")
public class Location {

    @Id
    private Integer id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

    private Integer parentId;

    private Integer responsibleUserId;

    @NotBlank
    private Timestamp createdAt = new Timestamp(new Date().getTime());

    private Timestamp deletedAt;
//    private List<User> users;

    public Location() { }

    public Location(
            Integer id,
            String title,
            Integer parentId,
            Integer responsibleUserId,
            Timestamp createdAt,
            Timestamp deletedAt) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.responsibleUserId = responsibleUserId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

}
