create table if not exists costumer (
    costumerId bigint auto_increment,
    name varchar(50) not null,
    phoneNumber varchar(15) not null,
    emailAddress varchar(50) not null,
    primary key(costumerId)
);