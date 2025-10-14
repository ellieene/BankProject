--liquibase formatted sql

-- changeset ellieene:add-expiration-date-to-cards
ALTER TABLE card
    ADD COLUMN expiration_date DATE NOT NULL DEFAULT CURRENT_DATE;