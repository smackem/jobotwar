DB_LOCAL_PATH=/home/pbo/jobotwar/jobotwar-web/target/db
DB_SCHEMA=jobotwar
DB_USER=philip
DB_PWD=jobotwar

docker build --tag jobotwar .
docker network create jobotwar-net
docker run -dp 5432:5432 \
    -v $DB_LOCAL_PATH:/var/lib/postgresql/data \
    -e POSTGRES_PASSWORD=$DB_PWD \
    -e POSTGRES_USER=$DB_USER \
    -e POSTGRES_DB=$DB_SCHEMA \
    -e PGDATA=/var/lib/postgresql/data/$DB_SCHEMA \
    --name jobotwar-postgres \
    --network jobotwar-net
    postgres:latest
docker run -dp 8666:8666 \
    -e DBURL='jdbc:postgresql://jobotwar-postgres/$DB_SCHEMA?user=$DB_USER&password=$DB_PWD' \
    --network jobotwar-net \
    jobotwar
