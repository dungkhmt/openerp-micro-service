import {
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
import { useAddEmployee } from "@/services/admin/employeeService";
import { employee_role_map } from "@/util/constant";

export const AddEmployee = () => {
    const { isOpen, onOpen, onOpenChange } = useDisclosure();

    const addEmployeeMutation = useAddEmployee(onOpenChange);

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
    } = useForm<IAddEmployee>();
    const handleAddEmployee: SubmitHandler<IAddEmployee> = (data) => {
        addEmployeeMutation.mutate(data);
    }

    return (
        <div>
            <Button onPress={onOpen} color="primary" endContent={<PlusIcon />}>
                Thêm nhân viên
            </Button>
            <Modal
                isOpen={isOpen}
                onOpenChange={onOpenChange}
                placement="top-center"
            >
                <ModalContent>
                    <ModalHeader className="flex flex-col gap-1">
                        Thêm nhân viên
                    </ModalHeader>
                    <form
                        className="space-y-4"
                        onSubmit={handleSubmit(handleAddEmployee)}
                    >
                        <ModalBody>

                            <div className="flex justify-between gap-2">
                                <div className="flex flex-col gap-3 w-full">
                                    <Input
                                        label="Họ tên"
                                        variant="bordered"
                                        {...register("name", { required: true, maxLength: 50 })}
                                    />
                                    {errors?.name && errors.name.type === "required" && (
                                        <p className="text-red-500 text-sm">*Tên không được để trống</p>
                                    )}
                                    {errors?.name && errors.name.type === "maxLength" && (
                                        <p className="text-red-500 text-sm">*Tên không được dài hơn 50 ký tự</p>
                                    )}
                                    <Input
                                        label="Avatar"
                                        variant="bordered"
                                        {...register("avatar", { required: false })}
                                    />
                                    <Input
                                        label="Ngày sinh"
                                        variant="bordered"
                                        type="date"
                                        {...register("dob", { required: true })}
                                    />
                                    {errors?.dob && errors.dob.type === "required" && (
                                        <p className="text-red-500 text-sm">*Ngày sinh không được để trống</p>)}
                                    <Input
                                        label="Số điện thoại"
                                        variant="bordered"
                                        type="number"
                                        {...register("phoneNumber", {
                                            required: true,
                                            maxLength: 10,
                                            pattern: /^[0-9]{10,11}$/
                                        })}
                                    />
                                    {errors?.phoneNumber && errors?.phoneNumber.type === "required" && (
                                        <p className="text-red-500 text-sm">*Số điện thoại không được để trống</p>
                                    )}
                                    {errors?.phoneNumber && errors?.phoneNumber.type === "maxLength" && (
                                        <p className="text-red-500 text-sm">*Số điện thoại phải là 10 hoặc 11 số</p>
                                    )}
                                    <Input
                                        label="Xe Bus hiện tại"
                                        variant="bordered"
                                        {...register("busNumberPlate", { required: false })}
                                    />

                                    <Select
                                        label="Vai trò"
                                        placeholder='Chọn vai trò'
                                        selectionMode='single'
                                        {...register("role", { required: true })}
                                    >
                                        {employee_role_map.map((role) => (
                                            <SelectItem key={role.value} value={role.value}>
                                                {role.label}
                                            </SelectItem>
                                        ))}
                                    </Select>
                                </div>

                                <div className="flex flex-col gap-3 w-full">
                                    <Input
                                        label="Email đăng nhập"
                                        variant="bordered"
                                        {...register("username", {
                                            required: true,
                                            pattern: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/
                                        })}
                                    />
                                    {errors.username && errors.username.type === "required" && (
                                        <p className="text-red-500 text-sm">*Email không được để trống</p>
                                    )}
                                    {errors.username && errors.username.type === "pattern" && (
                                        <p className="text-red-500 text-sm">*Email không hợp lệ</p>
                                    )}
                                    <Input
                                        label="Mật khẩu"
                                        variant="bordered"
                                        type="password"
                                        {...register("password", {
                                            required: true,
                                            minLength: 6
                                        })}
                                    />
                                    {errors.password && errors.password.type === "required" && (
                                        <p className="text-red-500 text-sm">*Mật khẩu không được để trống</p>
                                    )}
                                    {errors.password && errors.password.type === "minLength" && (
                                        <p className="text-red-500 text-sm">*Mật khẩu phải dài hơn 6 ký tự</p>
                                    )}
                                    <Input
                                        label="Nhập lại mật khẩu"
                                        variant="bordered"
                                        type="password"
                                        {...register("confirmPassword", {
                                            required: true,
                                            validate: (value) => value === watch("password") || "Mật khẩu không khớp"
                                        })}
                                    />
                                    {errors.confirmPassword && errors.confirmPassword.type === "required" && (
                                        <p className="text-red-500 text-sm">*Mật khẩu không được để trống</p>
                                    )}
                                </div>
                            </div>



                        </ModalBody>
                        <ModalFooter>
                            <Button color="danger" variant="flat" onPress={onOpenChange}>
                                Hủy
                            </Button>
                            <Button color="primary" type="submit">
                                Thêm nhân viên
                            </Button>
                        </ModalFooter>
                    </form>
                </ModalContent>
            </Modal>
        </div>
    );
}