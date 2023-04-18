insert into security_group(group_id,description, group_name)
values ('ROLE_PROGRAMMING_CONTEST_MANAGER', 'Managing programming contest', 'PROGRAMMING_CONTEST_MANAGER'),
('ROLE_PROGRAMMING_CONTEST_PARTICIPANT', 'Participant of programming contest', 'PROGRAMMING_CONTEST_PARTICIPANT');

insert into security_permission(permission_id, description) values
('PROGRAMMING_CONTEST_MANAGER', 'Managing programming contest'),
('PROGRAMMING_CONTEST_PARTICIPANT', 'Participant of programming contest');

insert into security_group_permission(group_id, permission_id) values
('ROLE_PROGRAMMING_CONTEST_MANAGER', 'PROGRAMMING_CONTEST_MANAGER'),
('ROLE_PROGRAMMING_CONTEST_PARTICIPANT', 'PROGRAMMING_CONTEST_PARTICIPANT');

insert into user_login_security_group(user_login_id, group_id) values
('admin', 'ROLE_PROGRAMMING_CONTEST_MANAGER'), ('admin', 'ROLE_PROGRAMMING_CONTEST_PARTICIPANT');

-- application
insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_MANAGER', 'MENU', NULL, 'PROGRAMMING_CONTEST_MANAGER',
'Menu  management for programming contest');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_MANAGER_LIST_PROBLEM', 'MENU', 'MENU_PROGRAMMING_CONTEST_MANAGER', 'PROGRAMMING_CONTEST_MANAGER',
'Menu  management for programming contest - list problems');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_MANAGER_CREATE_PROBLEM', 'MENU', 'MENU_PROGRAMMING_CONTEST_MANAGER', 'PROGRAMMING_CONTEST_MANAGER',
'Menu  management for programming contest - create problems');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_MANAGER_LIST_CONTEST', 'MENU', 'MENU_PROGRAMMING_CONTEST_MANAGER', 'PROGRAMMING_CONTEST_MANAGER',
'Menu  management for programming contest - list contests');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_MANAGER_CREATE_CONTEST', 'MENU', 'MENU_PROGRAMMING_CONTEST_MANAGER', 'PROGRAMMING_CONTEST_MANAGER',
'Menu  management for programming contest - create contest');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_MANAGER_IDE', 'MENU', 'MENU_PROGRAMMING_CONTEST_MANAGER', 'PROGRAMMING_CONTEST_MANAGER',
'Menu  management for programming contest - IDE');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_PARTICIPANT', 'MENU', NULL, 'PROGRAMMING_CONTEST_PARTICIPANT',
'Menu  participant for programming contest');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_NOT_REGISTERED_CONTEST', 'MENU', 'MENU_PROGRAMMING_CONTEST_PARTICIPANT', 'PROGRAMMING_CONTEST_PARTICIPANT',
'Menu  participant for programming contest - list not registered contest');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_REGISTERED_CONTEST', 'MENU', 'MENU_PROGRAMMING_CONTEST_PARTICIPANT', 'PROGRAMMING_CONTEST_PARTICIPANT',
'Menu  participant for programming contest - list registered contest');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_PARTICIPANT_IDE', 'MENU', 'MENU_PROGRAMMING_CONTEST_PARTICIPANT', 'PROGRAMMING_CONTEST_PARTICIPANT',
'Menu  participant for programming contest - IDE');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_PARTICIPANT_LIST_PUBLIC_PROBLEM', 'MENU', 'MENU_PROGRAMMING_CONTEST_PARTICIPANT', 'PROGRAMMING_CONTEST_PARTICIPANT',
'Menu  participant for programming contest - list public problems');


-- permision view all contests by admin---
insert into security_permission (permission_id, description) values('ADMIN_VIEW_ALL_PROGRAMMING_CONTESTS','Admin can view all programming contests');
insert into security_group_permission (group_id, permission_id) values('ROLE_FULL_ADMIN','ADMIN_VIEW_ALL_PROGRAMMING_CONTESTS');
insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROGRAMMING_CONTEST_ADMIN_VIEW_ALL_CONTEST', 'MENU', 'MENU_PROGRAMMING_CONTEST_MANAGER', 'ADMIN_VIEW_ALL_PROGRAMMING_CONTESTS',
'Menu  list all programming contests viewed by admin');
