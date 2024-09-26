import {
    User,
    Tooltip,
    Chip,
} from "@nextui-org/react";
import React from "react";
import { DeleteIcon } from "../../../icons/table/delete-icon";
import { EditIcon } from "../../../icons/table/edit-icon";
import { EyeIcon } from "../../../icons/table/eye-icon";
import { convertStringInstantToDate } from "@/util/dateConverter";

interface Props {
    data: IStudent;
    columnKey: string | React.Key;
    handleOpenChange: () => void;
    setSelectedStudent: (student: IStudent) => void;
    handleOpenChangeDelete: () => void;
}

export const ClientStudentRenderCell = ({ data, columnKey, handleOpenChange, setSelectedStudent, handleOpenChangeDelete
}: Props) => {

    switch (columnKey) {
        case "name":
            return (
                <div>
                    <div>
                        <User
                            avatarProps={{
                                // src: data.avatar ? data.avatar : undefined,
                            }}
                            name={data.name}
                        >
                            {data.name}
                        </User>
                    </div>
                </div>
            );
        case "studentClass":
            return (
                <div>
                    <div>
                        <Chip color="success">{data.studentClass}</Chip>
                    </div>
                </div>
            );
        case "phoneNumber":
            return (
                <div>
                    <div>
                        <span>{data.phoneNumber}</span>
                    </div>
                </div>
            );
        case "dob":
            return (
                <div>
                    <div>
                        <span>{convertStringInstantToDate(data.dob)}</span>
                    </div>
                </div>
            );
        case "actions":
            return (
                <div className="flex items-center gap-4 ">
                    <div>
                        <Tooltip content="Chi tiết">
                            <button onClick={() => console.log("View bus", data.id)}>
                                <EyeIcon size={20} fill="#979797" />
                            </button>
                        </Tooltip>
                    </div>
                    <div>
                        <Tooltip content="Chỉnh sửa học sinh" color="secondary">
                            <button onClick={
                                () => {
                                    setSelectedStudent(data);
                                    handleOpenChange();
                                }}>
                                <EditIcon size={20} fill="#979797" />
                            </button>
                        </Tooltip>
                    </div>
                    <div>
                        <Tooltip
                            content="Xoá học sinh" color="danger" >
                            <button onClick={
                                () => {
                                    setSelectedStudent(data);
                                    handleOpenChangeDelete();
                                }}>
                                <DeleteIcon size={20} fill="#FF0080" />
                            </button>
                        </Tooltip>
                    </div>
                </div>
            );
        default:
            return null;
    }
};
