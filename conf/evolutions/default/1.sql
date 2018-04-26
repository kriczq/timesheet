# User schema

# --- !Ups
create table user (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `email` TEXT NOT NULL,
  `name` TEXT NOT NULL,
  `password` TEXT NOT NULL
);

# --- !Downs
drop table `user`