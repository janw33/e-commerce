CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(750) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    condition VARCHAR(30) NOT NULL,
    size varchar(30) NOT NULL,
    shipping_size varchar(30) NOT NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    status varchar(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    sold_at TIMESTAMP,

    CONSTRAINT fk_product_user
        FOREIGN KEY (seller_id)
        REFERENCES users(id),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT fk_products_brand
        FOREIGN KEY (brand_id)
        REFERENCES brands(id)
);

CREATE TABLE product_colors (
    product_id BIGINT NOT NULL,
    color VARCHAR(30) NOT NULL,

    PRIMARY KEY (product_id, color),

    CONSTRAINT fk_product_colors_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);

CREATE TABLE product_materials (
    product_id BIGINT NOT NULL,
    material VARCHAR(30) NOT NULL,

    PRIMARY KEY (product_id, material),

    CONSTRAINT fk_product_materials_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);
