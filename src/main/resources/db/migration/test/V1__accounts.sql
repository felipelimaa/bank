CREATE TABLE accounts
(
    account_id bigint NOT NULL,
    document_number character varying(255),
    credit_avaiable numeric(19,2) NOT NULL DEFAULT 1000,
    CONSTRAINT accounts_pkey PRIMARY KEY (account_id)
);

CREATE SEQUENCE hibernate_sequence
    INCREMENT 1
    START 1;