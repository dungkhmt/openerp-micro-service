-- lay ra danh sach nhan su thuoc mot phong ban nao do
select pe.first_name, pe.middle_name, pe.last_name, d.department_name, u.user_login_id
from party_department as pd,
     person as pe,
     department as d,
     user_login as u
where pd.department_id = d.department_id
  and pd.party_id = pe.party_id
  and u.party_id = pe.party_id
  and thru_date is null;
