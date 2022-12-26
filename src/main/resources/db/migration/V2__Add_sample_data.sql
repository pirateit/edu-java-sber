BEGIN;

DELETE FROM locations;
DELETE FROM users;

INSERT INTO locations (id, title, parent_id, depth, responsible_user_id, created_at, deleted_at) VALUES
    (1, 'Моя компания', NULL, NULL, NULL, now(), NULL),
    (2, 'Москва', 1, 1, NULL, now(), NULL),
    (3, 'Краснодар', 1, 1, NULL, now(), NULL),
    (4, 'Волгоград', 1, 1, NULL, now(), NULL),
    (5, 'Офис на ул. Ленина', 2, 2, NULL, now(), NULL),
    (6, 'Склад на пр. Мира', 2, 2, NULL, now(), NULL),
    (7, 'Магазин на Сапрунова', 3, 2, NULL, now(), NULL),
    (8, 'Магазин на Исторической', 4, 2, NULL, now(), NULL);

INSERT INTO users (id, last_name, first_name, email, phone, password, location_id, is_active, role, created_at, deleted_at) VALUES
    (1, 'Летавина', 'Елизавета', 'elizaveta2042@ya.ru', 79818313615, '1234', 1, true, 'OWNER', now(), NULL),
    (2, 'Коленко', 'Афанасий', 'afanasiy18011985@mail.ru', 79798435867, '1234', 2, true, 'USER', now(), NULL),
    (3, 'Северинов', 'Василий', 'vasiliy16011990@yandex.ru', 79589027647, '1234', 5, true, 'USER', now(), NULL),
    (4, 'Есаулов', 'Никита', 'nikita91@ya.ru', 79592873253, '1234', 5, true, 'USER', now(), NULL),
    (5, 'Куняев', 'Даниил', 'daniil6777@mail.ru', 79529038435, '1234', 6, true, 'USER', now(), NULL),
    (6, 'Блаженова', 'Виктория', 'viktoriya5412@mail.ru', 79591498936, '1234', 7, true, 'USER', now(), NULL),
    (7, 'Островерхова', 'Катерина', 'katerina1969@ya.ru', 79138231819, '1234', 8, true, 'USER', now(), NULL);

UPDATE locations SET responsible_user_id = 1 WHERE id = 1;
UPDATE locations SET responsible_user_id = 2 WHERE id = 2;
UPDATE locations SET responsible_user_id = 3 WHERE id = 5;
UPDATE locations SET responsible_user_id = 5 WHERE id = 6;
UPDATE locations SET responsible_user_id = 6 WHERE id = 7;
UPDATE locations SET responsible_user_id = 7 WHERE id = 8;

INSERT INTO categories (id, name, prefix, parent_id, depth) VALUES
    (1, 'Компьютеры/Ноутбуки', NULL, NULL, NULL),
    (2, 'Системные блоки', 'ПК', 1, 1),
    (3, 'Ноутбуки', 'НБ', 1, 1),
    (4, 'Комплектующие', 'КМПЛ', 1, 1),
    (5, 'Аксессуары', NULL, 3, 2),
    (6, 'Оргтехника', NULL, NULL, NULL),
    (7, 'МФУ', 'ПР', 6, 1),
    (8, 'Расходные материалы', NULL, 6, 2),
    (9, 'Картриджи', NULL, 8, 3),
    (10, 'Периферия', NULL, NULL, NULL),
    (11, 'Мониторы', 'МОН', 8, 1),
    (12, 'Мыши', NULL, 8, 1),
    (13, 'Клавиатуры', NULL, 8, 1),
    (14, 'Веб-камеры', 'ВЕБК', 8, 1),
    (15, 'Краски', NULL, 8, 3);

INSERT INTO items (id, prefix, number, title, quantity, category_id, location_id) VALUES
    (1, 'ПК', 000001, 'Системный блок DEXP', 1, 2, 6),
    (2, 'ПК', 000002, 'Системный блок Lenovo', 1, 2, 6),
    (3, 'НБ', 000001, 'Ноутбук Asus', 1, 3, 5),
    (4, 'НБ', 000002, 'Ноутбук Asus', 1, 3, 7),
    (5, 'НБ', 000003, 'Ноутбук Samsung', 1, 3, 8),
    (6, 'МОН', 000001, 'Монитор LG', 1, 11, 6),
    (7, 'МОН', 000002, 'Монитор LG', 1, 11, 6),
    (8, NULL, 000001, 'Мышь A4Tech', 1, 12, 6),
    (9, NULL, 000002, 'Мышь A4Tech', 1, 12, 6),
    (10, NULL, 000003, 'Мышь A4Tech', 1, 12, 5),
    (11, NULL, 000004, 'Мышь A4Tech', 1, 12, 7),
    (12, NULL, 000005, 'Мышь A4Tech', 1, 12, 8),
    (13, NULL, 000006, 'Клавиатура A4Tech', 1, 13, 6),
    (14, NULL, 000007, 'Клавиатура A4Tech', 1, 13, 6),
    (15, NULL, 000008, 'Краска чёрная Epson', 4, 15, 6);

COMMIT;