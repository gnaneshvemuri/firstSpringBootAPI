-- Drop the databse and roles if there are any with the given name
drop database IF EXISTS expensetrackerdb;
drop user IF EXISTS expensetracker;

-- Create user and database with the given name
create user expensetracker with password 'mysecret';
create database expensetrackerdb with template=template0 owner=expensetracker;

-- change to the created database
\c expensetrackerdb;

-- grant all privileges on all tables, sequence to the user
alter default privileges grant all on tables to expensetracker;
alter default privileges grant all on sequences to expensetracker;

-- create tables and adding their foreign keys and primary keys
create table et_users(
user_id integer primary key not null,
first_name varchar(20) not null,
last_name varchar(20) not null,
email varchar(30) not null,
password text not null
);

create table et_categories(
category_id integer primary key not null,
user_id integer not null,
title varchar(20) not null,
description varchar(20) not null
);



alter table et_categories add constraint cat_users_fk
foreign key (user_id) references et_users(user_id);

create table et_transactions(
    transaction_id integer primary key not null,
    category_id integer not null,
    user_id integer not null,
    amount numeric(10,2) not null,
    note varchar(50) not null,
    transaction_date bigint not null
);

alter table et_transactions add constraint trans_category_id_fk
foreign key (category_id) references et_categories(category_id);

alter table et_transactions add constraint trans_user_id_fk
foreign key (user_id) references et_users(user_id);

-- creating sequnces for the tables.
create sequence et_user_seq increment 1 start 1;
create sequence et_categories_seq increment 1 start 1;
create sequence et_transactions_seq increment 1 start 1000;