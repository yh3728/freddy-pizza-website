-- Создание таблицы работников (staff)
CREATE TABLE IF NOT EXISTS staff (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Создание таблицы продуктов (Products)
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    weight INT,
    ingredients VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT,
    category VARCHAR(100) NOT NULL,
    image_path VARCHAR(255) DEFAULT '/uploads/products/image_placeholder.png'
);

-- Создание таблицы заказов (Orders)
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    comment TEXT,
    payment VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tracking_code VARCHAR(6) UNIQUE NOT NULL
);

-- Создание таблицы позиций в заказе (OrderItem)
CREATE TABLE IF NOT EXISTS order_item (
    id BIGSERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);