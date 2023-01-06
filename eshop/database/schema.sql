create database eshop;
use eshop;

create table customers (
    name varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,

    primary key(name)
);

insert into customers(name, address, email) 
values("fred", "201 Cobblestone Lane", "fredflintstone@bedrock.com"),
("sherlock", "221B Baker Street, London", "sherlock@consultingdetective.org"),
("spongebob", "124 Conch Street, Bikini Bottom", "spongebob@yahoo.com"),
("jessica", "698 Candlewood Land, Cabot Cove", "fletcher@gmail.com"),
("dursley", "4 Privet Drive, Little Whinging, Surrey", "dursley@gmail.com");

create table orders (
    order_id varchar(8) not null,
    name varchar(32) not null,
    itemname varchar(128) not null,
    quantity int not null,

    constraint fk_order foreign key (name)
    references customers(name)
);

create table order_status (
    order_id varchar(8) not null,
    delivery_id varchar(128),
    status varchar(128) CHECK(status='pending' or status='dispatched'),
    status_update DateTime DEFAULT CURRENT_TIMESTAMP
);