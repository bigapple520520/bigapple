--以--开头的行为注释行,语句间以go隔开(注意：这不是标准的sql格式，如果--写在一行中间将会被当做sql，--必须独立成行,go也必须独立成行)
--please pay attention to above words！！！UTF-8 coding
--每次升级请不要在原来的db_*.sql中修改，而是新创建一个文件，然后把数据版本＋1

--图片存储表
create table user(
	id char(32) not null,
	name varchar(100) null,
	creation_time datetime not null,
	primary key(id)
)
go