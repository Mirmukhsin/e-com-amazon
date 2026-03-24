-- ─────────────────────────────────────────
-- 1. ROLES
-- ─────────────────────────────────────────
INSERT INTO roles (name) VALUES ('BUYER');

INSERT INTO roles (name) VALUES ('SELLER');

INSERT INTO roles (name) VALUES ('ADMIN');

-- ─────────────────────────────────────────
-- 2. USERS
-- ─────────────────────────────────────────
INSERT INTO users (email, password, full_name, phone, status, created_at)
VALUES
--     buyer1 -> password -> buyer111
    ('buyer1@gmail.com', '$2a$12$Cf.TFvXHLAHfkcCIW87WQuL/tr2i7f2IjnrESewXCU6ROZ22mPG7.', 'Buyer 1', '+998915129737', 'ACTIVE', now() - interval '5 day'),
--     buyer2 -> password -> buyer222
    ('buyer2@gmail.com', '$2a$12$/3N0Zy04hdMURj17LRuLrOR/oa3RLIn8sM0W14wA7A6mRDfmfjpO.', 'Buyer 2', '+998942229919', 'ACTIVE', now() - interval '7 day'),
--     buyer3 -> password -> buyer333
    ('buyer3@gmail.com', '$2a$12$L9ANKXG5yq44qb8Q4jKX8uptS17S/OqnVqrniDfAwJXHgw9dUYp5y', 'Buyer 3', '+998902667466', 'ACTIVE', now() - interval '9 day'),
--     sellerRog -> password -> seller111
    ('sellerRog@gmail.com', '$2a$12$luPh45DELgj3vOBVpkuGY.MhsoG0t8sbY30q5kqN35AKsV/T8JNO6', 'Seller ROG', '+998997501208', 'ACTIVE', now() - interval '5 day'),
--     sellerMac -> password -> seller222
    ('sellerMac@gmail.com', '$2a$12$Q2mfsQa6Q111RRisx5/vH.6Isgzm/7/f0EOcrLf6Em8LQYbFDQClS', 'Seller Mac', '+998991221206', 'ACTIVE', now() - interval '6 day'),
--     sellerMix -> password -> seller333
    ('sellerMix@gmail.com', '$2a$12$j8CWnVeFutvcDwk2AzgTHuglfVwY/Hlxski.NTsF0KqYGcSSaHJPu', 'Seller Mix', '+998770011770', 'ACTIVE', now() - interval '8 day'),
--     admin1 -> password -> admin111
    ('admin@gmail.com',  '$2a$12$jBNQC/P.w1cUHeE584r2b.xjOn7S.Ygm8H0mdFbWW4JItryeIvYXq', 'Admin 0', '+998991221206', 'ACTIVE', now() - interval '10 day');

-- ─────────────────────────────────────────
-- 3. USER ROLES
-- ─────────────────────────────────────────
-- Buyers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
                  WHERE u.email IN ('buyer1@gmail.com', 'buyer2@gmail.com', 'buyer3@gmail.com') AND r.name = 'BUYER';

-- Seller gets both BUYER and SELLER roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
                  WHERE u.email = 'seller@gmail.com' AND r.name IN ('BUYER', 'SELLER');

-- Admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
                  WHERE u.email = 'admin@gmail.com' AND r.name = 'ADMIN';

-- ─────────────────────────────────────────
-- 4. Address data
-- ─────────────────────────────────────────
INSERT INTO addresses (label, street, city, state, zip, country, is_default, user_id)
VALUES ('HOME', '123 Main St', 'New York', 'NY', '10001', 'USA', true,
        (SELECT id FROM users WHERE email = 'buyer1@gmail.com')),
       ('WORK', '456 Office Ave', 'New York', 'NY', '10002', 'USA', false,
        (SELECT id FROM users WHERE email = 'sellerMac@gmail.com')),
       ('HOME', '789 Oak Lane', 'Los Angeles', 'CA', '90001', 'USA', true,
        (SELECT id FROM users WHERE email = 'buyer2@gmail.com')),
       ('HOME', '321 Pine Rd', 'Chicago', 'IL', '60601', 'USA', true,
        (SELECT id FROM users WHERE email = 'buyer3@gmail.com')),
       ('WORK', 'IT Park', 'Toshkent', 'Yakkasaroy', '10000', 'Uzbekistan', true,
        (SELECT id FROM users WHERE email = 'sellerRog@gmail.com'));


-- ─────────────────────────────────────────
-- 5. SELLER PROFILES
-- ─────────────────────────────────────────
INSERT INTO seller_profiles (store_name, store_description, average_rating, total_sales, user_id)
VALUES (
           'ROG & Asus Store',
           'Republic of Gamers',
           3.2,
           5,
           (SELECT id FROM users WHERE email = 'sellerRog@gmail.com')
       ),
    (
           'MacBro',
           'MacBook & Iphone',
           3.3,
           6,
           (SELECT id FROM users WHERE email = 'sellerMac@gmail.com')
       ),
    (
           'Mix Tech',
           'Laptop & Phones',
           3.4,
           7,
           (SELECT id FROM users WHERE email = 'sellerMix@gmail.com')
       );

-- ─────────────────────────────────────────
--  6. Parent categories
-- ─────────────────────────────────────────
INSERT INTO categories (name, parent_category_id)
VALUES ('Electronics', NULL),
       ('Clothing', NULL),
       ('Books', NULL);

-- ─────────────────────────────────────────
-- 7. Child categories
-- ─────────────────────────────────────────
INSERT INTO categories (name, parent_category_id)
VALUES ('Laptops', (SELECT id FROM categories WHERE name = 'Electronics')),
       ('Phones', (SELECT id FROM categories WHERE name = 'Electronics')),
       ('PCs', (SELECT id FROM categories WHERE name = 'Electronics')),
       ('Men', (SELECT id FROM categories WHERE name = 'Clothing')),
       ('Women', (SELECT id FROM categories WHERE name = 'Clothing')),
       ('Programming', (SELECT id FROM categories WHERE name = 'Books'));

-- ─────────────────────────────────────────
-- 8. sub-child categories
-- ─────────────────────────────────────────
INSERT INTO categories (name, parent_category_id)
VALUES ('Gaming Laptops', (SELECT id FROM categories WHERE name = 'Laptops')),
       ('Ultrabooks', (SELECT id FROM categories WHERE name = 'Laptops')),
       ('Android Phones', (SELECT id FROM categories WHERE name = 'Phones')),
       ('iPhones', (SELECT id FROM categories WHERE name = 'Phones'));

-- ─────────────────────────────────────────
-- 9. Products
-- ─────────────────────────────────────────
INSERT INTO products (name, description, base_price, status, seller_id, category_id, created_at)
VALUES ('ROG STRIX G17 2022',
        'High performance gaming laptop with AMD Ryzen 7',
        1200.0,
        'ACTIVE',
        (SELECT id FROM users WHERE email = 'sellerRog@gmail.com'),
        (SELECT id FROM categories WHERE name = 'Gaming Laptops'),
            now() - interval '5 day'),
       ('MacBook Pro 14',
        'Apple MacBook Pro with M3 chip',
        1999.0,
        'ACTIVE',
        (SELECT id FROM users WHERE email = 'sellerMac@gmail.com'),
        (SELECT id FROM categories WHERE name = 'Ultrabooks'),
        now() - interval '1 day'),
       ('Samsung Galaxy S24',
        'Latest Samsung flagship smartphone',
        999.0,
        'ACTIVE',
        (SELECT id FROM users WHERE email = 'sellerMix@gmail.com'),
        (SELECT id FROM categories WHERE name = 'Android Phones'),
        now() - interval '3 day'),
       ('iPhone 15 Pro',
        'Apple iPhone 15 Pro with titanium design',
        1199.0,
        'ACTIVE',
        (SELECT id FROM users WHERE email = 'sellerMac@gmail.com'),
        (SELECT id FROM categories WHERE name = 'iPhones'),
        now() - interval '1 day'),
       ('Dell XPS 15',
        'Premium ultrabook for professionals',
        1499.0,
        'ACTIVE',
        (SELECT id FROM users WHERE email = 'sellerMix@gmail.com'),
        (SELECT id FROM categories WHERE name = 'Ultrabooks'),
        now() - interval '2 day');

-- ─────────────────────────────────────────
-- 10. Product variants
-- ─────────────────────────────────────────
INSERT INTO product_variants (sku, price, stock_quantity, product_id)
VALUES

-- 1. ROG STRIX variants
('ROG-G17-RTX3050',
 1250.0,
 10,
 (SELECT id FROM products WHERE name = 'ROG STRIX G17 2022')),
('ROG-G17-RTX3070',
 1450.0,
 5,
 (SELECT id FROM products WHERE name = 'ROG STRIX G17 2022')),

-- 2. MacBook variants
('MBP-14-M3-8GB',
 1999.0,
 8,
 (SELECT id FROM products WHERE name = 'MacBook Pro 14')),
('MBP-14-M3-16GB',
 2299.0,
 4,
 (SELECT id FROM products WHERE name = 'MacBook Pro 14')),

-- 3. Samsung variants
('S24-128GB-BLACK',
 999.0,
 15,
 (SELECT id FROM products WHERE name = 'Samsung Galaxy S24')),
('S24-256GB-WHITE',
 1099.0,
 10,
 (SELECT id FROM products WHERE name = 'Samsung Galaxy S24'));


-- ─────────────────────────────────────────
-- 11. Variant attributes
-- ─────────────────────────────────────────
INSERT INTO variant_attributes (variant_id, attribute_key, attribute_value)
VALUES
-- ROG RTX 3050
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3050'), 'CPU', 'AMD Ryzen 7 6800H'),
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3050'), 'GPU', 'RTX 3050 4GB'),
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3050'), 'RAM', '16GB DDR5'),
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3050'), 'Storage', '512GB SSD'),

-- ROG RTX 3070
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3070'), 'CPU', 'AMD Ryzen 9 6900HX'),
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3070'), 'GPU', 'RTX 3070 8GB'),
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3070'), 'RAM', '32GB DDR5'),
((SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3070'), 'Storage', '1TB SSD'),

-- MacBook 8GB
((SELECT id FROM product_variants WHERE sku = 'MBP-14-M3-8GB'), 'Chip', 'Apple M3'),
((SELECT id FROM product_variants WHERE sku = 'MBP-14-M3-8GB'), 'RAM', '8GB'),
((SELECT id FROM product_variants WHERE sku = 'MBP-14-M3-8GB'), 'Storage', '512GB SSD'),

-- Samsung Black
((SELECT id FROM product_variants WHERE sku = 'S24-128GB-BLACK'), 'Storage', '128GB'),
((SELECT id FROM product_variants WHERE sku = 'S24-128GB-BLACK'), 'Color', 'Phantom Black'),
((SELECT id FROM product_variants WHERE sku = 'S24-128GB-BLACK'), 'RAM', '8GB');


-- ─────────────────────────────────────────
-- 12. Cart data // without values this syntax insert one cart per user / with values insert one
-- ─────────────────────────────────────────
INSERT INTO carts (user_id, created_at) SELECT id, CURRENT_TIMESTAMP FROM users
         WHERE email IN (
                         'buyer1@gmail.com',
                         'buyer2@gmail.com',
                         'buyer3@gmail.com',
                         'sellerMix@gmail.com'
             );


-- ─────────────────────────────────────────
-- 13. Cart items
-- ─────────────────────────────────────────
INSERT INTO cart_items (quantity, cart_id, product_variant_id, added_at)
VALUES (2,
        (SELECT id FROM carts WHERE user_id = (SELECT id FROM users WHERE email = 'buyer1@gmail.com')),
        (SELECT id FROM product_variants WHERE sku = 'ROG-G17-RTX3050'),
        now() - interval '1 day'),
       (1,
        (SELECT id FROM carts WHERE user_id = (SELECT id FROM users WHERE email = 'buyer1@gmail.com')),
        (SELECT id FROM product_variants WHERE sku = 'S24-128GB-BLACK'),
        now() - interval '2 day'),
       (1,
        (SELECT id FROM carts WHERE user_id = (SELECT id FROM users WHERE email = 'buyer2@gmail.com')),
        (SELECT id FROM product_variants WHERE sku = 'MBP-14-M3-8GB'),
        now() - interval '1 day');
