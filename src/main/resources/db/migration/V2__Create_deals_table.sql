CREATE TABLE deals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id),
    buy_quantity INTEGER NOT NULL,
    get_quantity INTEGER NOT NULL,
    discount_percentage DECIMAL(5,2) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_delete BOOLEAN default false
);