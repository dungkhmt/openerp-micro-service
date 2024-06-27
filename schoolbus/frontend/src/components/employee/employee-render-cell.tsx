import {
    User,
    Tooltip,
    Chip,
    Snippet,
} from "@nextui-org/react";
import React from "react";
import { DeleteIcon } from "../icons/table/delete-icon";
import { EditIcon } from "../icons/table/edit-icon";
import { EyeIcon } from "../icons/table/eye-icon";
import { convertStringInstantToDate } from "@/util/dateConverter";
import { employee_role_map } from "@/util/constant";
import { validateColor } from "@/util/color";

interface Props {
    employeeTable: IEmployeeTable;
    columnKey: string | React.Key;
    handleOpenChange: () => void;
    setSelectedEmployee: (employee: IEmployeeTable) => void;
    handleOpenChangeDelete: () => void;
}

const getChipColor = (role: string) => {
    const roleObject = employee_role_map.find((r) => r.value === role);
    return roleObject ? roleObject.color : 'primary';
};

export const EmployeeRenderCell = ({
    employeeTable,
    columnKey,
    handleOpenChange,
    setSelectedEmployee,
    handleOpenChangeDelete,
}: Props) => {

    switch (columnKey) {
        case "name":
            return (
                <User
                    avatarProps={{
                        // src: employeeTable?.employee.avatar ? employeeTable?.employee.avatar : undefined,
                    }}
                    name={employeeTable?.employee.name}
                >
                    {employeeTable?.employee.name}
                </User>
            );
        case "phoneNumber":
            return (
                <div>
                    <div>
                        <Snippet color="default" symbol="">{employeeTable?.employee.phoneNumber}</Snippet>
                    </div>
                </div>
            );
        case "dob":
            return (
                <div>
                    <div>
                        <span>{convertStringInstantToDate(employeeTable?.employee.dob)}</span>
                    </div>
                </div>
            );
        case "busNumberPlate":
            return (
                <div>
                    <div>
                        {
                            employeeTable?.bus?.numberPlate ?
                                <Snippet symbol="" color="default" >{employeeTable?.bus?.numberPlate}</Snippet>
                                : ""
                        }

                    </div>
                </div>
            );
        case "role":
            return (
                <Chip color={validateColor(getChipColor(employeeTable?.employee.role))} variant="flat" size="sm">
                    {employee_role_map.find(role => role.value === employeeTable?.employee.role)?.label}
                </Chip>
            );
        case "actions":
            return (
                <div className="flex items-center gap-4 ">
                    <div>
                        <Tooltip content="Chi tiết">
                            <button onClick={() => console.log("View employee", employeeTable.employee.id)}>
                                <EyeIcon size={20} fill="#979797" />
                            </button>
                        </Tooltip>
                    </div>
                    <div>
                        <Tooltip content="Chỉnh sửa" color="secondary">
                            <button onClick={
                                () => {
                                    setSelectedEmployee(employeeTable);
                                    handleOpenChange();
                                }}>
                                <EditIcon size={20} fill="#979797" />
                            </button>
                        </Tooltip>
                    </div>
                    <div>
                        <Tooltip
                            content="Xoá" color="danger" >
                            <button onClick={
                                () => {
                                    setSelectedEmployee(employeeTable);
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