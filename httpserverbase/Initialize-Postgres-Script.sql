-- Als Postgres User:
create user weatherdb PASSWORD 'weatherdb';
create database weatherdb with owner weatherdb;
create schema weatherdb;

alter schema weatherdb owner to weatherdb;
------------------------------------------------------------
-- Als WeatherDB user:
create table weather
(
    id          serial,
    region      VARCHAR(200) not null,
    city        varchar(200) not null,
    temperature float        not null
);
alter table  weather owner to weatherdb;
grant all on weather to weatherdb;

INSERT INTO weather (id, region, city, temperature) VALUES (DEFAULT, 'Europe', 'Vienna', 28);
INSERT INTO weather (id, region, city, temperature) VALUES (DEFAULT, 'Europe', 'Berlin', 26);
INSERT INTO weather (id, region, city, temperature) VALUES (DEFAULT, 'Asia', 'Tokyo', 18);
INSERT INTO weather (id, region, city, temperature) VALUES (DEFAULT, 'Europe', 'Rome', 35)

