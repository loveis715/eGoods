CREATE TABLE CATEGORY
(
ID SERIAL NOT NULL,
NAME VARCHAR(255) NOT NULL,
LOCK_VERSION INTEGER NOT NULL DEFAULT 1,
PARENT_CATEGORY_ID INTEGER,
PRIMARY KEY (ID)
);

ALTER TABLE CATEGORY ADD FOREIGN KEY (PARENT_CATEGORY_ID) REFERENCES CATEGORY (ID);