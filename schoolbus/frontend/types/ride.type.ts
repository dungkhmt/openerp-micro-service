interface IRide {
    id: number;
    busId: number;
    startAt: string;
    endAt: string;
    startFrom: string;
    status: string;
    isToSchool: boolean;
    createdAt: string;
    updatedAt: string;
}

interface IUpsertRideRequest {
    id: number | null;
    busId: number | null;
    startAt: string | null;
    endAt: string | null;
    startFrom: string | null;
    pickupPointIds: number[] | null;
    isToSchool: boolean;
}

// employee
interface IEmployeeUpdateRideRequest {
    rideId: number | null;
    status: string | null;
}