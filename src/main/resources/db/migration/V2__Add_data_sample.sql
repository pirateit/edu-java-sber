BEGIN;

INSERT INTO locations (id, title, parent_id, depth)
VALUES (1, 'Моя компания', DEFAULT, DEFAULT),
       (2, 'Москва', 1, 1),
       (3, 'Краснодар', 1, 1),
       (4, 'Волгоград', 1, 1),
       (5, 'Офис на ул. Ленина', 2, 2),
       (6, 'Склад на пр. Мира', 2, 2),
       (7, 'Магазин на Сапрунова', 3, 2),
       (8, 'Магазин на Исторической', 4, 2),
       (9, 'Ростов-на-Дону', 1, 1),
       (10, 'Санкт-Петербург', 1, 1),
       (11, 'Криворожская ул., 64', 9, 2),
       (12, 'Роменская ул., 2', 10, 2),
       (13, 'Склад', 12, 3),
       (14, 'Магазин', 12, 3),
       (15, 'IT-отдел', 1, 1);

SELECT setval('locations_id_seq', 15);

INSERT INTO users (id, last_name, first_name, email, phone, password, location_id, role)
VALUES (1, 'Летавина', 'Елизавета', 'elizaveta2042@ya.ru', '79818313615',
        '$2a$10$hcaxN5nidfDpRlvEqo3xMeLOGP7mv0BukIzpKuxmSCtZ5N1M8M1aS', 1, 'OWNER'),
       (2, 'Коленко', 'Афанасий', 'afanasiy18011985@mail.ru', '79798435867',
        '$2a$10$miOdCXmrTQujFXqAakwFZ.GbHE2wXmK5XVL5mO.CKUP.18HqvJ35q', 2, 'ADMIN'),
       (3, 'Северинов', 'Василий', 'vasiliy16011990@yandex.ru', '79589027647',
        '$2a$10$vkhTgdV0riAldlvtTA34Hu7nY3EOyQdkbF6npG4bkZ3zS0fs/jG0q', 5, 'USER'),
       (4, 'Есаулов', 'Никита', 'nikita91@ya.ru', '79592873253',
        '$2a$10$Z9bCynmofZM2/l/.GwYGAu/6eOYWFAQl8Th2jaZQmIrf94wHXKhOO', 5, 'USER'),
       (5, 'Куняев', 'Даниил', 'daniil6777@mail.ru', '79529038435',
        '$2a$10$0y02GkFDvVSNssHfp0U5BeI6wrVvLdYM7xRy0HC/dRmW9ZVAoR9OK', 6, 'USER'),
       (6, 'Блаженова', 'Виктория', 'viktoriya5412@mail.ru', '79591498936',
        '$2a$10$3NLlXaDShebTnxmNKDen1OrLREvtJ3qv0reOa1BmttT4VBgNwtx8G', 7, 'USER'),
       (7, 'Островерхова', 'Катерина', 'katerina1969@ya.ru', '79138231819',
        '$2a$10$kohWeR1WDlXMtjVbVcn6W.HPLrGm9UkyHccYSFExScfdMeW9rhB4a', 8, 'USER'),
       (8, 'Щитта', 'Вероника', 'veronika03081978@rambler.ru', DEFAULT,
        '$2a$10$Rl9zDfM8MEMYhHyXLWidR.Py27ECuYBAeUWAdBvXTftnKvg1StJJC', 11, 'USER'),
       (9, 'Слепцов', 'Илья', 'ilya18011960@outlook.com', DEFAULT,
        '$2a$10$YoWQjm.VVoBjgBTkokmtJuqvM5dqXoaCz34DFzLgq54Ak/16EYE2y', 13, 'USER'),
       (10, 'Санькова', 'Клара', 'klara18101964@hotmail.com', DEFAULT,
        '$2a$10$mP/bHtZXeACN8sjQ9NhQFeK2LT2DZDNleg0G4MbECrzS3J7cAJxvi', 12, 'USER'),
       (11, 'Саенко', 'Александр', 'admin@example.com', DEFAULT,
        '$2a$10$ACeHkQSea7UkIv933Jg52eAqqQ8st5jnicEBxqVOA5MW9x/nrBq8u', 15, 'ADMIN');

SELECT setval('users_id_seq', 11);

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

INSERT INTO categories (id, title, prefix, parent_id, depth)
VALUES (1, 'Компьютеры/Ноутбуки', DEFAULT, DEFAULT, 1),
       (2, 'Системные блоки', 'ПК', 1, 2),
       (3, 'Ноутбуки', 'НБ', 1, 2),
       (4, 'Комплектующие', 'КМПЛ', 1, 2),
       (5, 'Аксессуары', DEFAULT, 3, 3),
       (6, 'Оргтехника', DEFAULT, DEFAULT, 1),
       (7, 'МФУ', 'МФУ', 6, 2),
       (8, 'Расходные материалы', NULL, 6, 3),
       (9, 'Картриджи', DEFAULT, 8, 4),
       (10, 'Периферия', DEFAULT, DEFAULT, 1),
       (11, 'Мониторы', 'МОН', 10, 2),
       (12, 'Мыши', 'М', 10, 2),
       (13, 'Клавиатуры', 'КЛВ', 10, 2),
       (14, 'Веб-камеры', 'ВЕБК', 10, 2),
       (15, 'Краски', DEFAULT, 8, 4);

SELECT setval('categories_id_seq', 15);

INSERT INTO items (id, prefix, number, title, quantity, category_id, location_id)
VALUES (1, 'ПК', 000001, 'Системный блок DEXP', 1, 2, 6),
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
       (15, DEFAULT, 000008, 'Краска чёрная Epson', 4, 15, 6),
       (16, 'ПК', 000003, 'Системный блок', 1, 2, 13),
       (17, 'ПК', 000004, 'Системный блок', 1, 2, 14),
       (18, 'НБ', 000004, 'Ноутбук Asus', 1, 3, 11),
       (19, 'М', 000001, 'Мышь', 1, 12, 13),
       (20, 'М', 000002, 'Мышь', 1, 12, 14),
       (21, 'КЛВ', 000006, 'Клавиатура', 1, 13, 13),
       (22, 'КЛВ', 000007, 'Клавиатура', 1, 13, 14),
       (23, 'КЛВ', 000008, 'Клавиатура', 1, 13, 14),
       (24, 'МОН', 000003, 'Монитор', 1, 11, 13),
       (25, 'МОН', 000004, 'Монитор', 1, 11, 14);

SELECT setval('items_id_seq', 25);

INSERT INTO locations (id, title, parent_id, depth)
VALUES (1, 'Моя компания', DEFAULT, DEFAULT),
       (2, 'Москва', 1, 1),
       (3, 'Краснодар', 1, 1),
       (4, 'Волгоград', 1, 1),
       (5, 'Офис на ул. Ленина', 2, 2),
       (6, 'Склад на пр. Мира', 2, 2),
       (7, 'Магазин на Сапрунова', 3, 2),
       (8, 'Магазин на Исторической', 4, 2),
       (9, 'Ростов-на-Дону', 1, 1),
       (10, 'Санкт-Петербург', 1, 1),
       (11, 'Криворожская ул., 64', 9, 2),
       (12, 'Роменская ул., 2', 10, 2),
       (13, 'Склад', 12, 3),
       (14, 'Магазин', 12, 3),
       (15, 'IT-отдел', 1, 1);

INSERT INTO movements (type, item_id, quantity, location_to_id, requested_user_id, status)
VALUES ('MOVEMENT', 1, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 2, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 3, 1, 5, 11, 'SUCCESS'),
       ('MOVEMENT', 4, 1, 7, 11, 'SUCCESS'),
       ('MOVEMENT', 5, 1, 8, 11, 'SUCCESS'),
       ('MOVEMENT', 6, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 7, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 8, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 9, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 10, 1, 5, 11, 'SUCCESS'),
       ('MOVEMENT', 11, 1, 7, 11, 'SUCCESS'),
       ('MOVEMENT', 12, 1, 8, 11, 'SUCCESS'),
       ('MOVEMENT', 13, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 14, 1, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 15, 4, 6, 11, 'SUCCESS'),
       ('MOVEMENT', 16, 1, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 17, 1, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 18, 1, 11, 11, 'SUCCESS'),
       ('MOVEMENT', 19, 1, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 20, 1, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 21, 1, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 22, 1, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 23, 1, 14, 11, 'SUCCESS'),
       ('MOVEMENT', 24, 1, 13, 11, 'SUCCESS'),
       ('MOVEMENT', 25, 1, 14, 11, 'SUCCESS');

COMMIT;
