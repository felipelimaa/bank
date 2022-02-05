CREATE TABLE transactions
(
    transaction_id bigint NOT NULL,
    account_sender_id bigint NOT NULL,
    account_recipient_id bigint NOT NULL,
    transactiontype_id bigint NOT NULL,
    transaction_value numeric(19,2) NOT NULL,
    transaction_date timestamp without time zone,
    new_available_credit numeric(19,2) NOT NULL,
    CONSTRAINT transaction_pkey PRIMARY KEY (transaction_id),
    CONSTRAINT account_sender_fk FOREIGN KEY (account_sender_id)
        REFERENCES accounts (account_id),
    CONSTRAINT account_recip_fk FOREIGN KEY (account_recipient_id)
        REFERENCES accounts (account_id),
    CONSTRAINT transactiontype_fk FOREIGN KEY (transactiontype_id)
        REFERENCES transactions_types (transactiontype_id)
);