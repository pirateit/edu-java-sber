BEGIN;

DELETE FROM locations;
DELETE FROM users;

INSERT INTO locations (id, title, parent_id, responsible_user_id, created_at, deleted_at) VALUES
    (1, 'Моя компания', NULL, NULL, now(), NULL),
    (2, 'Москва', 1, NULL, now(), NULL),
    (3, 'Краснодар', 1, NULL ,now(), NULL),
    (4, 'Волгоград', 1, NULL, now(), NULL),
    (5, 'Офис на ул. Ленина', 2, NULL, now(), NULL),
    (6, 'Склад на пр. Мира', 2, NULL, now(), NULL),
    (7, 'Магазин на Сапрунова', 3, NULL, now(), NULL),
    (8, 'Магазин на Исторической', 4, NULL, now(), NULL);

INSERT INTO users (id, last_name, first_name, email, phone, password, location_id, created_at, deleted_at) VALUES
    (1, 'Летавина', 'Елизавета', 'elizaveta2042@ya.ru', 79818313615, '1234', 1, true, now(), NULL),
    (2, 'Коленко', 'Афанасий', 'afanasiy18011985@mail.ru', 79798435867, '1234', 2, true, now(), NULL),
    (3, 'Северинов', 'Василий', 'vasiliy16011990@yandex.ru', 79589027647, '1234', 5, true, now(), NULL),
    (4, 'Есаулов', 'Никита', 'nikita91@ya.ru', 79592873253, '1234', 5, true, now(), NULL),
    (5, 'Куняев', 'Даниил', 'daniil6777@mail.ru', 79529038435, '1234', 6, true, now(), NULL),
    (6, 'Блаженова', 'Виктория', 'viktoriya5412@mail.ru', 79591498936, '1234', 7, true, now(), NULL),
    (7, 'Островерхова', 'Катерина', 'katerina1969@ya.ru', 79138231819, '1234', 8, true, now(), NULL);

UPDATE locations SET responsible_user_id = 1 WHERE id = 1;
UPDATE locations SET responsible_user_id = 2 WHERE id = 2;
UPDATE locations SET responsible_user_id = 3 WHERE id = 5;
UPDATE locations SET responsible_user_id = 5 WHERE id = 6;
UPDATE locations SET responsible_user_id = 6 WHERE id = 7;
UPDATE locations SET responsible_user_id = 7 WHERE id = 8;

COMMIT;