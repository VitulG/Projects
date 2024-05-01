CREATE TABLE categories
(
    category_id INT    NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    is_deleted  BIT(1) NOT NULL,
    title       VARCHAR(255) NULL,
    CONSTRAINT pk_categories PRIMARY KEY (category_id)
);

CREATE TABLE product
(
    product_id           BIGINT NOT NULL,
    created_at           datetime NULL,
    updated_at           datetime NULL,
    is_deleted           BIT(1) NOT NULL,
    title                VARCHAR(255) NULL,
    price DOUBLE NULL,
    category_category_id INT NULL,
    `description`        VARCHAR(255) NULL,
    image_url            VARCHAR(255) NULL,
    CONSTRAINT pk_product PRIMARY KEY (product_id)
);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY_CATEGORYID FOREIGN KEY (category_category_id) REFERENCES categories (category_id);