-- A single person must not be able to review the same product in the same order twice.
-- The ReviewService checks this before inserting, but two requests arriving at the same
-- moment can both pass that check (race condition). This UNIQUE index makes the database
-- itself reject the second insert.
--
-- The index is composite in the order (order_id, product_id, user_id) so it also speeds up
-- the existsByOrderIdAndProductIdAndUserId lookup that runs on every POST /api/v1/reviews.
ALTER TABLE reviews
    ADD CONSTRAINT uq_review_order_product_user UNIQUE (order_id, product_id, user_id);
