BEGIN;

CREATE TABLE IF NOT EXISTS locations (
    id serial PRIMARY KEY,
    title varchar(50) NOT NULL,
    parent_id int,
    depth int,
    responsible_user_id int,
    created_at timestamptz NOT NULL DEFAULT now(),
    deleted_at timestamptz
);

CREATE TABLE IF NOT EXISTS users (
    id serial PRIMARY KEY,
    last_name varchar(50) NOT NULL,
    first_name varchar(50) NOT NULL,
    email varchar(100) UNIQUE NOT NULL,
    phone bigint UNIQUE NOT NULL,
    password varchar NOT NULL,
    location_id int NOT NULL,
    role varchar(35) NOT NULL DEFAULT 'USER',
    is_active boolean NOT NULL DEFAULT TRUE,
    created_at timestamptz NOT NULL DEFAULT now(),
    deleted_at timestamptz
);

CREATE TABLE IF NOT EXISTS categories (
    id serial PRIMARY KEY,
    name varchar NOT NULL,
    prefix varchar(4) UNIQUE DEFAULT NULL,
    parent_id int,
    depth int,
    created_at timestamptz NOT NULL DEFAULT now(),
    deleted_at timestamptz
);

CREATE TABLE IF NOT EXISTS items (
    id serial PRIMARY KEY,
    prefix varchar(4) DEFAULT '',
    number bigint NOT NULL,
    title varchar(255) NOT NULL,
    quantity int NOT NULL,
    category_id int NOT NULL,
    location_id int NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    deleted_at timestamptz,
    UNIQUE (prefix, number)
);

CREATE TABLE IF NOT EXISTS movements (
    id serial PRIMARY KEY,
    type varchar(35) NOT NULL,
    item_id int NOT NULL,
    quantity int NOT NULL,
    location_from_id int NOT NULL,
    location_to_id int NOT NULL,
    status varchar(35) NOT NULL,
    comment varchar(255),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS coordinations (
    movement_id int NOT NULL,
    chief_id int NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE locations ADD CONSTRAINT locations_parent_id FOREIGN KEY (parent_id) REFERENCES locations (id);
ALTER TABLE locations ADD CONSTRAINT locations_responsible_user_id FOREIGN KEY (responsible_user_id) REFERENCES users (id);

ALTER TABLE users ADD CONSTRAINT users_location_id FOREIGN KEY (location_id) REFERENCES locations (id);

ALTER TABLE categories ADD CONSTRAINT categories_parent_id FOREIGN KEY (parent_id) REFERENCES categories (id);

ALTER TABLE items ADD CONSTRAINT items_category_id FOREIGN KEY (category_id) REFERENCES categories (id);
ALTER TABLE items ADD CONSTRAINT items_location_id FOREIGN KEY (location_id) REFERENCES locations (id);

ALTER TABLE movements ADD CONSTRAINT movements_item_id FOREIGN KEY (item_id) REFERENCES locations (id);
ALTER TABLE movements ADD CONSTRAINT movements_location_from_id FOREIGN KEY (location_from_id) REFERENCES locations (id);
ALTER TABLE movements ADD CONSTRAINT movements_location_to_id FOREIGN KEY (location_to_id) REFERENCES locations (id);

ALTER TABLE coordinations ADD CONSTRAINT movement_id FOREIGN KEY (movement_id) REFERENCES movements (id);
ALTER TABLE coordinations ADD CONSTRAINT chief_id FOREIGN KEY (chief_id) REFERENCES users (id);

COMMIT;
