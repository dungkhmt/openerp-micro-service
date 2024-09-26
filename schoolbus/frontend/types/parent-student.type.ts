interface IGetStudentRidesParams {
}

interface IStudentRide {
    student: IStudent;
    pickupPoint: IPickupPoint;
    executions: IStudentRideExecution[];
}

interface IStudentRideExecution {
    ride: IRide;
    bus: IBus;
    pickupPoints: IPickupPoint[];
    ridePickupPoints: IRidePickupPoint[];
}