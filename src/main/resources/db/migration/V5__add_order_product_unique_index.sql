-- A product must not appear twice in the same order.
-- Besides enforcing that rule, the composite UNIQUE index speeds up
-- existsByOrderIdAndProductId, which runs on every POST /api/v1/reviews
-- (to check the product is actually part of the order being reviewed).
ALTER TABLE order_products
    ADD CONSTRAINT uq_order_product UNIQUE (order_id, product_id);
