DROP TABLE IF EXISTS app_list;
CREATE TABLE app_list(
                   app_id serial4 NOT NULL PRIMARY KEY,
                   app_name VARCHAR(100),
                   version int,
                   app_url VARCHAR(300),
                   date_added DATE,
                   date_updated DATE
);
