create table carts
(
    id bigserial,
    constraint pk_carts primary key (id)
);

create table products
(
    id   bigserial,
    name varchar(100)   not null,
    cost decimal(10, 2) not null,
    constraint pk_products primary key (id),
    constraint uq_products_name unique (name)
);

create table discounts
(
    code varchar(100),
    due  TIMESTAMP WITH TIME ZONE not null CHECK (due > NOW()),
    constraint pk_discounts primary key (code)
);

create table product_items
(
    cart_id    bigint,
    product_id bigint,
    quantity   int not null default 1,
    constraint fk_cart_product_items foreign key (cart_id) references carts (id),
    constraint fk_product_product_items foreign key (product_id) references products (id),
    constraint pk_product_items primary key (cart_id, product_id)
);

create index ix_product_items_product_id on product_items (product_id);

create table carts_discounts
(
    cart_id       bigint,
    discount_code varchar(100),
    constraint fk_carts_discounts foreign key (cart_id) references carts (id),
    constraint fk_discounts_carts foreign key (discount_code) references discounts (code),
    constraint pk_carts_discounts primary key (cart_id, discount_code)
);

create index ix_carts_discounts_discount_code on carts_discounts (discount_code);