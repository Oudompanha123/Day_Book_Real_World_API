create table users (
    id            bigserial primary key,
    email         varchar(254) not null unique,
    password_hash varchar(100) not null,
    business_name varchar(60)  not null default '',
    currency      varchar(3)   not null default 'USD',
    language      varchar(2)   not null default 'en' check (language in ('en', 'km')),
    created_at    timestamptz  not null default now()
);

-- One row per income or expense
create table entries (
    id          bigserial primary key,
    user_id     bigint        not null references users (id) on delete cascade,
    kind        varchar(3)    not null check (kind in ('IN', 'OUT')),
    amount      numeric(16,2) not null check (amount > 0),
    note        varchar(120)  not null default '',
    category    varchar(30)   not null,
    pay_method  varchar(20)   not null,
    entry_date  date          not null,
    created_at  timestamptz   not null default now()
);
create index entries_user_date_idx on entries (user_id, entry_date);

-- direction IN = they owe me, OUT = I owe
create table loans (
    id          bigserial primary key,
    user_id     bigint        not null references users (id) on delete cascade,
    direction   varchar(3)    not null check (direction in ('IN', 'OUT')),
    name        varchar(60)   not null,
    amount      numeric(16,2) not null check (amount > 0),
    note        varchar(120)  not null default '',
    lent_on     date          not null,
    due_on      date,
    paid        boolean       not null default false,
    paid_at     timestamptz,
    created_at  timestamptz   not null default now(),
    check (due_on is null or due_on >= lent_on)
);
create index loans_user_idx on loans (user_id);

create table loan_payments (
    id          bigserial primary key,
    loan_id     bigint        not null references loans (id) on delete cascade,
    amount      numeric(16,2) not null check (amount > 0),
    paid_on     date          not null,
    -- set when the payment was also recorded as income/expense
    entry_id    bigint        references entries (id) on delete set null,
    created_at  timestamptz   not null default now()
);
create index loan_payments_loan_idx on loan_payments (loan_id);
