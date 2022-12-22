CREATE TABLE IF NOT EXISTS locations (
   id serial PRIMARY KEY,
   title varchar(50) NOT NULL,
   parent_id int DEFAULT NULL,
   responsible_user_id int DEFAULT NULL,
   created_at timestamptz NOT NULL,
   deleted_at timestamptz DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS users (
   id serial PRIMARY KEY,
   last_name varchar(50) NOT NULL,
   first_name varchar(50) NOT NULL,
   email varchar(100) UNIQUE NOT NULL,
   phone bigint UNIQUE NOT NULL,
   password varchar NOT NULL,
   location_id int NOT NULL,
   created_at timestamptz NOT NULL,
   deleted_at timestamptz DEFAULT NULL
);

ALTER TABLE locations ADD CONSTRAINT locations_parent_id FOREIGN KEY (parent_id) REFERENCES locations (id);
ALTER TABLE locations ADD CONSTRAINT locations_responsible_user_id FOREIGN KEY (responsible_user_id) REFERENCES users (id);

ALTER TABLE users ADD CONSTRAINT users_location_id FOREIGN KEY (location_id) REFERENCES locations (id);