insert into roles (id, name) values (1, 'Adopter');
insert into roles (id, name) values (2, 'Manager');
insert into roles (id, name) values (3, 'Administrator');

insert into shelters (id, email, location, name, phone)
values (1, 'happy-paws@mail.com', 'Bucharest', 'Happy Paws', '0123456789');

insert into pets (id, name, species, age, description, photo, status, sex, shelter_id)
values (1, 'Milo', 'Cat', 3, 'Playful', null, 'Available', 'Male', 1);