BEGIN;

DELETE FROM locations;
DELETE FROM users;

INSERT INTO locations (id, title, parent_id, depth) VALUES
    (1, 'Моя компания', DEFAULT, DEFAULT),
    (2, 'Москва', 1, 1),
    (3, 'Краснодар', 1, 1),
    (4, 'Волгоград', 1, 1),
    (5, 'Офис на ул. Ленина', 2, 2),
    (6, 'Склад на пр. Мира', 2, 2),
    (7, 'Магазин на Сапрунова', 3, 2),
    (8, 'Магазин на Исторической', 4, 2);

SELECT setval('locations_id_seq', 8);

INSERT INTO users (id, last_name, first_name, email, phone, password, location_id, is_active, role) VALUES
    (1, 'Летавина', 'Елизавета', 'elizaveta2042@ya.ru', 79818313615, '$2a$10$hcaxN5nidfDpRlvEqo3xMeLOGP7mv0BukIzpKuxmSCtZ5N1M8M1aS', 1, true, 'OWNER'),
    (2, 'Коленко', 'Афанасий', 'afanasiy18011985@mail.ru', 79798435867, '$2a$10$F/NQmwwY4Qvk4z/ms9rGiefbIGTCNumJtqXzYAwQVl5WohaC8AmDK', 2, true, 'ADMIN'),
    (3, 'Северинов', 'Василий', 'vasiliy16011990@yandex.ru', 79589027647, '$2a$10$1w5eCDs9Eb7nNGfT4/NXfePwm7hvaGYcEVUmz32nQaSEJx/YWq/FG', 5, true, 'USER'),
    (4, 'Есаулов', 'Никита', 'nikita91@ya.ru', 79592873253, '$2a$10$LBrUGQ6i9LevUJuSGUDr/e10Ag3TzJU0qmgMFUor5iksmzoJIF64K', 5, true, 'USER'),
    (5, 'Куняев', 'Даниил', 'daniil6777@mail.ru', 79529038435, '$2a$10$J0DKQTpqenufljE6bGcM2.C/wu/d02op6AgYwygnwYYakfzHUZRii', 6, true, 'USER'),
    (6, 'Блаженова', 'Виктория', 'viktoriya5412@mail.ru', 79591498936, '$2a$10$1lCXyFBam.oyyl6G1Y/p0uTnuUosOinNjuRKmtZyx6QmQ9eEsloOS', 7, true, 'USER'),
    (7, 'Островерхова', 'Катерина', 'katerina1969@ya.ru', 79138231819, '$2a$10$SiPV5E3M8mGo/.XFD91fOOf94N1z2szuly5rdNL0m9LwFNwSl6iJC', 8, true, 'USER');

SELECT setval('users_id_seq', 7);

UPDATE locations SET responsible_user_id = 1 WHERE id = 1;
UPDATE locations SET responsible_user_id = 2 WHERE id = 2;
UPDATE locations SET responsible_user_id = 3 WHERE id = 5;
UPDATE locations SET responsible_user_id = 5 WHERE id = 6;
UPDATE locations SET responsible_user_id = 6 WHERE id = 7;
UPDATE locations SET responsible_user_id = 7 WHERE id = 8;

INSERT INTO categories (id, name, prefix, parent_id, depth) VALUES
    (1, 'Компьютеры/Ноутбуки', DEFAULT, DEFAULT, DEFAULT),
    (2, 'Системные блоки', 'ПК', 1, 1),
    (3, 'Ноутбуки', 'НБ', 1, 1),
    (4, 'Комплектующие', 'КМПЛ', 1, 1),
    (5, 'Аксессуары', DEFAULT, 3, 2),
    (6, 'Оргтехника', DEFAULT, DEFAULT, DEFAULT),
    (7, 'МФУ', 'ПР', 6, 1),
    (8, 'Расходные материалы', NULL, 6, 2),
    (9, 'Картриджи', DEFAULT, 8, 3),
    (10, 'Периферия', DEFAULT, DEFAULT, DEFAULT),
    (11, 'Мониторы', 'МОН', 8, 1),
    (12, 'Мыши', DEFAULT, 8, 1),
    (13, 'Клавиатуры', DEFAULT, 8, 1),
    (14, 'Веб-камеры', 'ВЕБК', 8, 1),
    (15, 'Краски', DEFAULT, 8, 3);

SELECT setval('categories_id_seq', 15);

INSERT INTO items (id, prefix, number, title, quantity, category_id, location_id) VALUES
    (1, 'ПК', 000001, 'Системный блок DEXP', 1, 2, 6),
    (2, 'ПК', 000002, 'Системный блок Lenovo', 1, 2, 6),
    (3, 'НБ', 000001, 'Ноутбук Asus', 1, 3, 5),
    (4, 'НБ', 000002, 'Ноутбук Asus', 1, 3, 7),
    (5, 'НБ', 000003, 'Ноутбук Samsung', 1, 3, 8),
    (6, 'МОН', 000001, 'Монитор LG', 1, 11, 6),
    (7, 'МОН', 000002, 'Монитор LG', 1, 11, 6),
    (8, DEFAULT, 000001, 'Мышь A4Tech', 1, 12, 6),
    (9, DEFAULT, 000002, 'Мышь A4Tech', 1, 12, 6),
    (10, DEFAULT, 000003, 'Мышь A4Tech', 1, 12, 5),
    (11, DEFAULT, 000004, 'Мышь A4Tech', 1, 12, 7),
    (12, DEFAULT, 000005, 'Мышь A4Tech', 1, 12, 8),
    (13, DEFAULT, 000006, 'Клавиатура A4Tech', 1, 13, 6),
    (14, DEFAULT, 000007, 'Клавиатура A4Tech', 1, 13, 6),
    (15, DEFAULT, 000008, 'Краска чёрная Epson', 4, 15, 6);

SELECT setval('items_id_seq', 15);

COMMIT;