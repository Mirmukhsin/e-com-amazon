-- ////////////////////////////////
-- Parent categories
-- ////////////////////////////////
INSERT INTO categories (name, parent_category_id)
VALUES ('Electronics', NULL),
       ('Clothing', NULL),
       ('Books', NULL);

-- ////////////////////////////////
-- child categories
-- ////////////////////////////////
INSERT INTO categories (name, parent_category_id)
VALUES ('Laptops', (SELECT id FROM categories WHERE name = 'Electronics')),
       ('Phones', (SELECT id FROM categories WHERE name = 'Electronics')),
       ('PCs', (SELECT id FROM categories WHERE name = 'Electronics')),
       ('Men', (SELECT id FROM categories WHERE name = 'Clothing')),
       ('Women', (SELECT id FROM categories WHERE name = 'Clothing')),
       ('Programming', (SELECT id FROM categories WHERE name = 'Books'));

-- ////////////////////////////////
-- sub-child categories
-- ////////////////////////////////
INSERT INTO categories (name, parent_category_id)
VALUES ('Gaming Laptops', (SELECT id FROM categories WHERE name = 'Laptops')),
       ('Ultrabooks', (SELECT id FROM categories WHERE name = 'Laptops')),
       ('Android Phones', (SELECT id FROM categories WHERE name = 'Phones')),
       ('iPhones', (SELECT id FROM categories WHERE name = 'Phones'));

-- ////////////////////////////////
-- products
-- ////////////////////////////////
INSERT INTO products (name, description, base_price, status, seller_id, category_id, created_at, updated_at)
VALUES ('ROG STRIX G17 2022',
        'High performance gaming laptop with AMD Ryzen 7',
        1200.0,
        'ACTIVE',
        4,
        (SELECT id FROM categories WHERE name = 'Gaming Laptops'),
        now() - interval '5 day',
        now() - interval '5 day'),

       ('MacBook Pro 14',
        'Apple MacBook Pro with M3 chip',
        1999.0,
        'ACTIVE',
        5,
        (SELECT id FROM categories WHERE name = 'Ultrabooks'),
        now() - interval '1 day',
        now() - interval '1 day'),

       ('Samsung Galaxy S24',
        'Latest Samsung flagship smartphone',
        999.0,
        'ACTIVE',
        6,
        (SELECT id FROM categories WHERE name = 'Android Phones'),
        now() - interval '3 day',
        now() - interval '3 day'),

       ('iPhone 15 Pro',
        'Apple iPhone 15 Pro with titanium design',
        1199.0,
        'ACTIVE',
        5,
        (SELECT id FROM categories WHERE name = 'iPhones'),
        now() - interval '1 day',
        now() - interval '1 day'),

       ('Dell XPS 15',
        'Premium ultrabook for professionals',
        1499.0,
        'ACTIVE',
        6,
        (SELECT id FROM categories WHERE name = 'Ultrabooks'),
        now() - interval '2 day',
        now() - interval '2 day');

-- ////////////////////////////////
-- product variants
-- ////////////////////////////////
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

-- ////////////////////////////////
-- variant attributes
-- ////////////////////////////////
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