CREATE TABLE IF NOT EXISTS OWNER(id bigint auto_increment PRIMARY KEY, firstName VARCHAR(255));
CREATE TABLE IF NOT EXISTS PET(id bigint auto_increment PRIMARY KEY, name VARCHAR(255), owner_id LONG);