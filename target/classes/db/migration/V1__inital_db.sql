CREATE TABLE reminders(
id bigint auto_increment NOT NULL,
name varchar(128) NOT NULL,
dateselected DATE,
timeselected TIME,
PRIMARY KEY(id)
);