#!/bin/sh

create() {
    rm -rf mysql/data/*
    touch mysql/data/.gitkeep
    restore
}

restore() {
    docker run --name modeldata -p 3306:3306 -e MYSQL_ROOT_PASSWORD=Kou@9916591569 -e MYSQL_DATABASE=operations -e MYSQL_USER=bits -e MYSQL_PASSWORD=Kou@9916591569 -v $(pwd)/mysql/data:/var/lib/mysql -d mysql
    sleep 30
    docker run -it -e MYSQL_ROOT_PASSWORD=Kou@9916591569 -v $(pwd)/mysql/script:/tmp/ --link modeldata:mysql --rm mysql sh -c 'exec mysql -hmysql -P3306 -ubits -pKou@9916591569 operations < /tmp/audit_data_model.sql'
    #docker run -it -e MYSQL_ROOT_PASSWORD=Kou@9916591569 -v $(pwd)/mysql/script:/tmp/ --link modeldata:mysql --rm mysql sh -c 'exec mysql -hmysql -P3306 -ubits -pKou@9916591569 operations < /tmp/insert_audit_data_model.sql'
    docker stop modeldata
    docker rm modeldata
}

start() {
    docker-compose build
    docker-compose up
}

clean() {
    docker kill $(docker ps -aq)
    docker rm $(docker ps -aq)
    echo "********Removed processes******"
    docker rm -v $(docker ps -a -q -f status=exited)
    echo "********Removed exited volumes********"
    docker rmi $(docker images -f "dangling=true" -q)
    echo "********Removed dangling images*******"
    docker volume rm $(docker volume ls -qf dangling=true)
    echo "********Removed dangling volumes******"
}

startdatabase() {
    docker run -it --link modeldata:mysql --rm mysql sh -c 'exec mysql -hmysql -P3306 -ubits -pKou@9916591569'
}

### main logic ###
case "$1" in
  restore)
        restore
        ;;
  create)
        create
        ;;
  start)
        start
        ;;
  clean)
        clean
        ;;
  startdatabase)
        startdatabase
        ;;
  *)
        echo $"Usage: $0 {start|gradle}"
        exit 1
esac
exit 0
