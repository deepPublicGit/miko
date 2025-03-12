CREATE SCHEMA IF NOT EXISTS miko;

DROP TABLE IF EXISTS miko.status;
CREATE TABLE miko.status(
                   bot_id serial4 NOT NULL,
                   app_id serial4 NOT NULL,
                   status int NOT NULL,
                   date_updated DATE,
                   PRIMARY KEY (bot_id, app_id)
);
