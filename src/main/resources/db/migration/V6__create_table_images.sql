CREATE TABLE images (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    position INT NOT NULL,

    CONSTRAINT fk_image_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT uq_product_position
        UNIQUE (product_id, position)
);