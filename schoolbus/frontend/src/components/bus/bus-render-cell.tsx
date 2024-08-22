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
import { bus_status_map } from "@/util/constant";
import { validateColor } from "@/util/color";

const getBusStatusColor = (status: string) => {
    const statusObject = bus_status_map.find((s) => s.value === status);
    return statusObject ? statusObject.color : "primary";
}

interface Props {
    bus: IBusTable;
    columnKey: string | React.Key;
    handleOpenChange: () => void;
    setSelectedBus: (bus: IBusTable) => void;
    handleOpenChangeDelete: () => void;
    handleOpenChangeDetail: () => void;
}

export const BusRenderCell = ({ bus, columnKey, handleOpenChange, setSelectedBus, handleOpenChangeDelete, handleOpenChangeDetail
}: Props) => {

    switch (columnKey) {
        case "numberPlate":
            return (
                <div>
                    <div>
                        <Snippet symbol="" color="default" >{bus?.bus.numberPlate}</Snippet>
                    </div>
                </div>
            );
        case "seatNumber":
            return (
                <div>
                    <div>
                        <span>{bus?.bus.seatNumber}</span>
                    </div>
                </div>
            );
        case "driverName":
            return (
                <User
                    avatarProps={{
                        // src: bus?.driver?.avatar ? bus?.driver?.avatar : undefined,
                    }}
                    name={bus?.driver?.name}
                    description={bus?.driver?.phoneNumber}
                >
                    {bus?.driver?.name}
                </User>
            );
        case "driverMateName":
            return (
                <User
                    avatarProps={{
                        // src: bus?.driverMate?.avatar ? bus?.driverMate?.avatar : undefined,
                    }}
                    name={bus?.driverMate?.name}
                    description={bus?.driverMate?.phoneNumber}
                >
                    {bus?.driverMate?.name}
                </User>
            );
        case "status":
            return (
                <Chip
                    size="sm"
                    variant="flat"
                    color={validateColor(getBusStatusColor(bus?.bus?.status))}
                >
                    <span className="capitalize text-xs">
                        {bus_status_map.find((s) => s.value === bus?.bus?.status)?.label}
                    </span>
                </Chip>
            );

        case "actions":
            return (
                <div className="flex items-center gap-4 ">
                    <div>
                        <Tooltip content="Details">
                            <button onClick={() => {
                                setSelectedBus(bus);
                                handleOpenChangeDetail();
                            }}>
                                <EyeIcon size={20} fill="#979797" />
                            </button>
                        </Tooltip>
                    </div>
                    <div>
                        <Tooltip content="Edit bus" color="secondary">
                            <button onClick={
                                () => {
                                    setSelectedBus(bus);
                                    handleOpenChange();
                                }}>
                                <EditIcon size={20} fill="#979797" />
                            </button>
                        </Tooltip>
                    </div>
                    <div>
                        <Tooltip
                            content="Delete bus" color="danger" >
                            <button onClick={
                                () => {
                                    setSelectedBus(bus);
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
