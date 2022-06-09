

sudo mysql -h localhost -u root -p
create database todolistexamen;
create user todolistexamen;
GRANT USAGE ON *.* TO 'todolistexamen'@localhost IDENTIFIED BY 'todolistexamen';
GRANT USAGE ON *.* TO 'todolistexamen'@'%' IDENTIFIED BY 'todolistexamen';
GRANT ALL privileges ON `todolistexamen`.* TO 'todolistexamen'@localhost;
flush privileges;
