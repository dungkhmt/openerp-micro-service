
--category
INSERT INTO public.backlog_task_category
    (backlog_task_category_id, backlog_task_category_name)
VALUES ('BUG', 'Lỗi')
INSERT INTO public.backlog_task_category
    (backlog_task_category_id, backlog_task_category_name)
VALUES ('REQUEST', 'Yêu cầu');
INSERT INTO public.backlog_task_category
    (backlog_task_category_id, backlog_task_category_name)
VALUES ('TASK', 'Công việc');
INSERT INTO public.backlog_task_category
    (backlog_task_category_id, backlog_task_category_name)
VALUES ('OTHER', 'Khác');

--priority
INSERT INTO public.backlog_task_priority
    (backlog_task_priority_id, backlog_task_priority_name)
VALUES ('HIGH', 'Cao');
INSERT INTO public.backlog_task_priority
    (backlog_task_priority_id, backlog_task_priority_name)
VALUES ('NORMAL', 'Trung bình');
INSERT INTO public.backlog_task_priority
    (backlog_task_priority_id, backlog_task_priority_name)
VALUES ('LOW', 'Thấp');

--status_type
INSERT INTO public.status_type
    (status_type_id, description)
VALUES ('BACKLOG_STATUS', 'Backlog status');

--status_item
INSERT INTO public.status_item
    (status_id, status_type_id, status_code, description)
VALUES ('ASSIGNMENT_ACTIVE', 'BACKLOG_STATUS', 'ACTIVE', 'Đang kích hoạt');
INSERT INTO public.status_item
    (status_id, status_type_id, status_code, description)
VALUES ('ASSIGNMENT_INACTIVE', 'BACKLOG_STATUS', 'INACTIVE', 'Không hoạt động');
INSERT INTO public.status_item
    (status_id, status_type_id, status_code, description)
VALUES ('TASK_OPEN', 'BACKLOG_STATUS', 'OPEN', 'Mở');
INSERT INTO public.status_item
    (status_id, status_type_id, status_code, description)
VALUES ('TASK_CLOSED', 'BACKLOG_STATUS', 'CLOSED', 'Đóng');
INSERT INTO public.status_item
    (status_id, status_type_id, status_code, description)
VALUES ('TASK_INPROGRESS', 'BACKLOG_STATUS', 'INPROGRESS', 'Đang xử lý');
INSERT INTO public.status_item
    (status_id, status_type_id, status_code, description)
VALUES ('TASK_RESOLVED', 'BACKLOG_STATUS', 'RESOLVED', 'Đã xử lý');
