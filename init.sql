CREATE SCHEMA IF NOT EXISTS public;

create table if not exists users
(
    id          serial constraint users_pk primary key,
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

create table if not exists users_codes
(
    user_id     integer not null constraint users_codes_users_id_fk references users on delete cascade,
    auth_code text not null,
    auth_email text not null,
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    constraint  users_codes_pk primary key (user_id, created_at)
);

alter table users_codes
    owner to admin;

create table if not exists users_firebase_tokens
(
    user_id     integer not null constraint users_firebase_tokens_users_id_fk references users on delete cascade,
    token text not null,
    constraint  users_firebase_tokens_pk primary key (user_id, token)
);

alter table users_firebase_tokens
    owner to admin;

create table if not exists users_access_tokens
(
    user_id     integer not null constraint users_access_tokens_users_id_fk references users on delete cascade,
    token text not null,
    constraint  users_access_tokens_pk primary key (user_id, token)
);

alter table users_access_tokens
    owner to admin;

create table if not exists users_refresh_tokens
(
    user_id     integer not null constraint users_access_tokens_users_id_fk references users on delete cascade,
    token text not null,
    constraint  users_access_tokens_pk primary key (user_id, token)
);

alter table users_access_tokens
    owner to admin;