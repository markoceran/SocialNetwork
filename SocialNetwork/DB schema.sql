DROP SCHEMA IF EXISTS socialnetwork;
CREATE SCHEMA socialnetwork DEFAULT CHARACTER SET utf8;
USE socialnetwork;

CREATE TABLE user
(
    id                      BIGINT AUTO_INCREMENT,
    name                     VARCHAR(20) NOT NULL,
    lastName                 VARCHAR(20) NOT NULL,
    username                   VARCHAR(20) NOT NULL unique,
    email                   VARCHAR(40) NOT NULL,
    password                 VARCHAR(80) NOT NULL,
    dateOfBirth              DATE NOT NULL,
    phoneNumber              VARCHAR(20) NOT NULL,
    gender                   VARCHAR(6) NOT NULL,

    PRIMARY KEY (id)
);
