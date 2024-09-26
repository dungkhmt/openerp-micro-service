interface IStudentPickupPointRaw {
    id: number;
    pickupPointId: number;
    studentId: number;
    status: string;
    getInAt: string;
    createdAt: string;
    updatedAt: string;
}

// employee
interface IEmployeeUpdateStudentPickupPointRequest {
    studentIds: number[];
    pickupPointId: number | null;
    status: string | null;
    rideId: number | null;
}