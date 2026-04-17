INSERT INTO roles (name)
VALUES ('BUYER'),
       ('SELLER'),
       ('ADMIN');

-- USERS

INSERT INTO auth_users (email, password, status)
VALUES
--     buyer1 -> password -> buyer111
('buyer1@gmail.com', '$2a$12$Cf.TFvXHLAHfkcCIW87WQuL/tr2i7f2IjnrESewXCU6ROZ22mPG7.', 'ACTIVE'),

--     buyer2 -> password -> buyer222
('buyer2@gmail.com', '$2a$12$/3N0Zy04hdMURj17LRuLrOR/oa3RLIn8sM0W14wA7A6mRDfmfjpO.', 'ACTIVE'),

--     buyer3 -> password -> buyer333
('buyer3@gmail.com', '$2a$12$L9ANKXG5yq44qb8Q4jKX8uptS17S/OqnVqrniDfAwJXHgw9dUYp5y', 'ACTIVE'),

--     sellerRog -> password -> seller111
('sellerRog@gmail.com', '$2a$12$luPh45DELgj3vOBVpkuGY.MhsoG0t8sbY30q5kqN35AKsV/T8JNO6', 'ACTIVE'),

--     sellerMac -> password -> seller222
('sellerMac@gmail.com', '$2a$12$Q2mfsQa6Q111RRisx5/vH.6Isgzm/7/f0EOcrLf6Em8LQYbFDQClS', 'ACTIVE'),

--     sellerMix -> password -> seller333
('sellerMix@gmail.com', '$2a$12$j8CWnVeFutvcDwk2AzgTHuglfVwY/Hlxski.NTsF0KqYGcSSaHJPu', 'ACTIVE'),

--     admin1 -> password -> admin111
('admin@gmail.com', '$2a$12$jBNQC/P.w1cUHeE584r2b.xjOn7S.Ygm8H0mdFbWW4JItryeIvYXq', 'ACTIVE');

-- user roles

-- Buyers
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM auth_users u
         CROSS JOIN roles r
WHERE u.email IN ('buyer1@gmail.com', 'buyer2@gmail.com', 'buyer3@gmail.com')
  AND r.name = 'BUYER';

-- Seller gets both BUYER and SELLER roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM auth_users u
         CROSS JOIN roles r
WHERE u.email in ('sellerRog@gmail.com', 'sellerMac@gmail.com', 'sellerMix@gmail.com') AND r.name IN ('BUYER', 'SELLER');

-- Admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM auth_users u
         CROSS JOIN roles r
WHERE u.email = 'admin@gmail.com'
  AND r.name = 'ADMIN';

