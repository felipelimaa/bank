CREATE TABLE public.transactions_types
(
    transactiontype_id bigint NOT NULL,
    description character varying(255) not null,
    need_recipient character varying(255) not null,
    operation_decrease character varying(255) not null,
    CONSTRAINT transactions_types_pkey PRIMARY KEY (transactiontype_id)
);

INSERT INTO public.transactions_types(transactiontype_id, description, need_recipient, operation_decrease) VALUES (nextval('hibernate_sequence'), 'Depósito', 'false', 'false');
INSERT INTO public.transactions_types(transactiontype_id, description, need_recipient, operation_decrease) VALUES (nextval('hibernate_sequence'), 'Saque', 'false', 'true');
INSERT INTO public.transactions_types(transactiontype_id, description, need_recipient, operation_decrease) VALUES (nextval('hibernate_sequence'), 'Transferência', 'true', 'true');