interface IPickupPoint {
    id: number;
    address: string;
    latitude: number;
    longitude: number;
}

interface IPickupPointTable {
    pickupPoint: IPickupPoint;
    students: IStudent[];
    rides: IRide[];
}

interface IStudentPickupPoint {
    pickupPoint: IPickupPoint;
    student: IStudent;
    parent: IParent;
}

interface IGetListPickupPointParams {
    address?: string;
}

interface IManipulatePickupPointOutput {
    bus: IBus;
    driver: IEmployee;
    driverMate: IEmployee;
    ride: IRide;
    pickupPointWithStudents: IPickupPointWithStudent[];
}

interface IPickupPointWithStudent {
    pickupPoint: IPickupPoint;
    ridePickupPoint: IRidePickupPoint;
    studentWithPickupPoints: IStudentWithPickupPoint[];
}

interface IStudentWithPickupPoint {
    student: IStudent;
    studentPickupPoint: IStudentPickupPointRaw;
}