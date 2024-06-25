import {
    Autocomplete,
    AutocompleteItem,
    Avatar,
    Button,
    Input,
    Modal,
    ModalBody,
    ModalContent,
    ModalFooter,
    ModalHeader,
    Select,
    SelectItem,
    useDisclosure,
} from "@nextui-org/react";
import React from "react";
import { PlusIcon } from "../icons/plus";
import { SubmitHandler, useForm } from "react-hook-form";
import { useAddBus } from "@/services/admin/busService";
import { EmployeeRole, bus_status_map } from "@/util/constant";
import { useGetAvailableEmployees, useGetListEmployee } from "@/services/admin/employeeService";

export const AddBus = () => {
    const { isOpen, onOpen, onOpenChange } = useDisclosure();

    const [queryDriver, setQueryDriver] = React.useState<string | null>(null);
    const { data: availableDrivers, isLoading: isLoadingDrivers, error: isErrorDrivers } = useGetAvailableEmployees(EmployeeRole.DRIVER, queryDriver);

    const [queryDriverMate, setQueryDriverMate] = React.useState<string | null>(null);
    const { data: availableDriverMates, isLoading: isLoadingDriverMates, error: isErrorDriverMates } = useGetAvailableEmployees(EmployeeRole.DRIVER_MATE, queryDriverMate);

    const addBusMutation = useAddBus(onOpenChange);
    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
        setValue,
    } = useForm<IBus>();
    const handleAddBus: SubmitHandler<IBus> = (data) => addBusMutation.mutate(data);

    return (
        <div>
            <Button onPress={onOpen} color="primary" endContent={<PlusIcon />}>
                Thêm xe Bus
            </Button>
            <Modal
                isOpen={isOpen}
                onOpenChange={onOpenChange}
                placement="top-center"
            >
                <ModalContent>
                    <ModalHeader className="flex flex-col gap-1">
                        Thêm xe Bus
                    </ModalHeader>
                    <form
                        className="space-y-4"
                        onSubmit={handleSubmit(handleAddBus)}
                    >
                        <ModalBody>

                            <Input
                                label="Biển số xe"
                                variant="bordered"
                                {...register("numberPlate", { required: true })}
                            />
                            <Input
                                label="Số ghế"
                                variant="bordered"
                                {...register("seatNumber", {
                                    required: true,
                                    validate: (value: any) => parseInt(value, 10) > 0 || 'Số ghế không hợp lệ'
                                })}
                            />
                            {errors.seatNumber && errors.seatNumber.message && <p className="text-red-500 text-sm">{`*${errors.seatNumber.message}`}</p>}

                            <Select
                                label="Trạng thái"
                                placeholder="Chọn trạng thái"
                                selectionMode="single"
                                {...register("status", { required: true })}
                            >
                                {bus_status_map.map((status) => (
                                    <SelectItem key={status.value} value={status.value}>
                                        {status.label}
                                    </SelectItem>
                                ))}
                            </Select>

                            <Autocomplete
                                variant="bordered"
                                items={isLoadingDrivers ? [] : isErrorDrivers ? [] : availableDrivers?.result || []}
                                label="Chọn tài xế"
                                onInputChange={(value) => {
                                    setQueryDriver(value);
                                }}
                                onSelectionChange={(selected) => {
                                    if (selected) {
                                        setValue("driverId", Number(selected));
                                    }
                                }}
                            >
                                {(item) =>
                                    <AutocompleteItem
                                        key={item.id}
                                        value={item.id}
                                        textValue={item.name}
                                    >
                                        <div className="flex gap-2 items-center">
                                            <Avatar alt={item.name} className="flex-shrink-0" size="sm" src={item.avatar} />
                                            <div className="flex flex-col">
                                                <span className="text-small">{item.name}</span>
                                                <span className="text-tiny text-default-400">{item.phoneNumber}</span>
                                            </div>
                                        </div>
                                    </AutocompleteItem>
                                }
                            </Autocomplete>

                            <Autocomplete
                                variant="bordered"
                                items={isLoadingDriverMates ? [] : isErrorDriverMates ? [] : availableDriverMates?.result || []}
                                label="Chọn phụ xe"
                                onInputChange={(value) => {
                                    setQueryDriverMate(value);
                                }}
                                onSelectionChange={(selected) => {
                                    if (selected) {
                                        setValue("driverMateId", Number(selected));
                                    }
                                }}
                            >
                                {(item) =>
                                    <AutocompleteItem
                                        key={item.id}
                                        value={item.id}
                                        textValue={item.name}
                                    >
                                        <div className="flex gap-2 items-center">
                                            <Avatar alt={item.name} className="flex-shrink-0" size="sm" src={item.avatar} />
                                            <div className="flex flex-col">
                                                <span className="text-small">{item.name}</span>
                                                <span className="text-tiny text-default-400">{item.phoneNumber}</span>
                                            </div>
                                        </div>
                                    </AutocompleteItem>
                                }
                            </Autocomplete>

                        </ModalBody>
                        <ModalFooter>
                            <Button color="danger" variant="flat" onPress={onOpenChange}>
                                Huỷ
                            </Button>
                            <Button color="primary" type="submit">
                                Thêm xe Bus
                            </Button>
                        </ModalFooter>
                    </form>
                </ModalContent>
            </Modal>
        </div>
    );
};
