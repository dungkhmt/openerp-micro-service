--lấy ra các group của user-login admin
select ug.group_id
from user_login_security_group as ug,
     user_login as u
where ug.user_login_id = u.user_login_id
  and u.user_login_id = 'admin';


--lấy ra các permission của user-login admin
select gp.permission_id
from security_group_permission as gp
where group_id in (select group_id from user_login_security_group where user_login_id = 'admin');


--lấy ra các application la menu (application type la menu) của user-login admin
select *
from application
where permission_id in
      (select permission_id
       from security_group_permission
       where group_id in (select group_id from user_login_security_group where user_login_id = 'admin'))
  and application_type_id = 'MENU';

--lay ra permission cua 1 menu nao do
select a.permission_id
from application as a
where a.application_id = 'MENU_PRODUCT_CREATE';

--lay ra group co quyen voi 1 menu nao do
select gp.group_id
from security_group_permission as gp
where gp.permission_id in (select a.permission_id from application as a where a.application_id = 'MENU_PRODUCT_CREATE');

--lay ra danh sach user_login co quyen voi 1 menu nao do
select ug.user_login_id
from user_login_security_group as ug
where ug.group_id in
      (select gp.group_id
       from security_group_permission as gp
       where gp.permission_id in
             (select a.permission_id from application as a where a.application_id = 'MENU_PRODUCT_CREATE'));

-- lay ra danh sach day du cua nguoi dung he thong (party_id, ho ten day du, user_login_id)
select pe.first_name, pe.middle_name, pe.last_name, u.user_login_id, p.party_id
from party as p,
     user_login as u,
     person as pe
where p.party_id = pe.party_id
  and u.party_id = p.party_id;

