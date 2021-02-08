create table robot (
    id uuid primary key,
    name varchar(100),
    language varchar(10),
    acceleration real,
    code text,
    rgb int4,
    dateCreated date,
    dateModified date
);

create index on robot (name);
