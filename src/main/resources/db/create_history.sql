CREATE SCHEMA IF NOT EXISTS miko;

DROP TABLE IF EXISTS miko.history_status;
CREATE TABLE miko.history_status(
                   bot_id serial4 NOT NULL,
                   app_id serial4 NOT NULL,
                   status int NOT NULL,
                   date_updated DATE NOT NULL
);
