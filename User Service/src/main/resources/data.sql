INSERT INTO roles (name)
VALUES ('BUYER'),
       ('SELLER'),
       ('ADMIN');

-- users profiles

INSERT INTO users (email, full_name, phone, status, created_at, updated_at)
VALUES
('buyer1@gmail.com', 'Buyer 1', '+998915129737', 'ACTIVE', now() - interval '5 day', now() - interval '5 day'),

('buyer2@gmail.com', 'Buyer 2', '+998942229919', 'ACTIVE', now() - interval '7 day', now() - interval '7 day'),

('buyer3@gmail.com', 'Buyer 3', '+998902667466', 'ACTIVE', now() - interval '9 day', now() - interval '9 day'),

('sellerRog@gmail.com', 'Seller ROG', '+998997501208', 'ACTIVE', now() - interval '5 day',now() - interval '5 day'),

('sellerMac@gmail.com', 'Seller Mac', '+998991221206', 'ACTIVE', now() - interval '6 day',now() - interval '6 day'),

('sellerMix@gmail.com', 'Seller Mix', '+998770011770', 'ACTIVE', now() - interval '8 day',now() - interval '8 day'),

('admin@gmail.com', 'Admin 0', '+998991221206', 'ACTIVE', now() - interval '10 day', now() - interval '10 day');

-- user roles

-- Buyers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.email IN ('buyer1@gmail.com', 'buyer2@gmail.com', 'buyer3@gmail.com')
  AND r.name = 'BUYER';

-- Seller gets both BUYER and SELLER roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.email in ('sellerRog@gmail.com', 'sellerMac@gmail.com', 'sellerMix@gmail.com') AND r.name IN ('BUYER', 'SELLER');

-- Admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.email = 'admin@gmail.com'
  AND r.name = 'ADMIN';

-- addresses

INSERT INTO addresses (label, street, city, state, zip, country, is_default, user_id)
VALUES
    ('HOME', '123 Main St', 'New York', 'NY', '10001', 'USA', true,
        (SELECT id FROM users WHERE email = 'buyer1@gmail.com')),

    ('WORK', '456 Office Ave', 'New York', 'NY', '10002', 'USA', false,
        (SELECT id FROM users WHERE email = 'sellerMac@gmail.com')),

    ('HOME', '789 Oak Lane', 'Los Angeles', 'CA', '90001', 'USA', true,
        (SELECT id FROM users WHERE email = 'buyer2@gmail.com')),

    ('HOME', '321 Pine Rd', 'Chicago', 'IL', '60601', 'USA', true,
        (SELECT id FROM users WHERE email = 'buyer3@gmail.com')),

    ('WORK', 'IT Park', 'Toshkent', 'Yakkasaroy', '10000', 'Uzbekistan', true,
        (SELECT id FROM users WHERE email = 'sellerRog@gmail.com'));

-- sellers profile

INSERT INTO seller_profiles (store_name, store_description, average_rating, total_sales, user_id)
VALUES
    ('ROG & Asus Store',
        'Republic of Gamers',
        3.2,
        5,
        (SELECT id FROM users WHERE email = 'sellerRog@gmail.com')),

    ('MacBro',
        'MacBook & Iphone',
        3.3,
        6,
        (SELECT id FROM users WHERE email = 'sellerMac@gmail.com')),

    ('Mix Tech',
        'Laptop & Phones',
        3.4,
        7,
        (SELECT id FROM users WHERE email = 'sellerMix@gmail.com'));