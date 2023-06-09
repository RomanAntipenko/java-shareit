DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

create TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL,
CONSTRAINT PK_USER PRIMARY KEY (id),
CONSTRAINT UQ_USER_EMAIL UNIQUE (email));

create TABLE IF NOT EXISTS requests (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
description VARCHAR(512) NOT NULL,
requestor_id BIGINT NOT NULL,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT PK_REQUEST PRIMARY KEY (id),
CONSTRAINT FK_REQUEST_USER FOREIGN KEY (requestor_id) REFERENCES users (id));

create TABLE IF NOT EXISTS items (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
description VARCHAR(512) NOT NULL,
is_available VARCHAR(100) NOT NULL,
owner_id BIGINT NOT NULL,
request_id BIGINT,
CONSTRAINT PK_ITEM PRIMARY KEY (id),
CONSTRAINT FK_ITEM_USER FOREIGN KEY (owner_id) REFERENCES users (id),
CONSTRAINT FK_ITEM_REQUEST FOREIGN KEY (request_id) REFERENCES requests (id));

create TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
item_id BIGINT NOT NULL,
booker_id BIGINT NOT NULL,
state VARCHAR(100) NOT NULL,
CONSTRAINT PK_BOOKING PRIMARY KEY (id),
CONSTRAINT FK_BOOKING_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
CONSTRAINT FK_BOOKING_USER FOREIGN KEY (booker_id) REFERENCES users (id));

create TABLE IF NOT EXISTS comments (
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
text VARCHAR(1000) NOT NULL,
item_id BIGINT NOT NULL,
author_id BIGINT NOT NULL,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
CONSTRAINT PK_COMMENT PRIMARY KEY (id),
CONSTRAINT FK_COMMENT_USER FOREIGN KEY (author_id) REFERENCES users (id),
CONSTRAINT FK_COMMENT_ITEM FOREIGN KEY (item_id) REFERENCES items (id));




