ALTER TABLE public.hr_checkinout
    ADD FOREIGN KEY (user_id) REFERENCES user_login(user_login_id);

ALTER TABLE public.hr_staff
    ADD FOREIGN KEY (user_login_id) REFERENCES user_login(user_login_id)