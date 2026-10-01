CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    size_type VARCHAR(20) NOT NULL,

    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id)
        REFERENCES categories(id)
);

CREATE UNIQUE INDEX uq_category_name_parent
    ON categories (name, COALESCE(parent_id, 0));