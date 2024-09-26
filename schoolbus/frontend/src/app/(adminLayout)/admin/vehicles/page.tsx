"use client";

import React from 'react';
import {
    Button,
    Input,
    Link,
    Table,
    TableBody,
    TableCell,
    TableColumn,
    TableHeader,
    TableRow,
    Pagination,
    useDisclosure,
    Modal,
    ModalBody,
    ModalContent,
    ModalFooter,
    ModalHeader,
    Select,
    SelectItem,
    select,
    Autocomplete,
    AutocompleteItem,
    Avatar,
    Snippet,
    Chip,
    User,
} from "@nextui-org/react";
import { BusRenderCell } from '@/components/bus/bus-render-cell';
import { AddBus } from '@/components/bus/add-bus';
import { ExportIcon } from '@/components/icons/export-icon';
import { useGetListBus, useUpdateBus, useDeleteBus, useGetBusDetail } from '@/services/admin/busService';
import CustomSkeleton from '@/components/custom-skeleton';
import { SubmitHandler, set, useForm } from "react-hook-form";
import { bus_status_map, EmployeeRole } from '@/util/constant';
import { useGetAvailableEmployees } from '@/services/admin/employeeService';
import { convertStringInstantToDate } from '@/util/dateConverter';
import { validateColor } from '@/util/color';


const VehiclesPage: React.FC = () => {
    const [page, setPage] = React.useState(1);

    // define for bus-render-cell.tsx
    const [selectedBus, setSelectedBus] = React.useState<IBusTable | null>(null);
    const { isOpen, onOpen, onOpenChange } = useDisclosure();
    const { isOpen: isOpenDelete, onOpen: onOpenDelete, onOpenChange: onOpenChangeDelete } = useDisclosure();
    const updateBusMutation = useUpdateBus(onOpenChange);
    const deleteBusMutation = useDeleteBus(onOpenChangeDelete);
    const {
        register,
        handleSubmit,
        watch,
        setValue,
        formState: { errors },
        reset,
    } = useForm<IBus>();

    const [queryDriver, setQueryDriver] = React.useState<string | null>(null);
    const { data: availableDrivers, isLoading: isLoadingDrivers, error: isErrorDrivers } = useGetAvailableEmployees(EmployeeRole.DRIVER, queryDriver);

    const [queryDriverMate, setQueryDriverMate] = React.useState<string | null>(null);
    const { data: availableDriverMates, isLoading: isLoadingDriverMates, error: isErrorDriverMates } = useGetAvailableEmployees(EmployeeRole.DRIVER_MATE, queryDriverMate);

    const handleUpdateBus: SubmitHandler<IBus> = (data) => {
        if ((data.driverId == null || data.driverId == undefined) && (queryDriver != null && queryDriver != '' && queryDriver != undefined)) {
            data.driverId = selectedBus?.driver?.id || null;
        }
        if ((data.driverMateId == null || data.driverMateId == undefined) && (queryDriverMate != null && queryDriverMate != '' && queryDriverMate != undefined)) {
            data.driverMateId = selectedBus?.driverMate?.id || null;
        }
        updateBusMutation.mutate(data);
    }
    const handleDeleteBus = (id: number) => deleteBusMutation.mutate(id);

    const handleOpenChange = () => onOpenChange();
    const handleOpenChangeDelete = () => onOpenChangeDelete();


    // search filters
    const [numberPlate, setNumberPlate] = React.useState('');
    const [seatNumber, setSeatNumber] = React.useState<number | null>(null);
    const [statuses, setStatuses] = React.useState<string | null>('');
    const [driverName, setDriverName] = React.useState('');
    const [driverId, setDriverId] = React.useState('');
    const [driverMateName, setDriverMateName] = React.useState('');
    const [driverMateId, setDriverMateId] = React.useState('');

    // map columns for table
    const columns = [
        { name: 'BIỂN SỐ XE', uid: 'numberPlate' },
        { name: 'SỐ GHẾ', uid: 'seatNumber' },
        { name: 'TRẠNG THÁI', uid: 'status' },
        { name: 'TÀI XẾ', uid: 'driverName' },
        { name: 'PHỤ XE', uid: 'driverMateName' },
        { name: 'ACTIONS', uid: 'actions' },
    ];

    // get bus page
    const { data, isLoading, isError } = useGetListBus({
        numberPlate: numberPlate ? numberPlate : null,
        seatNumber: seatNumber,
        statuses: statuses,
        driverName: driverName ? driverName : null,
        driverId: null,
        driverMateName: driverMateName ? driverMateName : null,
        driverMateId: null,
        page: page - 1,
        size: 10,
        sort: "-createdAt",
    });

    // bottom content of table (pagination, ...)
    const bottomContent = (
        <div className="py-2 px-2 flex w-full justify-center items-center">
            <Pagination
                isCompact
                showControls
                showShadow
                color="primary"
                page={page}
                total={data?.result.totalPages || 1}
                onChange={setPage}
            />
        </div>
    );

    // get bus detail
    const { data: busDetail, isLoading: isLoadingBusDetail, isError: isErrorBusDetail } = useGetBusDetail(selectedBus?.bus.id || -1);
    const { isOpen: isOpenDetail, onOpen: onOpenDetail, onOpenChange: onOpenChangeDetail } = useDisclosure();
    const handleOpenChangeDetail = () => onOpenChangeDetail();

    return (
        <div>
            <div className="my-8 lg:px-6 max-w-[95rem] mx-auto w-full flex flex-col gap-4">
                <h3 className="text-xl font-semibold">Danh sách xe bus</h3>
                <div className="flex justify-between flex-wrap gap-4 items-center">
                    <div className="flex items-center gap-3 flex-wrap md:flex-nowrap w-2/3">
                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Biển số xe"
                            value={numberPlate}
                            onValueChange={(newValue) => setNumberPlate(newValue)}
                        />
                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Số ghế"
                            type="number"
                            value={seatNumber?.toString() || ''}
                            onValueChange={(newValue) => setSeatNumber(newValue ? parseInt(newValue) : null)}
                        />

                        <Select
                            label="Trạng thái"
                            placeholder='Chọn trạng thái'
                            selectionMode='multiple'
                            size='sm'
                            color={validateColor(bus_status_map.find((status) => status.value === statuses)?.color || 'default')}
                            value={statuses?.split(',')}
                            onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                                const newValue = event.target.value;
                                setStatuses(newValue);
                            }}
                        >
                            {bus_status_map.map((status) => (
                                <SelectItem key={status.value} value={status.value} color={validateColor(status.color)}
                                >
                                    {status.label}
                                </SelectItem>
                            ))
                            }
                        </Select>

                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Tài xế"
                            value={driverName}
                            onValueChange={(newValue) => setDriverName(newValue)}
                        />
                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Phụ xe"
                            value={driverMateName}
                            onValueChange={(newValue) => setDriverMateName(newValue)}
                        />
                    </div>
                    <div className="flex flex-row gap-3.5 flex-wrap">
                        <AddBus />
                        <Button color="primary" startContent={<ExportIcon />}>
                            Xuất file CSV
                        </Button>
                    </div>
                </div>
            </div>

            {
                isLoading ? <CustomSkeleton /> :
                    <>
                        <div className="flex flex-col gap-4 mx-4">
                            <Table aria-label="Example table with custom cells"
                                bottomContent={bottomContent}
                            >
                                <TableHeader columns={columns}>
                                    {(column) => (
                                        <TableColumn
                                            key={column.uid}
                                            hideHeader={column.uid === "actions"}
                                            align={column.uid === "actions" ? "center" : "start"}
                                        >
                                            {column.name}
                                        </TableColumn>
                                    )}
                                </TableHeader>
                                {data?.result && data.result.content ? (
                                    <TableBody items={data.result.content} emptyContent='No row to display'>
                                        {(item) => (
                                            <TableRow key={item.bus.id}>
                                                {(columnKey) => (
                                                    <TableCell>
                                                        {BusRenderCell({
                                                            bus: item as IBusTable,
                                                            columnKey: columnKey,
                                                            handleOpenChange: () => { handleOpenChange() },
                                                            setSelectedBus: (bus: IBusTable) => {
                                                                setSelectedBus(bus);
                                                                reset();
                                                            },
                                                            handleOpenChangeDelete: () => { handleOpenChangeDelete() },
                                                            handleOpenChangeDetail: () => { handleOpenChangeDetail() },
                                                        })}
                                                    </TableCell>

                                                )}
                                            </TableRow>
                                        )}
                                    </TableBody>
                                ) : (
                                    <TableBody emptyContent={"No rows to display."}>{[]}</TableBody>
                                )}
                            </Table>
                        </div>

                        {/* modal for edit button */}
                        <Modal
                            isOpen={isOpen}
                            onOpenChange={onOpenChange}
                            placement="top-center"
                        >
                            <ModalContent>
                                <ModalHeader className="flex flex-col gap-1">
                                    Cập nhật xe Bus
                                </ModalHeader>
                                <form
                                    className="space-y-4"
                                    onSubmit={handleSubmit(handleUpdateBus)}
                                >
                                    <ModalBody>

                                        <Input
                                            label="id"
                                            variant="bordered"
                                            className='hidden'
                                            {...register("id", { required: true })}
                                            defaultValue={selectedBus?.bus.id?.toString()}
                                        />

                                        <Input
                                            label="Biển số xe"
                                            variant="bordered"
                                            {...register("numberPlate", { required: true })}
                                            defaultValue={selectedBus?.bus.numberPlate}
                                        />
                                        <Input
                                            label="Số ghế"
                                            variant="bordered"
                                            {...register("seatNumber", {
                                                required: true,
                                                validate: (value: any) => parseInt(value, 10) > 0 || 'Số ghế không hợp lệ'
                                            })}
                                            defaultValue={selectedBus?.bus.seatNumber?.toString()}
                                        />
                                        {errors.seatNumber && errors.seatNumber.message && <p className="text-red-500 text-sm">{`*${errors.seatNumber.message}`}</p>}

                                        <Select
                                            label="Trạng thái"
                                            placeholder="Chọn trạng thái"
                                            selectionMode="single"
                                            value={selectedBus?.bus.status}
                                            defaultSelectedKeys={[bus_status_map.find((status) => status.value === selectedBus?.bus.status)?.value || '']}
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
                                            label="Chọn tài xế"
                                            onInputChange={(value) => {
                                                setQueryDriver(value);
                                                if (value === '' || value === null || value === undefined) {
                                                    setValue("driverId", null);
                                                }
                                            }}
                                            onSelectionChange={(selected) => {
                                                if (selected) {
                                                    setValue("driverId", Number(selected));
                                                }
                                            }}
                                            defaultItems={[... (availableDrivers?.result || []), selectedBus?.driver] || []}
                                            defaultInputValue={selectedBus?.driver?.name}
                                            defaultSelectedKey={selectedBus?.driver?.id?.toString()}
                                        >
                                            {(item) => {
                                                if (item) {
                                                    return (
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
                                                    );
                                                } else {
                                                    // Return a default CollectionElement when item is undefined
                                                    return (
                                                        <AutocompleteItem
                                                            key="default"
                                                            value="default"
                                                            textValue="default"
                                                        >
                                                            <div className="flex gap-2 items-center">
                                                                <Avatar alt="default" className="flex-shrink-0" size="sm" src="default" />
                                                                <div className="flex flex-col">
                                                                    <span className="text-small">default</span>
                                                                    <span className="text-tiny text-default-400">default</span>
                                                                </div>
                                                            </div>
                                                        </AutocompleteItem>
                                                    );
                                                }
                                            }}
                                        </Autocomplete>

                                        <Autocomplete
                                            variant="bordered"
                                            label="Chọn phụ xe"
                                            onInputChange={(value) => {
                                                setQueryDriverMate(value);
                                                if (value === '' || value === null || value === undefined) {
                                                    setValue("driverMateId", null);
                                                }
                                            }}
                                            onSelectionChange={(selected) => {
                                                if (selected) {
                                                    setValue("driverMateId", Number(selected));
                                                }
                                            }}
                                            defaultItems={[...(availableDriverMates?.result || []), selectedBus?.driverMate] || []}
                                            defaultInputValue={selectedBus?.driverMate?.name}
                                            defaultSelectedKey={selectedBus?.driverMate?.id?.toString()}
                                        >
                                            {(item) => {
                                                if (item) {
                                                    return (
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
                                                    );
                                                } else {

                                                    return (
                                                        <AutocompleteItem
                                                            key="default"
                                                            value="default"
                                                            textValue="default"
                                                        >
                                                            <div className="flex gap-2 items-center">
                                                                <Avatar alt="default" className="flex-shrink-0" size="sm" src="default" />
                                                                <div className="flex flex-col">
                                                                    <span className="text-small">default</span>
                                                                    <span className="text-tiny text-default-400">default</span>
                                                                </div>
                                                            </div>
                                                        </AutocompleteItem>
                                                    );
                                                }
                                            }}
                                        </Autocomplete>
                                    </ModalBody>

                                    <ModalFooter>
                                        <Button variant="flat" color='danger' onPress={onOpenChange}>
                                            Hủy
                                        </Button>
                                        <Button color="primary" type="submit">
                                            Cập nhật
                                        </Button>
                                    </ModalFooter>
                                </form>
                            </ModalContent>
                        </Modal>


                        {/* delete modal */}
                        <Modal isOpen={isOpenDelete} onOpenChange={onOpenChangeDelete}>
                            <ModalContent>
                                {(onCloseDelete) => (
                                    <>
                                        <ModalHeader className="flex flex-col gap-1">Xoá xe {selectedBus?.bus.numberPlate}</ModalHeader>
                                        <ModalBody>
                                            <p>
                                                Bạn có muốn xoá không?
                                            </p>
                                        </ModalBody>
                                        <ModalFooter>
                                            <Button color="danger" variant="light" onPress={onCloseDelete}>
                                                Huỷ
                                            </Button>
                                            <Button color="primary" onPress={() => {
                                                if (selectedBus?.bus.id !== undefined) {
                                                    handleDeleteBus(selectedBus.bus.id);
                                                } else {
                                                    console.error('selectedBus?.bus.id is undefined');
                                                }
                                            }}>
                                                Xác nhận
                                            </Button>
                                        </ModalFooter>
                                    </>
                                )}
                            </ModalContent>
                        </Modal>

                        {/* bus detail modal */}
                        <Modal isOpen={isOpenDetail} onOpenChange={onOpenChangeDetail}>
                            <ModalContent>
                                <ModalHeader className="flex flex-col gap-1">
                                    Chi tiết xe bus
                                </ModalHeader>
                                <ModalBody>
                                    <div className="flex flex-col gap-4 overflow-auto">
                                        <div className="flex flex-col gap-2 overflow-auto">
                                            <div className='flex gap-2 items-center'>
                                                <span className="font-bold">Biển số xe:</span>
                                                <Snippet symbol="" color="default">{busDetail?.result?.bus.numberPlate}</Snippet>
                                            </div>
                                            {/* <span className="font-bold">Ngày tạo: {convertStringInstantToDate(busDetail?.result?.bus.createdAt)}</span> */}
                                            <div className='flex gap-2 items-center overflow-auto'>
                                                <span className="font-bold overflow-auto">Ngày tạo:</span>
                                                <p>{convertStringInstantToDate(busDetail?.result?.bus.createdAt)}</p>
                                            </div>

                                            <div className='flex gap-2 items-center'>
                                                <span className="font-bold">Ngày cập nhật:</span>
                                                <p>{convertStringInstantToDate(busDetail?.result?.bus.updatedAt)}</p>
                                            </div>

                                            <div className='flex gap-2 items-center'>
                                                <span className="font-bold">Số ghế:</span>
                                                <p>{busDetail?.result?.bus.seatNumber}</p>
                                            </div>
                                            <div className='flex gap-2 items-center'>
                                                <span className="font-bold">Trạng thái:</span>
                                                <Chip
                                                    size="sm"
                                                    variant="flat"
                                                    color={validateColor(bus_status_map.find((s) => s.value === busDetail?.result?.bus.status)?.color || 'default')}
                                                >
                                                    <span className="capitalize text-xs">
                                                        {bus_status_map.find((s) => s.value === busDetail?.result?.bus.status)?.label}
                                                    </span>
                                                </Chip>
                                            </div>
                                        </div>

                                        <div className="flex flex-col gap-2">
                                            {/* <span className="font-bold">Tài xế: {busDetail?.result?.driver.name}</span>
                                            <span className="font-bold">Phụ xe: {busDetail?.result?.driverMate.name}</span> */}
                                            <div className='flex gap-2 items-center'>
                                                <div className='flex gap-1 items-center'>
                                                    <span className="font-bold">Tài xế:</span>
                                                    <User
                                                        avatarProps={{
                                                            // src: busDetail?.result?.driver.avatar,
                                                        }}
                                                        name={busDetail?.result?.driver.name}
                                                        description={busDetail?.result?.driver.phoneNumber}
                                                    >
                                                        {busDetail?.result?.driver.name}
                                                    </User>
                                                </div>
                                                <div className='flex gap-2 items-center'>
                                                    <span className="font-bold">Ngày sinh:</span>
                                                    <p>{convertStringInstantToDate(busDetail?.result?.driver.dob)}</p>
                                                </div>
                                            </div>

                                            <div className='flex gap-2 items-center'>
                                                <div className='flex gap-1 items-center'>
                                                    <span className="font-bold">Phụ xe:</span>
                                                    <User
                                                        avatarProps={{
                                                            // src: busDetail?.result?.driverMate.avatar,
                                                        }}
                                                        name={busDetail?.result?.driverMate.name}
                                                        description={busDetail?.result?.driverMate.phoneNumber}
                                                    >
                                                        {busDetail?.result?.driverMate.name}
                                                    </User>
                                                </div>


                                                <div className='flex gap-2 items-center'>
                                                    <span className="font-bold">Ngày sinh:</span>
                                                    <p>{convertStringInstantToDate(busDetail?.result?.driverMate.dob)}</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="primary" onPress={onOpenChangeDetail}>
                                        Đóng
                                    </Button>
                                </ModalFooter>
                            </ModalContent>
                        </Modal>

                    </>
            }


        </div >
    );
};

export default VehiclesPage;