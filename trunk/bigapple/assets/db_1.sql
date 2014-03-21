create table test_table(
	id char(32) not null primary key,
	name varchar(40) not null,
	creation_time datetime not null
)
go

create table test_table2(
    id char(32) not null primary key,
    name varchar(40) not null
)
go