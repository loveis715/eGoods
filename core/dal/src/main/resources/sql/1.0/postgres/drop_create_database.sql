-- The database has to be dropped first
drop database egoods;

-- The login can now be dropped and recreated
drop user egoods;
create user egoods;

-- Password = egoods
-- command: md5 -s egoods
--          MD5 ("egoods") = 3750c667d5cd8aecc0a9213b362066e9
--ALTER ROLE egoods LOGIN ENCRYPTED PASSWORD '3750c667d5cd8aecc0a9213b362066e9' VALID UNTIL 'infinity';
ALTER ROLE egoods WITH ENCRYPTED PASSWORD 'egoods' VALID UNTIL 'infinity';
   
ALTER Role egoods CREATEDB;

create database egoods;
