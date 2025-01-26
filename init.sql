CREATE SCHEMA IF NOT EXISTS public;

create table if not exists users
(
    id          serial constraint users_pk primary key,
    telegram_id bigint not null constraint users_pk_2 unique,
    nickname VARCHAR(255) not null constraint users_pk_3 unique,
    created_at  timestamp default CURRENT_TIMESTAMP not null
);

alter table users
    owner to admin;

create table if not exists users_track_lists
(
    user_id integer not null constraint users_track_lists_users_id_fk references users on delete cascade,
    url     text    not null,
    constraint users_track_lists_pk primary key (user_id, url)
);

alter table users_track_lists
    owner to admin;

create table if not exists users_cities
(
    user_id   integer not null constraint users_cities_users_id_fk references users on delete cascade,
    city_name text    not null,
    constraint users_cities_pk primary key (city_name, user_id)
);

alter table users_cities
    owner to admin;

create table if not exists shown_concerts
(
    user_id     integer not null constraint shown_concerts_users_id_fk references users on delete cascade,
    concert_url text    not null,
    constraint shown_concerts_pk primary key (user_id, concert_url)
);

alter table shown_concerts
    owner to admin;
