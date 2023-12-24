DROP TABLE IF EXISTS juices;

CREATE TABLE juices (
  id int unsigned AUTO_INCREMENT,
  name VARCHAR(20) NOT NULL,
  price INT NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO juices (name, price) VALUES ("コカ・コーラ", 180);
INSERT INTO juices (name, price) VALUES ("バブルマン", 120);
INSERT INTO juices (name, price) VALUES ("ペプシ", 150);
INSERT INTO juices (name, price) VALUES ("ファンタ", 130);
INSERT INTO juices (name, price) VALUES ("レッドブル", 200);
INSERT INTO juices (name, price) VALUES ("モンスター", 220);
INSERT INTO juices (name, price) VALUES ("オロナミンC", 110);
INSERT INTO juices (name, price) VALUES ("リポビタンD", 140);
