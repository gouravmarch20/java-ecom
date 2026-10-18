# FakeCommerce

FakeCommerce is a REST API for an e-commerce store. It is built with Spring Boot 4.1, Java 21, MySQL, JPA/Hibernate and Flyway, and is monitored with New Relic.

## Running

Start MySQL on `localhost:3306` first. The database `fakecommerce` is created automatically, and Flyway runs the migrations on startup.

```bash
./gradlew bootRun
```

`bootRun` attaches the New Relic agent from `newrelic/newrelic.jar`. Set your license key in the environment first, for example `export NEW_RELIC_LICENSE_KEY=...`. Without it the app still runs, but sends nothing to New Relic.

### Development seed data

With the `dev` profile (`SPRING_PROFILES_ACTIVE=dev`, already set in `.env`), Flyway also runs [`db/seed/R__seed_dev_data.sql`](src/main/resources/db/seed/R__seed_dev_data.sql). The script loads:

- 15 stock-market sector categories (IT, Energy, Finance, ...)
- 1000 company products
- 25 orders with their items
- reviews

Without the `dev` profile, only the schema migrations in `db/migration` run.

To reset the dev database, drop it and restart the app. The app recreates the database, and Flyway rebuilds the schema and reloads the seed data:

```sql
DROP DATABASE fakecommerce;
```

The seed script uses `INSERT IGNORE` with fixed ids, so rows that already exist are skipped. Flyway re-runs it only when the file changes.

## Response format

Every endpoint returns the same envelope:

```json
{
  "success": true,
  "message": "Products fetched successfully",
  "error": null,
  "data": { }
}
```

Errors use the same shape with `success: false`:

- 404: resource not found
- 400: validation failed
- 500: any other error

## API

All paths are relative to `/api/v1`.

### Categories

| Method | Path | Body / Params | Description |
|---|---|---|---|
| POST | `/categories` | `{ "name": "Electronics" }` | Create a category (201). `name` is required. |
| GET | `/categories` | – | List all categories |
| GET | `/categories/{id}` | – | Get one category |
| DELETE | `/categories/{id}` | – | Delete a category |

### Products

| Method | Path | Body / Params | Description |
|---|---|---|---|
| GET | `/products` | – | List all products |
| GET | `/products/{id}` | – | Get one product |
| GET | `/products/{id}/details` | – | Get one product with its category name |
| POST | `/products` | see below | Create a product (201) |
| DELETE | `/products/{id}` | – | Delete a product |
| GET | `/products/search` | `?categoryName=Electronics` | List the products in a category |
| GET | `/products/categories` | – | List the names of categories that have at least one product |
| GET | `/products/test` | – | Write test log lines for New Relic |

Create product body:

```json
{
  "title": "iPhone",
  "description": "Smartphone",
  "price": 999.99,
  "categoryId": 1,
  "image": "https://...",
  "rating": 4.5
}
```

- `title`, `price` and `categoryId` are required.
- `price` must be greater than 0.

### Orders

| Method | Path | Body / Params | Description |
|---|---|---|---|
| GET | `/orders` | – | List all orders with their items |
| GET | `/orders/{id}` | – | Get one order with its items |
| POST | `/orders` | see below | Create an order with status `PENDING` (201) |
| PUT | `/orders/{id}` | see below | Update the status and/or the items |
| DELETE | `/orders/{id}` | – | Soft-delete an order (sets `deleted_at`) |
| GET | `/orders/{id}/summary` | – | Get an order with `totalItems` and `totalPrice` |

Create order body:

```json
{
  "orderItems": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3 }
  ]
}
```

`quantity` defaults to 1 when it is left out.

Update order body:

```json
{
  "status": "SHIPPED",
  "orderItems": [
    { "productId": 1, "quantity": 2, "action": "ADD" },
    { "productId": 3, "action": "DECREMENT" }
  ]
}
```

- `status` and `orderItems` are both optional.
- `status` can be `PENDING`, `SHIPPED`, `DELIVERED` or `CANCELLED`.
- `action` can be:

| Action | Effect |
|---|---|
| `ADD` | Adds `quantity` (default 1). Creates the item if it is not in the order. |
| `REMOVE` | Removes the item from the order. |
| `INCREMENT` | Adds 1 to the item's quantity. |
| `DECREMENT` | Subtracts 1 from the item's quantity. Removes the item when its quantity reaches 0. |

### Reviews

| Method | Path | Body / Params | Description |
|---|---|---|---|
| POST | `/reviews` | see below | Create a review (201) |
| GET | `/reviews` | – | List all reviews |
| GET | `/reviews/{id}` | – | Get one review |
| GET | `/reviews/product/{productId}` | – | List the reviews for a product |
| GET | `/reviews/order/{orderId}` | – | List the reviews for an order |
| DELETE | `/reviews/{id}` | – | Delete a review |

Create review body:

```json
{
  "orderId": 1,
  "productId": 1,
  "userId": "gourav",
  "rating": 4.5,
  "comment": "Great product"
}
```

- All fields are required.
- `rating` must be between 1 and 5.
- `userId` and `comment` can be at most 255 characters.
- `userId` is free text for now, because the app has no users or login yet.

The API rejects a review in these cases:

| Case | Response |
|---|---|
| The order or product does not exist | 404 |
| The product is not part of that order | 400 |
| The same `userId` already reviewed that product in that order | 400 |
| A field is missing or invalid | 400, with a message for each invalid field in `data` |

### Health

| Method | Path | Description |
|---|---|---|
| GET | `/actuator/health` | Spring Boot Actuator health check |
