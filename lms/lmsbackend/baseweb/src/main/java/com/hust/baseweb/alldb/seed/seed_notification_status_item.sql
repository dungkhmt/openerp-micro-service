insert into status_type(status_type_id,description) values('NOTIFICATION_STATUS','Status for notification');

insert into status_item(status_id,status_type_id,description) values('NOTIFICATION_CREATED','NOTIFICATION_STATUS','Thông báo được tạo ra');
insert into status_item(status_id,status_type_id,description) values('NOTIFICATION_READ','NOTIFICATION_STATUS','Thông báo được đọc');
