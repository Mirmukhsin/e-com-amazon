-- ////////////////////////////////
-- cart data
-- ////////////////////////////////
INSERT INTO carts (user_id, created_at)
values (1, CURRENT_TIMESTAMP),
       (2, CURRENT_TIMESTAMP),
       (3, current_timestamp);

-- ////////////////////////////////
-- cart items
-- ////////////////////////////////
INSERT INTO cart_items (quantity, cart_id, product_variant_id, added_at)
VALUES (2,
        1,
        1,
        now() - interval '1 day'),

       (1,
        2,
        2,
        now() - interval '2 day'),

       (1,
        1,
        3,
        now() - interval '1 day');