CREATE TABLE likes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    CONSTRAINT fk_like_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_like_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT uq_user_product
        UNIQUE (user_id, product_id)
);