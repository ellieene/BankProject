--liquibase formatted sql
--changeset ellieene:alter-card-number-length

ALTER TABLE card
ALTER COLUMN number TYPE VARCHAR(512);