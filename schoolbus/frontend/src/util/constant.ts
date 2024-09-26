export const employee_role_map = [
    { value: 'DRIVER', label: 'Tài xế', color: 'success' },
    { value: 'DRIVER_MATE', label: 'Phụ xe', color: 'warning' },
];

export const bus_status_map = [
    { value: 'AVAILABLE', label: 'Sẵn sàng', color: 'secondary' },
    { value: 'RUNNING', label: 'Đang chạy', color: 'success' },
    { value: 'MAINTENANCE', label: 'Bảo dưỡng', color: 'warning' },
    { value: 'BROKEN', label: 'Hỏng hóc', color: 'danger' },
];

export const ride_status_map = [
    { value: 'PENDING', label: 'Chờ xử lý', color: 'warning' },
    { value: 'READY', label: 'Sẵn sàng', color: 'secondary' },
    { value: 'RUNNING', label: 'Đang chạy', color: 'success' },
    { value: 'FINISHED', label: 'Hoàn thành', color: 'danger' },
    { value: 'AT_SCHOOL', label: 'Tại trường', color: 'danger' },
];

export const ride_pickup_point_status_map = [
    { value: 'PICKING', label: 'Đang đón', color: 'warning' },
    { value: 'PICKED', label: 'Đã đón', color: 'success' },
];

export const student_pickup_point_status_map = [
    { value: 'PICKING', label: 'Đang đón', color: 'warning' },
    { value: 'PICKED', label: 'Đã đón', color: 'success' },
    { value: 'MISSED', label: 'Bỏ lỡ', color: 'danger' },
    { value: 'AT_SCHOOL', label: 'Tại trường', color: 'secondary' },
    { value: 'AT_HOME', label: 'Tại nhà', color: 'secondary' },
];

export const request_registration_status_map = [
    { value: 'PENDING', label: 'Chờ xử lý', color: 'warning' },
    { value: 'ACCEPTED', label: 'Chấp nhận', color: 'success' },
    { value: 'REJECTED', label: 'Từ chối', color: 'danger' },
];

export enum EmployeeRole {
    DRIVER = "DRIVER",
    DRIVER_MATE = "DRIVER_MATE",
}