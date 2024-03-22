DROP SCHEMA IF EXISTS socialnetwork;
CREATE SCHEMA socialnetwork DEFAULT CHARACTER SET utf8;
USE socialnetwork;

CREATE TABLE user
(
    id                      BIGINT AUTO_INCREMENT,
    username                   VARCHAR(20) NOT NULL unique,
    password                 VARCHAR(80) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE friendship
(
	user_id        BIGINT NOT NULL,
    friend_id      BIGINT NOT NULL,
    
	FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (friend_id) REFERENCES user (id)
   

)
