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
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    duration     INTEGER,
    mpa_id       INTEGER REFERENCES mpa_table (id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES film_table (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre_table (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS user_table
(
    id       INTEGER AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(255) NOT NULL,
    name     VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friend_link_table
(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id      INTEGER REFERENCES user_table (id),
    friend_id      INTEGER REFERENCES user_table (id),
    is_approved BOOL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS like_table (
    film_id INTEGER,
    user_id INTEGER,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film_table(id),
    FOREIGN KEY (user_id) REFERENCES user_table(id)
);