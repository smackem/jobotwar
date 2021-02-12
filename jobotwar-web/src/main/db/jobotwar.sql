-----------------------------------------------------------
-- clean up
-----------------------------------------------------------
drop table match_event;
drop table match_robots;
drop table match;
drop table robot;

-----------------------------------------------------------
-- create tables
-----------------------------------------------------------
create table robot (
                       id uuid primary key,
                       name varchar(100),
                       language varchar(20),
                       code text,
                       acceleration real,
                       rgb int4,
                       date_created date,
                       date_modified date
);

create index robot_name on robot (name);

create table match (
                       id uuid primary key,
                       board_width int4,
                       board_height int4,
                       duration_millis int8,
                       max_duration_millis int8,
                       winner_id uuid references robot(id),
                       outcome varchar(20)
);

create index match_winner on match (winner_id);

create table match_robots (
                              robot_id uuid references robot(id),
                              match_id uuid references match(id)
);

create index match_robots_robot_id on match_robots(robot_id);
create index match_robots_match_id on match_robots(match_id);

create table match_event (
                             match_id uuid references match(id),
                             game_time_millis int8,
                             event varchar(200)
);

create index match_event_match_id on match_event(match_id);
