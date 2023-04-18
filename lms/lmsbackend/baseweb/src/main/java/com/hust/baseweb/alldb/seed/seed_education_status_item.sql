insert into status_type(status_type_id,description) values('STUDENT_CLASS_STATUS','Status for student-class relation');

insert into status_item(status_id,status_type_id,description) values('REGISTER','STUDENT_CLASS_STATUS','Đăng ký tham gia lớp');
insert into status_item(status_id,status_type_id,description) values('APPROVED','STUDENT_CLASS_STATUS','Phê duyệt tham gia lớp');
insert into status_item(status_id,status_type_id,description) values('REMOVED','STUDENT_CLASS_STATUS','Không Phê duyệt tham gia lớp');
