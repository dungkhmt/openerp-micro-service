interface IRideHistory {
    id: number;
    rideId: number;
    busId: number;
    startAt: string;
    endAt: string;
    startFrom: string;
    status: string;
    isToSchool: boolean;
    driverId: number;
    driverMateId: number;
    createdAt: string;
    updatedAt: string;
}

interface IRidePickupPointHistory {
    id: number;
    ride_pickup_point_id: number;
    pickup_point_id: number;
    ride_id: number;
    orderIndex: number;
    status: string;
    address: string;
    longitude: number;
    latitude: number;
    createdAt: string;
    updatedAt: string;
}

interface IStudentPickupPointHistory {
    id: number;
    student_pickup_point_id: number;
    student_id: number;
    pickup_point_id: number;
    status: string;
    address: string;
    longitude: number;
    latitude: number;
    rideId: number;
    createdAt: string;
    updatedAt: string;
}

interface IStudentRideHistory {
    student: IStudent;
    studentPickupPointHistories: IStudentPickupPointHistory[];
}
// use for all admin, employee, client
interface IHistoryResponse {
    bus: IBus;
    ride: IRide;
    driver: IEmployee;
    driverMate: IEmployee;
    rideHistories: IRideHistory[];
    ridePickupPointHistories: IRidePickupPointHistory[];
    studentRideHistories: IStudentRideHistory[];
}

interface IAdminHistoryRideFilterParam {
    startAt: string | null;
    rideId: number | null;
    numberPlate: string | null;
    status: string | null;
    isToSchool: boolean | null;
    address: string | null;

    studentPhoneNumber: string | null;
    parentPhoneNumber: string | null;

    page: number;
    size: number;
    sort: string;
}

interface IClientHistoryRideFilterParam {
    startAt: string | null;
    rideId: number | null;
    numberPlate: string | null;
    status: string | null;
    isToSchool: boolean | null;
    address: string | null;

    studentPhoneNumber: string | null;

    page: number;
    size: number;
    sort: string;
}

interface IEmployeeHistoryRideFilterParam {
    startAt: string | null;
    rideId: number | null;
    numberPlate: string | null;
    status: string | null;
    isToSchool: boolean | null;
    address: string | null;

    studentPhoneNumber: string | null;
    parentPhoneNumber: string | null;

    page: number;
    size: number;
    sort: string;
}


