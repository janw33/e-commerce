CREATE TABLE brand_category_suggestions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    category_group VARCHAR(100) NOT NULL,

    CONSTRAINT fk_brand_category_suggestion_brand
        FOREIGN KEY (brand_id)
        REFERENCES brands(id),

    CONSTRAINT uk_brand_category_group
        UNIQUE (brand_id, category_group)
);