DROP DATABASE test;

CREATE DATABASE test;

USE test;

INSERT INTO role (id, role) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, role) VALUES (2, 'ROLE_ADMIN');


SELECT * FROM user;
SELECT * FROM users_role;
SELECT * FROM role;