CREATE SCHEMA IF NOT EXISTS miko;

DROP TABLE IF EXISTS miko.app_list;
CREATE TABLE miko.app_list(
                   app_id serial4 NOT NULL PRIMARY KEY,
                   app_name VARCHAR(100),
                   version int,
                   app_url VARCHAR(300),
                   date_added TIMESTAMP,
                   date_updated TIMESTAMP
);
