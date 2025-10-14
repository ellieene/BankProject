--liquibase formatted sql

-- changeset ellieene:create-base

CREATE TABLE IF NOT EXISTS "user"
(
    id       UUID PRIMARY KEY,
    login    VARCHAR(255) UNIQUE NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL,
    fio      VARCHAR(255) NOT NULL,
    status  VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS card
(
    id      UUID PRIMARY KEY,
    number  VARCHAR(16) UNIQUE NOT NULL,
    status  VARCHAR(50) NOT NULL,
    balance BIGINT NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS card_block_request
(
    id           UUID PRIMARY KEY,
    card_id      UUID NOT NULL,
    requested_by UUID NOT NULL,
    status       VARCHAR(20) NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    processed_at TIMESTAMP NULL,
    CONSTRAINT fk_card_block_requests_card FOREIGN KEY (card_id) REFERENCES card(id) ON DELETE CASCADE,
    CONSTRAINT fk_card_block_requests_user FOREIGN KEY (requested_by) REFERENCES "user"(id) ON DELETE CASCADE
);

-- rollback commands
-- rollback DROP TABLE card_block_request;
-- rollback DROP TABLE card;
-- rollback DROP TABLE user;