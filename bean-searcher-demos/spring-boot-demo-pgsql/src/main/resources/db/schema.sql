DROP TABLE IF EXISTS department;

CREATE TABLE department (
    id integer,
    name character varying(45),
    create_date timestamp with time zone,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS employee;

CREATE TABLE employee (
    id integer,
    name character varying(50),
    age smallint,
    gender character varying(10),
    entry_date timestamp with time zone,
    department_id integer,
    PRIMARY KEY (id)
);
