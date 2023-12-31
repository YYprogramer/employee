DROP TABLE IF EXISTS employees;

CREATE TABLE employees (
  employeeNumber int unsigned AUTO_INCREMENT,
  name VARCHAR(20) NOT NULL,
  age int NOT NULL,
  PRIMARY KEY(employeeNumber)
);

INSERT INTO employees (name, age) VALUES ("スティーブ", 21);
INSERT INTO employees (name, age) VALUES ("マーク", 20);
INSERT INTO employees (name, age) VALUES ("ジェフ", 30);

