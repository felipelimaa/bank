CREATE TABLE public.transactions
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
        REFERENCES public.accounts (account_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT account_recipient_fk FOREIGN KEY (account_recipient_id)
        REFERENCES public.accounts (account_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT transactiontype_fk FOREIGN KEY (transactiontype_id)
        REFERENCES public.transactions_types (transactiontype_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);