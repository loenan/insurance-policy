--liquibase formatted sql

-- changeset loenan:001_create_table_insurance_policy
-- comment Create table
SET search_path TO insurance_policy;

CREATE TABLE insurance_policy (
    id          SERIAL          NOT NULL,
    name        VARCHAR(200)    NOT NULL,
    status      VARCHAR(10)     NOT NULL,
    start_date  DATE            NOT NULL,
    end_date    DATE            NOT NULL,
    creation    TIMESTAMP WITH TIME ZONE    NOT NULL    DEFAULT timezone('utc'::text, now()),
    last_update TIMESTAMP WITH TIME ZONE    NOT NULL    DEFAULT timezone('utc'::text, now()),
    CONSTRAINT pk_insurance_policy PRIMARY KEY (id),
    CONSTRAINT ck_insurance_policy_status CHECK (status in ('ACTIVE', 'INACTIVE'))
);

-- rollback DROP TABLE IF EXISTS insurance_policy.insurance_policy;
