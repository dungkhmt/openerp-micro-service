-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_VIDEO_WEBCAM_CAPTURE', 'Group capture video webcam');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('VIDEO_WEBCAM_CAPTURE', 'Permission capture video webcam');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_VIDEO_WEBCAM_CAPTURE', 'MENU', NULL, 'VIDEO_WEBCAM_CAPTURE',
'Menu video webcam capture');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_VIDEO_WEBCAM_CAPTURE_EXECUTE', 'MENU', 'MENU_VIDEO_WEBCAM_CAPTURE', 'VIDEO_WEBCAM_CAPTURE',
'Menu execute video webcam capture');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_VIDEO_WEBCAM_CAPTURE_LIST', 'MENU', 'MENU_VIDEO_WEBCAM_CAPTURE', 'VIDEO_WEBCAM_CAPTURE',
'Menu view list and play video webcam capture');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_VIDEO_WEBCAM_CAPTURE', 'VIDEO_WEBCAM_CAPTURE');

--user login security group
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_VIDEO_WEBCAM_CAPTURE', NOW(), NOW());

