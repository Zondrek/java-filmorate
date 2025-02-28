CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpas
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    duration     INTEGER,
    mpa_id       INTEGER REFERENCES mpas (id)
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INTEGER REFERENCES films (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(255) NOT NULL,
    name     VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friend_links
(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id      INTEGER REFERENCES users (id),
    friend_id      INTEGER REFERENCES users (id),
    is_approved BOOL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER,
    user_id INTEGER,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);