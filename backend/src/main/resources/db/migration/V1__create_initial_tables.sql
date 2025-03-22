-- V1__Create_tables_and_indexes.sql

-- Создание таблицы работников (staff)
CREATE TABLE staff (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'STAFF'
);

-- Создание таблицы продуктов (Product)
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT false,
    category VARCHAR(100) NOT NULL
);

-- Создание таблицы заказов (Order)
CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    comment TEXT
);

-- Создание таблицы позиций в заказе (OrderItem)
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES "order" (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);