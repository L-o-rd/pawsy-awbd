DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS adoptions;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS shelters;
DROP TABLE IF EXISTS users;

create table roles
(
    id   bigint auto_increment
        primary key,
    name varchar(255) not null,
    constraint uq_roles_name
        unique (name)
);

create table users
(
    account_non_expired     bit          null,
    account_non_locked      bit          null,
    credentials_non_expired bit          null,
    enabled                 bit          null,
    id                      bigint auto_increment
        primary key,
    email                   varchar(255) null,
    first_name              varchar(255) null,
    last_name               varchar(255) null,
    password                varchar(255) not null,
    phone                   varchar(255) null,
    username                varchar(255) not null,
    constraint uq_users_email
        unique (email),
    constraint uq_users_username
        unique (username)
);

create table user_roles
(
    role_id bigint not null,
    user_id bigint not null,
    primary key (role_id, user_id),
    constraint fk_user_roles_role
        foreign key (role_id) references roles (id),
    constraint fk_user_roles_user
        foreign key (user_id) references users (id)
);

create table shelters
(
    id         bigint auto_increment
        primary key,
    manager_id bigint       not null,
    email      varchar(255) null,
    location   varchar(255) null,
    name       varchar(255) null,
    phone      varchar(255) null,
    constraint uq_shelters_manager
        unique (manager_id),
    constraint fk_shelters_manager
        foreign key (manager_id) references users (id)
);

create table reviews
(
    rating     int          null,
    adopter_id bigint       not null,
    created_at datetime(6)  null,
    edited_at  datetime(6)  null,
    id         bigint auto_increment
        primary key,
    shelter_id bigint       not null,
    comment    varchar(255) null,
    constraint uq_reviews_adopter_shelter
        unique (adopter_id, shelter_id),
    constraint fk_reviews_adopter
        foreign key (adopter_id) references users (id),
    constraint fk_reviews_shelter
        foreign key (shelter_id) references shelters (id)
);

create table pets
(
    age         int                                       null,
    id          bigint auto_increment
        primary key,
    shelter_id  bigint                                    not null,
    description varchar(255)                              null,
    name        varchar(255)                              null,
    photo       varchar(255)                              null,
    species     varchar(255)                              null,
    sex         enum ('Female', 'Male', 'None')           null,
    status      enum ('Adopted', 'Available', 'Awaiting') null,
    constraint fk_pets_shelter
        foreign key (shelter_id) references shelters (id)
);

create table appointments
(
    appointment_date  date                                  null,
    adopter_id        bigint                                not null,
    id                bigint auto_increment
        primary key,
    pet_id            bigint                                not null,
    scheduled_at_date datetime(6)                           null,
    status            enum ('Cancelled', 'Done', 'Ongoing') null,
    constraint fk_appointments_adopter
        foreign key (adopter_id) references users (id),
    constraint fk_appointments_pet
        foreign key (pet_id) references pets (id)
);

create table adoptions
(
    adopter_id    bigint                                   not null,
    approval_date datetime(6)                              null,
    id            bigint auto_increment
        primary key,
    pet_id        bigint                                   not null,
    request_date  datetime(6)                              null,
    status        enum ('Approved', 'Pending', 'Rejected') null,
    constraint uq_adoptions_adopter_pet
        unique (adopter_id, pet_id),
    constraint fk_adoptions_pet
        foreign key (pet_id) references pets (id),
    constraint fk_adoptions_adopter
        foreign key (adopter_id) references users (id)
);
