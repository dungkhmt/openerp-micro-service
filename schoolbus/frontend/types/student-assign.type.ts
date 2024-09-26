interface IStudentAssign {
    id: number;
    studentId: number;
    numberPlate: string;
    createdAt: string;
    updatedAt: string;
}

interface IUpsertStudentAssignRequest {
    items: IUpsertStudentAssignItem[];
}

interface IUpsertStudentAssignItem {
    studentId: number;
    numberPlate: string;
}