BEGIN;

DROP TABLE IF EXISTS coordinations CASCADE;
DROP TABLE IF EXISTS movements CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS locations CASCADE;

COMMIT;

BEGIN;

CREATE TABLE IF NOT EXISTS locations
(
  id                  serial PRIMARY KEY,
  title               varchar(50) NOT NULL,
  parent_id           int,
  depth               int,
  responsible_user_id int,
  deleted_at          timestamptz
);

CREATE TABLE IF NOT EXISTS users
(
  id          serial PRIMARY KEY,
  last_name   varchar(50)         NOT NULL,
  first_name  varchar(50)         NOT NULL,
  email       varchar(100) UNIQUE NOT NULL,
  phone       varchar(11) UNIQUE CHECK ( LENGTH(phone) = 11 ),
  password    varchar             NOT NULL,
  location_id int                 NOT NULL DEFAULT 1,
  role        varchar(35)         NOT NULL DEFAULT 'USER',
  is_active   boolean             NOT NULL DEFAULT TRUE,
  created_at  timestamptz         NOT NULL DEFAULT NOW(),
  deleted_at  timestamptz
);

CREATE TABLE IF NOT EXISTS categories
(
  id        serial PRIMARY KEY,
  title     varchar NOT NULL,
  prefix    varchar(4) DEFAULT '',
  parent_id int,
  depth     int
);

CREATE TABLE IF NOT EXISTS items
(
  id          serial PRIMARY KEY,
  prefix      varchar(4)            DEFAULT '',
  number      bigint       NOT NULL,
  title       varchar(255) NOT NULL,
  quantity    int          NOT NULL,
  category_id int          NOT NULL,
  location_id int          NOT NULL,
  created_at  timestamptz  NOT NULL DEFAULT NOW(),
  deleted_at  timestamptz,
  UNIQUE (prefix, number)
);

CREATE TABLE IF NOT EXISTS movements
(
  id                serial PRIMARY KEY,
  type              varchar(35) NOT NULL,
  item_id           int         NOT NULL,
  quantity          int         NOT NULL,
  location_from_id  int,
  location_to_id    int,
  requested_user_id int         NOT NULL,
  status            varchar(35) NOT NULL DEFAULT 'UNDER_APPROVAL',
  comment           varchar(255),
  created_at        timestamptz NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS coordinations
(
  movement_id   int         NOT NULL,
  chief_user_id int         NOT NULL,
  status        varchar(35) NOT NULL DEFAULT 'WAITING',
  comment       varchar(255),
  created_at    timestamptz NOT NULL DEFAULT NOW(),
  PRIMARY KEY (movement_id, chief_user_id, created_at)
);

ALTER TABLE locations
  ADD CONSTRAINT locations_parent_id FOREIGN KEY (parent_id) REFERENCES locations (id);
ALTER TABLE locations
  ADD CONSTRAINT locations_responsible_user_id FOREIGN KEY (responsible_user_id) REFERENCES users (id);

ALTER TABLE users
  ADD CONSTRAINT users_location_id FOREIGN KEY (location_id) REFERENCES locations (id);

ALTER TABLE categories
  ADD CONSTRAINT categories_parent_id FOREIGN KEY (parent_id) REFERENCES categories (id);

ALTER TABLE items
  ADD CONSTRAINT items_category_id FOREIGN KEY (category_id) REFERENCES categories (id);
ALTER TABLE items
  ADD CONSTRAINT items_location_id FOREIGN KEY (location_id) REFERENCES locations (id);

ALTER TABLE movements
  ADD CONSTRAINT movements_item_id FOREIGN KEY (item_id) REFERENCES items (id);
ALTER TABLE movements
  ADD CONSTRAINT movements_location_from_id FOREIGN KEY (location_from_id) REFERENCES locations (id);
ALTER TABLE movements
  ADD CONSTRAINT movements_location_to_id FOREIGN KEY (location_to_id) REFERENCES locations (id);

ALTER TABLE coordinations
  ADD CONSTRAINT movement_id FOREIGN KEY (movement_id) REFERENCES movements (id);
ALTER TABLE coordinations
  ADD CONSTRAINT chief_user_id FOREIGN KEY (chief_user_id) REFERENCES users (id);

COMMIT;
BEGIN;

INSERT INTO locations (title, parent_id, depth, deleted_at)
VALUES ('��� ��������', DEFAULT, DEFAULT, null),
       ('������', 1, 1, null),
       ('���������', 1, 1, null),
       ('���������', 1, 1, null),
       ('���� �� ��. ������', 2, 2, null),
       ('����� �� ��. ����', 2, 2, null),
       ('������� �� ���������', 3, 2, null),
       ('������� �� ������������', 4, 2, null),
       ('������-��-����', 1, 1, null),
       ('�����-���������', 1, 1, null),
       ('������������ ��., 64', 9, 2, null),
       ('��������� ��., 2', 10, 2, null),
       ('�����', 12, 3, null),
       ('�������', 12, 3, null),
       ('IT-�����', 1, 1, null),
       ('�����', 15, 2, '2023-02-12 01:22:26.377+03'),
       ('��������� �����', DEFAULT, DEFAULT, null),
       ('�������������� �������������', DEFAULT, DEFAULT, '2023-02-12 01:22:26.377+03');


INSERT INTO users (last_name, first_name, email, phone, password, location_id, role)
VALUES ('��������', '���������', 'elizaveta2042@ya.ru', '79818313615',
        '$2a$10$hcaxN5nidfDpRlvEqo3xMeLOGP7mv0BukIzpKuxmSCtZ5N1M8M1aS', 1, 'OWNER'),
       ('�������', '��������', 'afanasiy18011985@mail.ru', '79798435867',
        '$2a$10$miOdCXmrTQujFXqAakwFZ.GbHE2wXmK5XVL5mO.CKUP.18HqvJ35q', 2, 'ADMIN'),
       ('���������', '�������', 'vasiliy16011990@yandex.ru', '79589027647',
        '$2a$10$vkhTgdV0riAldlvtTA34Hu7nY3EOyQdkbF6npG4bkZ3zS0fs/jG0q', 5, 'USER'),
       ('�������', '������', 'nikita91@ya.ru', '79592873253',
        '$2a$10$Z9bCynmofZM2/l/.GwYGAu/6eOYWFAQl8Th2jaZQmIrf94wHXKhOO', 5, 'USER'),
       ('������', '������', 'daniil6777@mail.ru', '79529038435',
        '$2a$10$0y02GkFDvVSNssHfp0U5BeI6wrVvLdYM7xRy0HC/dRmW9ZVAoR9OK', 6, 'USER'),
       ('���������', '��������', 'viktoriya5412@mail.ru', '79591498936',
        '$2a$10$3NLlXaDShebTnxmNKDen1OrLREvtJ3qv0reOa1BmttT4VBgNwtx8G', 7, 'USER'),
       ('������������', '��������', 'katerina1969@ya.ru', '79138231819',
        '$2a$10$kohWeR1WDlXMtjVbVcn6W.HPLrGm9UkyHccYSFExScfdMeW9rhB4a', 8, 'USER'),
       ('�����', '��������', 'veronika03081978@rambler.ru', DEFAULT,
        '$2a$10$Rl9zDfM8MEMYhHyXLWidR.Py27ECuYBAeUWAdBvXTftnKvg1StJJC', 11, 'USER'),
       ('�������', '����', 'ilya18011960@outlook.com', DEFAULT,
        '$2a$10$YoWQjm.VVoBjgBTkokmtJuqvM5dqXoaCz34DFzLgq54Ak/16EYE2y', 13, 'USER'),
       ('��������', '�����', 'klara18101964@hotmail.com', DEFAULT,
        '$2a$10$mP/bHtZXeACN8sjQ9NhQFeK2LT2DZDNleg0G4MbECrzS3J7cAJxvi', 12, 'USER'),
       ('������', '���������', 'admin@example.com', DEFAULT,
        '$2a$10$ACeHkQSea7UkIv933Jg52eAqqQ8st5jnicEBxqVOA5MW9x/nrBq8u', 15, 'ADMIN');

UPDATE locations
SET responsible_user_id = 1
WHERE id = 1;
UPDATE locations
SET responsible_user_id = 2
WHERE id = 2;
UPDATE locations
SET responsible_user_id = 3
WHERE id = 5;
UPDATE locations
SET responsible_user_id = 5
WHERE id = 6;
UPDATE locations
SET responsible_user_id = 6
WHERE id = 7;
UPDATE locations
SET responsible_user_id = 8
WHERE id = 11;
UPDATE locations
SET responsible_user_id = 10
WHERE id = 12;
UPDATE locations
SET responsible_user_id = 11
WHERE id = 15;

INSERT INTO categories (title, prefix, parent_id, depth)
VALUES ('����������/��������', DEFAULT, DEFAULT, 1),
       ('��������� �����', '��', 1, 2),
       ('��������', '��', 1, 2),
       ('�������������', '����', 1, 2),
       ('����������', DEFAULT, 3, 3),
       ('����������', DEFAULT, DEFAULT, 1),
       ('���', '���', 6, 2),
       ('��������� ���������', NULL, 6, 3),
       ('���������', DEFAULT, 8, 4),
       ('���������', DEFAULT, DEFAULT, 1),
       ('��������', '���', 10, 2),
       ('����', '�', 10, 2),
       ('����������', '���', 10, 2),
       ('���-������', '����', 10, 2),
       ('������', DEFAULT, 8, 4);

INSERT INTO items (prefix, number, title, quantity, category_id, location_id, deleted_at)
VALUES ('��', 000001, '��������� ���� DEXP', 1, 2, 6, null),
       ('��', 000002, '��������� ���� Lenovo', 1, 2, 6, null),
       ('��', 000001, '������� Asus', 1, 3, 5, null),
       ('��', 000002, '������� Asus', 1, 3, 7, null),
       ('��', 000003, '������� Samsung', 1, 3, 8, null),
       ('���', 000001, '������� LG', 1, 11, 6, null),
       ('���', 000002, '������� LG', 1, 11, 6, null),
       (DEFAULT, 000001, '���� A4Tech', 1, 12, 6, null),
       (DEFAULT, 000002, '���� A4Tech', 1, 12, 6, null),
       (DEFAULT, 000003, '���� A4Tech', 1, 12, 5, null),
       (DEFAULT, 000004, '���� A4Tech', 1, 12, 7, null),
       (DEFAULT, 000005, '���� A4Tech', 1, 12, 8, null),
       (DEFAULT, 000006, '���������� A4Tech', 1, 13, 6, null),
       (DEFAULT, 000007, '���������� A4Tech', 1, 13, 6, null),
       (DEFAULT, 000008, '������ ������ Epson', 4, 15, 6, null),
       ('��', 000003, '��������� ����', 1, 2, 13, null),
       ('��', 000004, '��������� ����', 1, 2, 14, null),
       ('��', 000004, '������� Asus', 1, 3, 11, null),
       ('�', 000001, '����', 1, 12, 13, null),
       ('�', 000002, '����', 1, 12, 14, null),
       ('���', 000006, '����������', 1, 13, 13, null),
       ('���', 000007, '����������', 1, 13, 14, null),
       ('���', 000008, '����������', 1, 13, 14, null),
       ('���', 000003, '�������', 1, 11, 13, null),
       ('���', 000005, '�������', 1, 11, 13, '2023-02-12 01:22:26.377+03'),
       ('���', 000004, '�������', 1, 11, 14, null);

INSERT INTO movements (type, item_id, quantity, location_from_id, location_to_id, requested_user_id, status)
VALUES ('MOVEMENT', 1, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 2, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 3, 1, null, 5, 11, 'SUCCESS'),
       ('MOVEMENT', 4, 1, null, 7, 11, 'SUCCESS'),
       ('MOVEMENT', 5, 1, null, 8, 11, 'SUCCESS'),
       ('MOVEMENT', 6, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 7, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 8, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 9, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 10, 1, null, 5, 11, 'SUCCESS'),
       ('MOVEMENT', 11, 1, null, 7, 11, 'SUCCESS'),
       ('MOVEMENT', 12, 1, null, 8, 11, 'SUCCESS'),
       ('MOVEMENT', 13, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 14, 1, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 15, 4, null, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 16, 1, null, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 17, 1, null, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 18, 1, null, 11, 11, 'SUCCESS'),
       ('MOVEMENT', 19, 1, null, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 20, 1, null, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 21, 1, null, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 22, 1, null, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 23, 1, null, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 24, 1, null, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 25, 1, null, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 13, 1, 6, 15, 2, 'SENT'),
       ('WRITE_OFF', 23, 1, 14, null, 10, 'UNDER_APPROVAL'),
       ('MOVEMENT', 10, 1, 5, 15, 4, 'APPROVED'),
       ('MOVEMENT', 12, 1, 5, 15, 4, 'UNDER_APPROVAL');

INSERT INTO coordinations (movement_id, chief_user_id, status, comment, created_at)
VALUES (26, 1, 'COORDINATED', null, '2023-02-12 01:22:26.377+03'),
       (27, 1, 'WAITING', null, '2023-02-17 21:20:58.5+03'),
       (28, 1, 'COORDINATED', null, '2023-02-18 01:04:28.082+03'),
       (28, 2, 'COORDINATED', null, '2023-02-18 01:04:16.717+03'),
       (28, 3, 'COORDINATED', null, '2023-02-18 01:01:34.942+03'),
       (26, 2, 'SENT', null, '2023-02-21 15:06:22.3+03'),
       (29, 3, 'WAITING', null, '2023-02-21 15:06:22.3+03');

COMMIT;
