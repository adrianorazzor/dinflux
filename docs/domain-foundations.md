# Domain Foundations

## Users
- Multi-tenant boundary; every resource belongs to exactly one user.
- Stores auth credentials plus preferences (currency, locale).

## Accounts
- Types: `BANK` (cash/transfer) and `CREDIT_CARD` (extra fields `limit`, `closingDay`, `dueDay`).
- Only active accounts accept new transactions.
- Credit-card accounts aggregate statement balances.

## Transactions
- Immutable ledger entries owned by a user and one origin account.
- Fields: `date`, `description`, `amount > 0`, `type`, `account`, optional `counterAccount`, `category`, `tags`.
- `INCOME` increases origin account balance; `EXPENSE` decreases it; `TRANSFER` moves funds from `account` to `counterAccount`.
- Credit-card expenses never count toward cash totals; they post to statements.

## Statements
- Billing cycles for credit-card accounts; identified by `periodStart`, `periodEnd`, `dueDate`, `status (OPEN|CLOSED|PAID)`, running `balance`.
- Each card transaction attaches to the statement covering its purchase date.
- Paying a statement creates a `TRANSFER` (bank → card) that reduces the outstanding balance.

## Recurrences
- Scheduling templates that spawn transactions automatically.
- Hold cadence (`MONTHLY` or `WEEKLY`), prototype transaction data, and target account.
- Generated transactions must respect the same invariants as manual entries.

## Categories
- User-defined taxonomy for incomes/expenses (future-ready for parent/child and color/icon).
- Optional on transactions; used for analytics.

## Invariants
- Transaction `amount` is always positive; sign is implied by `type`.
- Each transaction references exactly one origin account; transfers require a counter account.
- Credit-card expenses appear only on statements until paid via transfer.
- Statement payments must keep card balance ≥ 0 and sync with the origin bank account.
