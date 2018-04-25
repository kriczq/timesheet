# User schema

# --- !Ups
create table user (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` TEXT NOT NULL,
  `name` TEXT NOT NULL,
  `password` TEXT NOT NULL
);

# --- !Downs
drop table `user`