"use client";
import {
    Button,
    Input,
    Modal,
    ModalBody,
    ModalContent,
    ModalFooter,
    ModalHeader,
    Skeleton,
} from "@nextui-org/react";
import React, { Key, use, useEffect, useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { convertStringInstantToDate } from "@/util/dateConverter";
import _ from "lodash";
import { useGetStudentDetailClient, useUpdateStudentClient } from "@/services/client/clientAccountService";
interface IProps {
    isOpen: boolean;
    selectedStudent: IStudent | null;
    onOpenChange: () => void;
}
export const ClientModalUpdateStudent = ({ isOpen, selectedStudent, onOpenChange }: IProps) => {


    const updateStudentMutation = useUpdateStudentClient(onOpenChange);
    const {
        register,
        handleSubmit,
        watch,
        setValue,
        formState: { errors },
    } = useForm<IStudentUpdate>();
    const handUpdateStudent: SubmitHandler<IStudentUpdate> = (data) => {
        updateStudentMutation.mutate(data)
    };


    const { data: studentDetail, isLoading: studentLoading, error: studentError } = useGetStudentDetailClient(selectedStudent?.id);

    if (studentLoading) {
        return (
            <Skeleton className="rounded-lg">
                <div className="h-24 rounded-lg bg-default-300"></div>
            </Skeleton>)
    }
    return (

        <Modal
            isOpen={isOpen}
            onOpenChange={onOpenChange}
            placement="top-center"
            size='2xl'
        >
            <ModalContent>
                <ModalHeader className="flex flex-col gap-1">
                    Cập nhật học sinh
                </ModalHeader>
                <form
                    className="space-y-4"
                    onSubmit={handleSubmit(handUpdateStudent)}
                >
                    <ModalBody>
                        <Input
                            label="id"
                            variant="bordered"
                            {...register("id")}
                            defaultValue={studentDetail?.result.id.toString() || "1234"}
                            className="hidden"
                        />
                        <Input
                            label="Họ và tên"
                            variant="bordered"
                            {...register("name",
                                { required: true, maxLength: 50 })}
                            defaultValue={studentDetail?.result.name || "1234"}
                        />
                        {errors.name && errors.name.type === "required" && (
                            <p className="text-red-500 text-sm">*Tên không được để trống</p>
                        )}
                        {errors.name && errors.name.type === "maxLength" && (
                            <p className="text-red-500 text-sm">*Tên không được dài hơn 50 ký tự</p>
                        )}

                        <Input
                            label="Ngày sinh"
                            variant="bordered"
                            type="date"
                            {...register("dob", { required: true, pattern: /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/ })}
                            defaultValue={convertStringInstantToDate(studentDetail?.result.dob) || "2021-01-01"}
                        />
                        {errors.dob && errors.dob.type === "required" && (
                            <p className="text-red-500 text-sm">*Ngày sinh không được để trống</p>)}
                        {errors.dob && errors.dob.type === "pattern" && (
                            <p className="text-red-500 text-sm">*Ngày sinh không hợp lệ</p>)}
                        <Input
                            label="Số điện thoại"
                            variant="bordered"
                            type="number"
                            {...register("phoneNumber", {
                                required: true,
                                maxLength: 10,
                                pattern: /^[0-9]{10,11}$/
                            })}
                            defaultValue={studentDetail?.result.phoneNumber || "0123456789"}
                        />
                        {errors.phoneNumber && errors.phoneNumber.type === "required" && (
                            <p className="text-red-500 text-sm">*Số điện thoại không được để trống</p>
                        )}
                        {errors.phoneNumber && errors.phoneNumber.type === "pattern" && (
                            <p className="text-red-500 text-sm">*Số điện thoại phải là 10 hoặc 11 số</p>
                        )}

                        <Input
                            label="Lớp"
                            variant="bordered"
                            {...register("studentClass", {
                                required: true,
                                maxLength: 20,
                            })}
                            defaultValue={studentDetail?.result.studentClass || "12A2"}
                        />
                        {errors.studentClass && errors.studentClass.type === "required" && (
                            <p className="text-red-500 text-sm">*Tên lớp không được để trống</p>
                        )}
                        {errors.studentClass && errors.studentClass.type === "maxLength" && (
                            <p className="text-red-500 text-sm">*Tên lớp không được dài hơn 20 ký tự</p>
                        )}

                    </ModalBody>
                    <ModalFooter>
                        <Button color="danger" variant="flat" onPress={onOpenChange}>
                            Huỷ
                        </Button>
                        <Button color="primary" type="submit">
                            Xác nhận
                        </Button>
                    </ModalFooter>
                </form>
            </ModalContent>
        </Modal>

    );
};
