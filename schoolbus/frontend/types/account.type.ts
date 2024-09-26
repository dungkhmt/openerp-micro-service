
interface IStudentBase {
    id: number;
    name: string | null;
    avatar: string | undefined;
    dob: string | null;
    phoneNumber: string | null;
    studentClass: string | null;
    parentId: number;
    created_at: string;
    updated_at: string;

    numberPlateAssign: string | null;
}
interface IStudent extends IStudentBase {
    id: number;
}

interface IStudenAdd {
    id: number | string | null;
    name: string;
    avatar: string | null;
    dob: string;
    phoneNumber: string;
    studentClass: string;
    parentId: number;
}


interface IStudenAddClient {
    id: number | string | null;
    name: string;
    avatar: string | null;
    dob: string;
    phoneNumber: string;
    studentClass: string;
    parentId: number;
}

interface IStudentUpdate extends IStudentBase {
    id: number;
    name: string;
    avatar: string | undefined;
    dob: string;
    phoneNumber: string;
    studentClass: string;
    parentId: number;
}

interface IStudentDetail extends IStudentBase {
    parentName: string | null;
    parentPhoneNumber: string | null;

}

interface IParentBase {
    name: string | undefined;
    avatar: string | undefined;
    dob: string | null;
    phoneNumber: string | null;
    created_at: string;
    updated_at: string;
    students: IStudent[] | null;
}

interface IParent extends IParentBase {
    id: number;
}
interface IParentDetail extends IParentBase {
    id: number;
    username: string | null;
}

interface IParentAdd extends IParentBase {
    username: string | null;
    password: string | null;
    confirmPassword: string | null;
    studentIds: number[] | null;
}

interface IParentUpdate extends IParentBase {
    id: number;
    username?: string | null;
    password?: string | null;
    confirmPassword?: string | null;
    studentIds: number[] | null;
}


interface IGetListParentParams {
    id: number | null;
    name: string | null;
    dob: string | null;
    page: number | null;
    size: number | null;
    phoneNumber: string | null;
    sort: string | null;
    sortBy: '-createdAt' | '-updatedAt' | 'createdAt' | 'updatedAt' | null;
    searchBy: 'PARENT_NAME' | 'STUDENT_NAME' | 'PARENT_PHONE_NUMBER' | 'STUDENT_CLASS' | null;
}


interface IGetListStudentParams {
    id: number | null;
    name: string | null;
    dob: string | null;
    phoneNumber: string | null;
    studentClass: string | null;
    parent_id: number | null;
    page: number | null;
    size: number | null;
    sort: string | null;
    sortBy: '-createdAt' | '-updatedAt' | 'createdAt' | 'updatedAt' | null;
}


