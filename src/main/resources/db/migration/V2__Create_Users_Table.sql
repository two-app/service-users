CREATE TABLE `users`.`user`
(
  `uid`   INT          NOT NULL AUTO_INCREMENT,
  `pid`   INT          NULL,
  `cid`   INT          NULL,
  `email` VARCHAR(150) NOT NULL, # TODO Lookup length of email
  `name`  VARCHAR(45)  NOT NULL,
  `age`   INT          NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE INDEX `uid_UNIQUE` (`uid` ASC) VISIBLE,
  INDEX `fk_users_couples_cid_idx` (`cid` ASC) VISIBLE,
  CONSTRAINT `fk_users_couples_cid`
    FOREIGN KEY (`cid`)
      REFERENCES `users`.`couple` (`cid`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION
);
