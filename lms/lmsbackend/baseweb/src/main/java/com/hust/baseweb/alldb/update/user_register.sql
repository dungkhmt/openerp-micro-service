alter table user_register
    rename column full_name to first_name;

alter table user_register
    add middle_name varchar(100);

alter table user_register
    add last_name varchar(100);

alter table contest_new add count_down number ;
alter table contest_new add started_count_down_time timestamp ;
alter table contest_new add end_time timestamp ;
