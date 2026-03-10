DROP TABLE IF EXISTS Handles CASCADE;
DROP TABLE IF EXISTS Needs CASCADE;
DROP TABLE IF EXISTS Works_On CASCADE;
DROP TABLE IF EXISTS Owns CASCADE;

DROP TABLE IF EXISTS Service CASCADE;
DROP TABLE IF EXISTS Car CASCADE;
DROP TABLE IF EXISTS Mechanic CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;

CREATE TABLE Customer (
  Phone_Num   TEXT PRIMARY KEY,
  First_Name  TEXT NOT NULL,
  Last_Name   TEXT NOT NULL,
  Address     TEXT
);

CREATE TABLE Car (
  VIN     TEXT PRIMARY KEY,
  Year    INTEGER NOT NULL,
  Make    TEXT NOT NULL,
  Model   TEXT NOT NULL
);

CREATE TABLE Mechanic (
  ID          INT PRIMARY KEY,
  First_Name  TEXT NOT NULL,
  Last_Name   TEXT NOT NULL,
  Experience  INTEGER NOT NULL
);

CREATE TABLE Service (
  ID          INT PRIMARY KEY,
  Open_Date   DATE NOT NULL,
  Status      TEXT NOT NULL,
  Odometer    INTEGER,
  Description TEXT
);

CREATE TABLE Owns (
  Phone_Num  TEXT NOT NULL REFERENCES Customer(Phone_Num),
  VIN        TEXT NOT NULL UNIQUE REFERENCES Car(VIN),
  PRIMARY KEY (Phone_Num, VIN)
);

CREATE TABLE Works_On (
  Mechanic_ID INT NOT NULL UNIQUE REFERENCES Mechanic(ID),
  VIN         TEXT NOT NULL UNIQUE REFERENCES Car(VIN),
  PRIMARY KEY (Mechanic_ID, VIN)
);

CREATE TABLE Needs (
  VIN         TEXT NOT NULL REFERENCES Car(VIN),
  Service_ID  INT NOT NULL UNIQUE REFERENCES Service(ID),
  PRIMARY KEY (VIN, Service_ID)
);

CREATE TABLE Handles (
  Mechanic_ID INT NOT NULL REFERENCES Mechanic(ID),
  Service_ID  INT NOT NULL UNIQUE REFERENCES Service(ID),
  Bill        NUMERIC(10,2),
  Closed_Date DATE,
  Comments    TEXT,
  PRIMARY KEY (Mechanic_ID, Service_ID)
);

COPY Customer (
  Phone_Num,
  First_Name,
  Last_Name,
  Address)
FROM '/class/classes/ahern561/CS-166-project/Customer.csv'
WITH DELIMITER ',';

COPY Car (
  VIN,
  Year,
  Make,
  Model)
FROM '/class/classes/ahern561/CS-166-project/Car.csv'
WITH DELIMITER ',';

COPY Mechanic (
  ID,
  First_Name,
  Last_Name,
  Experience)
FROM '/class/classes/ahern561/CS-166-project/Mechanic.csv'
WITH DELIMITER ',';

COPY Service (
  ID,
  Open_Date,
  Status,
  Odometer,
  Description)
FROM '/class/classes/ahern561/CS-166-project/Service.csv'
WITH DELIMITER ',';

COPY Owns (
  Phone_Num,
  VIN)
FROM '/class/classes/ahern561/CS-166-project/Owns.csv'
WITH DELIMITER ',';

COPY Works_On (
  Mechanic_ID,
  VIN)
FROM '/class/classes/ahern561/CS-166-project/Works_On.csv'
WITH DELIMITER ',';

COPY Needs (
  VIN,
  Service_ID)
FROM '/class/classes/ahern561/CS-166-project/Needs.csv'
WITH DELIMITER ',';

COPY Handles (
  Mechanic_ID,
  Service_ID,
  Bill,
  Closed_Date,
  Comments)
FROM '/class/classes/ahern561/CS-166-project/Handles.csv'
WITH DELIMITER ',';