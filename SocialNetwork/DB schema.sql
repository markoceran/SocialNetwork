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
	id             BIGINT AUTO_INCREMENT,
	user_id        BIGINT NOT NULL,
    friend_id      BIGINT NOT NULL,
    
    PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (friend_id) REFERENCES user (id)
);

CREATE TABLE friendship_request
(
	id             BIGINT AUTO_INCREMENT,
	forUser_id        BIGINT NOT NULL,
    fromUser_id      BIGINT NOT NULL,
    approved		boolean,

    PRIMARY KEY (id),
	FOREIGN KEY (forUser_id) REFERENCES user (id),
    FOREIGN KEY (fromUser_id) REFERENCES user (id)
);

CREATE TABLE post
(
	id            			BIGINT AUTO_INCREMENT,
	content        			VARCHAR(250) NOT NULL,
    postedByUser_id      	BIGINT NOT NULL,
    edited					boolean,

    PRIMARY KEY (id),
	FOREIGN KEY (postedByUser_id) REFERENCES user (id)
);
