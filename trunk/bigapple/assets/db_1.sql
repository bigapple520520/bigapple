create table test_table(
	id char(32) not null primary key,
	name varchar(40) null,
	creation_time datetime not null
)
go

create table test_table2(
    id char(32) not null primary key,
    name varchar(40) not null
)
go

create table login_user(
	region_id char(6) not null,
	username varchar(30) not null,
	password varchar(100) null,
	auto_login smallint default 0 not null,
	creation_time datetime not null,
	primary key(region_id,username)
)
go