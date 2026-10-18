# Indexing — the simple version

## The problem

POST /api/v1/reviews



When a user posts a review, your code asks the database one question:

> Has this user already reviewed this product in this order?

The SQL for that question is:

```sql
SELECT 1
FROM reviews
WHERE order_id = ?
  AND product_id = ?
  AND user_id = ?;
```

If the answer is yes, the API returns an error. If no, the review is saved.

This question is asked **every single time** someone posts a review. So it needs to be fast.

---

## Think of the database as a notebook

Picture the `reviews` table as a notebook. Each line is one review:

| line | order_id | product_id | user_id | rating | comment |
|---|---|---|---|---|---|
| 1 | 1 | 5 | gourav | 4.5 | nice |
| 2 | 1 | 7 | rahul  | 3.0 | ok |
| 3 | 1 | 5 | priya  | 5.0 | great |
| 4 | 2 | 5 | gourav | 4.0 | good |
| 5 | 1 | 9 | gourav | 2.0 | bad |
| ... (thousands more) | | | | | |

Now you ask:

> **Order 1, product 5, user gourav — is it in the notebook?**

### Without an index: read every line

The database starts at line 1 and reads each line one at a time:

```
line 1: order=1, product=5, gourav → match! stop ✅
```

Lucky this time, it was the first line. But if the match is on line 1000, it reads 1000 lines. If there is no match, it reads **every single line** before saying no.

**This is slow.** And it happens on every new review.

---

### With an index: use a sorted list on the side

An index is a small, sorted side-list the database keeps next to the notebook. We'll add this one:

```sql
CREATE UNIQUE INDEX uq_review_order_product_user
  ON reviews (order_id, product_id, user_id);
```

This builds a sorted list of all three columns, like a phone book:

```
(1, 5, gourav) → line 1
(1, 5, priya ) → line 3
(1, 7, rahul ) → line 2
(1, 9, gourav) → line 5
(2, 5, gourav) → line 4
...
```

Now, when you ask the same question:

> **Order 1, product 5, user gourav — is it in the notebook?**

The database jumps straight to `(1, 5, gourav)` in the sorted list, the same way you flip to "Patel" in a phone book without reading from "A". **One jump, done.**

It doesn't matter if the notebook has 10 or 10 million lines. The index finds the entry in roughly the same time.

---

## Comparing the two

| | Without the index | With the index |
|---|---|---|
| How it works | Reads lines one by one | Jumps straight to the answer |
| Fast when the table is small? | Yes | Yes |
| Still fast when it has 1 million rows? | **No, very slow** | **Yes, still fast** |

---

## Bonus: UNIQUE prevents a hidden bug

Your Java code already checks for duplicate reviews:

```java
if (reviewRepository.existsByOrderIdAndProductIdAndUserId(...)) {
    throw new BadRequestException("already reviewed");
}
reviewRepository.save(review);
```

Looks safe. But what if **two requests arrive at the exact same moment**?

```
Time    Request A                     Request B
----    ---------                     ---------
10:00   check: already reviewed?      check: already reviewed?
10:00     → no                          → no
10:00   save review                   save review
```

Both requests passed the check at the same moment, because neither one had saved anything yet. Both inserts succeed. You now have **two reviews** for the same user, product and order.

Java **cannot** stop this. The two threads don't see each other.

A **UNIQUE** index tells the database itself: "never allow two rows with the same `(order_id, product_id, user_id)`." Now the race looks like this:

```
Request A: insert → saved ✅
Request B: insert → ERROR 1062 "Duplicate entry" ❌
```

The database rejects the second one. Problem solved, by the database, not by Java.

---

## In one sentence

**Without the index:** the database searches by reading every row. Slow when the table is big, and cannot prevent duplicates from two requests at once.

**With the index:** the database jumps straight to the row. Fast at any size, and the UNIQUE part blocks duplicates for good.

---

## The migration

Save this as `src/main/resources/db/migration/V4__add_indexes.sql`:

```sql
ALTER TABLE reviews
  ADD UNIQUE INDEX uq_review_order_product_user (order_id, product_id, user_id);
```

Restart the app. Flyway runs it once, and from then on the duplicate check is fast and safe.
