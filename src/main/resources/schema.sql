CREATE TABLE IF NOT EXISTS genre_table
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_table
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_table
(
    id           INTEGER PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    duration     INTEGER,
    genre_id     INTEGER REFERENCES genre_table (id),
    mpa_id       INTEGER REFERENCES mpa_table (id)
);

CREATE TABLE IF NOT EXISTS user_table
(
    id       INTEGER PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(255) NOT NULL,
    name     VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friend_link_table
(
    id          INTEGER PRIMARY KEY,
    user_1      INTEGER REFERENCES user_table (id),
    user_2      INTEGER REFERENCES user_table (id),
    is_approved BOOL DEFAULT FALSE
);