
-- won matches per robot
select r.name, r.id, count(m) as win_count
from robot r
join match m on r.id = m.winner_id
group by r.name, r.id
order by win_count;

-- won matches per robot in matches with specified robot count
with matches_of_any as (
    select m.id, m.winner_id, count(mr) as robot_count
    from match m
    join match_robot mr on m.id = mr.match_id
    group by m.id, m.winner_id
), matches_of_n as (
    select * from matches_of_any
    where robot_count = 2
)
select r.name, r.id, count(mon) as win_count
from robot r
join matches_of_n mon on mon.winner_id = r.id
group by r.name, r.id
order by win_count desc;

-- match outcome distribution
select outcome, count(m) as count
from match m
group by outcome;

-- get number of robots in matches won by specified robot
with x as (
    select m.id as match_id, count(mr) as robot_count
    from match m
    join match_robot mr on m.id = mr.match_id
    where m.winner_id = uuid_in('5ee91c5d-67b9-437c-bfd0-82f359847e94')
    group by m.id)
select x.robot_count as robot_count_per_match, count(x) as win_count
from x
group by x.robot_count
order by x.robot_count;

-- average number of robots in matches lost by specified robot
with match_lost as (
    select m.id
    from match m
    join match_robot mr on mr.match_id = m.id
    where mr.robot_id = uuid_in('3dc3de7e-1695-4757-84c5-845932ec7b12')
      and m.winner_id <> uuid_in('3dc3de7e-1695-4757-84c5-845932ec7b12')
), robot_count_in_match_lost as (
    select match_lost.id, count(mr) robot_count
    from match_lost
    join match_robot mr on mr.match_id = match_lost.id
    group by match_lost.id
)
select avg(robot_count_in_match_lost.robot_count)
from robot_count_in_match_lost;

-------------------------------------------
-- robot statistics
-------------------------------------------

-- won matches per robot
select r.name, r.id, count(m) as win_count
from robot r
         join match m on r.id = m.winner_id
group by r.name, r.id
order by win_count;

-- matches played and won for each robot
with matches_played as (
    select r.id as robot_id, r.name as robot_name, count(m) as play_count
    from match m
             join match_robot mr on m.id = mr.match_id
             join robot r on r.id = mr.robot_id
    group by r.id, r.name
), matches_won as (
    select r.id as robot_id, r.name as robot_name, count(*) as win_count
    from match m
             join robot r on r.id = m.winner_id
    group by robot_id, robot_name
)
select
    matches_played.robot_id,
    matches_played.robot_name,
    matches_played.play_count,
    matches_won.win_count,
    matches_won.win_count * 100.0 / matches_played.play_count as win_percent
from matches_played, matches_won
where matches_played.robot_id = matches_won.robot_id
order by win_percent desc;

-- matches with specified robot count played and won by single robot
with matches_of_any as (
    select m.id, m.winner_id, count(mr) as robot_count
    from match m
             join match_robot mr on m.id = mr.match_id
    group by m.id, m.winner_id
), matches_of_n as (
    select * from matches_of_any
    where robot_count = 2
), matches_played as (
    select count(m) play_count
    from matches_of_n m
             join match_robot mr on m.id = mr.match_id
    where mr.robot_id = uuid_in('3fc309a7-ee7c-4f52-8ede-324d98db528e')
), matches_won as (
    select count(*) win_count
    from matches_of_n m
    where m.winner_id = uuid_in('3fc309a7-ee7c-4f52-8ede-324d98db528e')
)
select
    matches_played.play_count,
    matches_won.win_count,
    matches_won.win_count * 100.0 / matches_played.play_count as win_percent
from matches_played, matches_won;

-- matches played and won by single robot
with matches_played as (
    select count(m) play_count
    from match m
             join match_robot mr on m.id = mr.match_id
    where mr.robot_id = uuid_in('3fc309a7-ee7c-4f52-8ede-324d98db528e')
), matches_won as (
    select count(*) win_count
    from match m
    where m.winner_id = uuid_in('3fc309a7-ee7c-4f52-8ede-324d98db528e')
)
select
    matches_played.play_count,
    matches_won.win_count,
    matches_won.win_count * 100.0 / matches_played.play_count as win_percent
from matches_played, matches_won;

-- get number of robots in matches won by specified robot
with x as (
    select m.id as match_id, count(mr) as robot_count
    from match m
             join match_robot mr on m.id = mr.match_id
    where m.winner_id = uuid_in('5ee91c5d-67b9-437c-bfd0-82f359847e94')
    group by m.id)
select x.robot_count as robot_count_per_match, count(x) as win_count
from x
group by x.robot_count
order by x.robot_count;

-- average number of robots in matches lost by specified robot
with match_lost as (
    select m.id
    from match m
             join match_robot mr on mr.match_id = m.id
    where mr.robot_id = uuid_in('3dc3de7e-1695-4757-84c5-845932ec7b12')
      and m.winner_id <> uuid_in('3dc3de7e-1695-4757-84c5-845932ec7b12')
), robot_count_in_match_lost as (
    select match_lost.id, count(mr) robot_count
    from match_lost
             join match_robot mr on mr.match_id = match_lost.id
    group by match_lost.id
)
select avg(robot_count_in_match_lost.robot_count)
from robot_count_in_match_lost;

-------------------------------------------
-- match statistics
-------------------------------------------

-- match outcome distribution
select outcome, count(m) as count
from match m
group by outcome;

select count(*)
from match m
where m.duration_millis > 60*1000;