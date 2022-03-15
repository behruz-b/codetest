GRANT CONNECT ON DATABASE news TO test;

CREATE SCHEMA IF NOT EXISTS news AUTHORIZATION test;

CREATE TABLE news.headline (
   link VARCHAR PRIMARY KEY,
   title VARCHAR NOT NULL
);