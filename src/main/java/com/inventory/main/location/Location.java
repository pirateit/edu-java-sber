package com.inventory.main.location;

import com.inventory.main.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "locations")
@AllArgsConstructor
// TODO: What is "force" flag?
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Location {

    @Id
    private Integer id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

    @Column("parent_id")
    private Integer parentId;

    @Column("responsible_user_id")
    private Integer responsibleUserId;

    @Column("created_at")
    @NotBlank
    private Timestamp createdAt = new Timestamp(new Date().getTime());

    @Column("deleted_at")
    private Timestamp deletedAt;

    private List<User> users;

//    public Location(
//            Integer id,
//            String title,
//            Integer parentId,
//            Integer responsibleUserId,
//            Timestamp createdAt,
//            Timestamp deletedAt) {
//        this.id = id;
//        this.title = title;
//        this.parentId = parentId;
//        this.responsibleUserId = responsibleUserId;
//        this.createdAt = createdAt;
//        this.deletedAt = deletedAt;
//    }

}
