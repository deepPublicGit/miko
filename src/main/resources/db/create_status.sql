DROP TABLE IF EXISTS status;
CREATE TABLE status(
                   bot_id serial4 NOT NULL,
                   app_id serial4 NOT NULL,
                   status int NOT NULL,
                   date_updated DATE,
                   PRIMARY KEY (bot_id, app_id)
);
