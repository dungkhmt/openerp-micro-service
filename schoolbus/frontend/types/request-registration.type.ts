interface IRequestRegistration {
    id: number;
    parentId: number;
    studentId: number;
    status: string;
    address: string;
    latitude: number;
    longitude: number;
    note: string;
    createdAt: string;
    updatedAt: string;
}

interface IAddRequestRegistrationRequest {
    studentIds: number[];
    address: string;
    latitude: number | null;
    longitude: number | null;
}

interface IGetRequestRegistrationParams {
}

interface IPageRequestRegistrationParams {
    studentName: string | null;
    parentName: string | null;
    statuses: string | null;
    address: string | null;
    page: number;
    size: number;
    sort: string;
}

interface IRequestRegistrationResponse {
    parent: IParent;
    student: IStudent;
    requestRegistration: IRequestRegistration;
}

interface IHandleRequestRegistrationRequest {
    requestIds: number[] | null;
    status: string | null;
    pickupPointId: number | null;
    address: string | null;
    latitude: number | null;
    longitude: number | null;
    note: string | null;
}