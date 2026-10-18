-- A category name must be unique. Right now, POST /api/v1/categories accepts
-- a duplicate name (e.g. two "IT" rows with different ids), which splits products
-- across the duplicates and makes search by categoryName confusing.
--
-- Besides the uniqueness constraint, this index backs the join in
-- ProductRepository.findByCategory_Name used by GET /api/v1/products/search?categoryName=...
ALTER TABLE categories
    ADD CONSTRAINT uq_category_name UNIQUE (name);
