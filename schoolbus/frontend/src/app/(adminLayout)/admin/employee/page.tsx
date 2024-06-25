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
} from "@nextui-org/react";
import { EmployeeRenderCell } from '@/components/employee/employee-render-cell';
import { AddEmployee } from '@/components/employee/add-employee';
import { ExportIcon } from '@/components/icons/export-icon';
import { useGetListEmployee, useUpdateEmployee, useDeleteEmployee } from '@/services/admin/employeeService';
import CustomSkeleton from '@/components/custom-skeleton';
import { SubmitHandler, set, useForm } from 'react-hook-form';
import { convertStringInstantToDate } from '@/util/dateConverter';
import { employee_role_map } from '@/util/constant';
import { validateColor } from '@/util/color';

const EmployeePage: React.FC = () => {
    const [page, setPage] = React.useState(1);


    const { isOpen, onOpen, onOpenChange } = useDisclosure();
    const { isOpen: isOpenDelete, onOpen: onOpenDelete, onOpenChange: onOpenChangeDelete } = useDisclosure();
    const updateEmployeeMutation = useUpdateEmployee(onOpenChange);
    const deleteEmployeeMutation = useDeleteEmployee(onOpenChangeDelete);
    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
        reset,
    } = useForm<IEmployee>();
    const handleUpdateEmployee: SubmitHandler<IEmployee> = (data) => {
        updateEmployeeMutation.mutate(data);
    }
    const handleDeleteEmployee = (id: number) => deleteEmployeeMutation.mutate(id);

    const [selectedEmployee, setSelectedEmployee] = React.useState<IEmployeeTable | null>(null);

    const handleOpenChange = () => onOpenChange();
    const handleOpenChangeDelete = () => onOpenChangeDelete();

    // search filters
    const [name, setName] = React.useState<string>('');
    const [phoneNumber, setPhoneNumber] = React.useState<string>('');
    const [dob, setDob] = React.useState<string>('');
    const [busID, setBusID] = React.useState<number | null>(null);
    const [busNumberPlate, setBusNumberPlate] = React.useState<string>('');
    const [roles, setRoles] = React.useState<string | null>('');

    const columns = [
        { name: 'Họ tên', uid: 'name' },
        { name: 'Số điện thoại', uid: 'phoneNumber' },
        { name: 'Ngày sinh', uid: 'dob' },
        { name: 'Xe bus hiện tại', uid: 'busNumberPlate' },
        { name: 'Vai trò', uid: 'role' },
        { name: 'ACTIONS', uid: 'actions' },
    ]

    const { data, isLoading, isError } = useGetListEmployee({
        name: name ? name : null,
        phoneNumber: phoneNumber ? phoneNumber : null,
        dob: dob ? dob : null,
        busID: busID ? busID : null,
        busNumberPlate: busNumberPlate ? busNumberPlate : null,
        roles: roles ? roles : null,
        page: page - 1,
        size: 10,
        sort: "-createdAt",
    });

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

    return (
        <div>
            <div className="my-8 lg:px-6 max-w-[95rem] mx-auto w-full flex flex-col gap-4">
                <h3 className="text-xl font-semibold">Danh sách nhân viên</h3>
                <div className="flex justify-between flex-wrap gap-4 items-center">
                    <div className="flex items-center gap-3 flex-wrap md:flex-nowrap w-2/3">
                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Họ tên"
                            value={name}
                            onValueChange={(newValue) => setName(newValue)}
                        />
                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Số điện thoại"
                            value={phoneNumber}
                            onValueChange={(newValue) => setPhoneNumber(newValue)}
                        />
                        <Input
                            classNames={{
                                input: "w-full",
                                mainWrapper: "w-full",
                            }}
                            size='sm'
                            label="Biển số xe"
                            value={busNumberPlate}
                            onValueChange={(newValue) => setBusNumberPlate(newValue)}
                        />
                        <Select
                            label="Vai trò"
                            placeholder='Chọn vai trò'
                            selectionMode='multiple'
                            value={roles?.split(',')}
                            onChange={(event: React.ChangeEvent<HTMLSelectElement>) => {
                                const newValue = event.target.value;
                                setRoles(newValue);
                            }}
                        >
                            {employee_role_map.map((role) => (
                                <SelectItem key={role.value} value={role.value}>
                                    {role.label}
                                </SelectItem>
                            ))}
                        </Select>
                    </div>
                    <div className="flex flex-row gap-3.5 flex-wrap">
                        <AddEmployee />
                        <Button color="primary" startContent={<ExportIcon />}>
                            Xuất file CSV
                        </Button>
                    </div>
                </div>
            </div>

            {isLoading ? <CustomSkeleton /> :
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
                                        <TableRow key={item.employee.id}>
                                            {(columnKey) => (
                                                <TableCell>
                                                    {EmployeeRenderCell({
                                                        employeeTable: item as IEmployeeTable,
                                                        columnKey: columnKey,
                                                        handleOpenChange: () => { handleOpenChange() },
                                                        setSelectedEmployee: (employee: IEmployeeTable) => {
                                                            setSelectedEmployee(employee);
                                                            reset();
                                                        },
                                                        handleOpenChangeDelete: () => { handleOpenChangeDelete() },
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
                                Cập nhật thông tin nhân viên
                            </ModalHeader>
                            <form
                                className="space-y-4"
                                onSubmit={handleSubmit(handleUpdateEmployee)}
                            >
                                <ModalBody>
                                    <Input
                                        label="id"
                                        variant="bordered"
                                        className='hidden'
                                        {...register("id", { required: true })}
                                        defaultValue={selectedEmployee?.employee.id?.toString()}
                                    />

                                    <Input
                                        label="Họ tên"
                                        variant="bordered"
                                        {...register("name", { required: true })}
                                        defaultValue={selectedEmployee?.employee.name}
                                    />

                                    <Input
                                        label="Số điện thoại"
                                        variant="bordered"
                                        {...register("phoneNumber", { required: true })}
                                        defaultValue={selectedEmployee?.employee.phoneNumber}
                                    />

                                    <Input
                                        label="Ngày sinh"
                                        variant="bordered"
                                        type='date'
                                        {...register("dob", { required: true })}
                                        defaultValue={convertStringInstantToDate(selectedEmployee?.employee.dob)}
                                    />

                                    <Input
                                        label="Biển số xe"
                                        variant="bordered"
                                        {...register("busNumberPlate", { required: false })}
                                        defaultValue={selectedEmployee?.bus?.numberPlate}
                                    />

                                    <Select
                                        label="Vai trò"
                                        placeholder='Chọn vai trò'
                                        selectionMode='single'
                                        size='sm'
                                        value={selectedEmployee?.employee.role}
                                        defaultSelectedKeys={[employee_role_map.find(role => role.value === selectedEmployee?.employee.role)?.value || '']}
                                        color={validateColor(employee_role_map.find(role => role.value === selectedEmployee?.employee.role)?.color || 'default')}
                                        {...register("role", { required: true })}
                                    >
                                        {employee_role_map.map((role) => (
                                            <SelectItem key={role.value} value={role.value} color={validateColor(role.color)}>
                                                {role.label}
                                            </SelectItem>
                                        ))}
                                    </Select>

                                </ModalBody>
                                <ModalFooter>
                                    <Button color='danger' variant="flat" onPress={onOpenChange}>
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
                                    <ModalHeader className="flex flex-col gap-1">Xoá nhân viên {selectedEmployee?.employee.name}</ModalHeader>
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
                                            if (selectedEmployee?.employee.id !== undefined) {
                                                handleDeleteEmployee(selectedEmployee.employee.id);
                                            } else {
                                                console.error('selectedEmployee?.id is undefined');
                                            }
                                        }}>
                                            Xác nhận
                                        </Button>
                                    </ModalFooter>
                                </>
                            )}
                        </ModalContent>
                    </Modal>
                </>
            }
        </div>
    );
};

export default EmployeePage;