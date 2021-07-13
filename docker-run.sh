docker network create jobotwar-net
docker run -dp 5432:5432 \
    -v /home/pbo/jobotwar/jobotwar-web/target/db:/var/lib/postgresql/data \
    -e POSTGRES_PASSWORD=jobotwar \
    -e POSTGRES_USER=philip \
    -e POSTGRES_DB=jobotwar \
    -e PGDATA=/var/lib/postgresql/data/jobotwar \
    --name jobotwar-postgres \
    --network jobotwar-net
    postgres:latest
docker run -dp 8666:8666 \
    -e DBURL='jdbc:postgresql://jobotwar-postgres/jobotwar?user=philip&password=jobotwar' \
    --network jobotwar-net \
    jobotwar
