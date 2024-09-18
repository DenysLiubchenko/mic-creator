INSERT INTO carts (id)
VALUES (50),
       (51),
       (52);

INSERT INTO products (id, name, cost)
VALUES (1, 'Laptop', 999.99),
       (2, 'Smartphone', 499.99),
       (3, 'Headphones', 59.99);

INSERT INTO discounts (code, due)
VALUES ('SUMMER21', '2030-01-01T01:01:01Z'),
       ('CODE_2000', '3000-01-01T01:01:01Z');

INSERT INTO product_items (cart_id, product_id, quantity)
VALUES (50, 1, 1),
       (51, 2, 1),
       (52, 2, 1),
       (52, 3, 2);

INSERT INTO carts_discounts (cart_id, discount_code)
VALUES (50, 'SUMMER21'),
       (51, 'CODE_2000'),
       (52, 'SUMMER21');
