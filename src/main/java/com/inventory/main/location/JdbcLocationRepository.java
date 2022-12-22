package com.inventory.main.location;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLocationRepository implements LocationRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcLocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Location> findAll() {
        return jdbcTemplate.query(
                "SELECT id, title, parent_id, created_at, deleted_at FROM locations;",
                this::mapRowToLocation
        );
    }

    @Override
    public Optional<Location> findById(Integer id) {
        List<Location> results = jdbcTemplate.query(
                "SELECT id, title, parent_id, created_at, deleted_at From locations WHERE id=?",
                this::mapRowToLocation,
                id
        );

        return results.size() == 0 ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Location save(Location location) {
        jdbcTemplate.update(
                "INSERT INTO locations (id, title, parent_id, created_at, deleted_at)" +
                        "VALUES (?, ?, ?, ?, ?);",
                location.getId(),
                location.getTitle(),
                location.getParentId(),
                location.getCreatedAt(),
                location.getDeletedAt()
        );

        return location;
    }

    private Location mapRowToLocation(ResultSet row, int rowNum) throws SQLException {
        return new Location(
                row.getInt("id"),
                row.getString("title"),
                row.getInt("parent_id"),
                row.getTimestamp("created_at"),
                row.getTimestamp("deleted_at")
        );
    }

}
