"use client";
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
    Skeleton,
    useDisclosure,
} from "@nextui-org/react";
import React, { Key, use, useEffect, useState } from "react";
import { PlusIcon } from "../../../icons/plus";
import { SubmitHandler, useForm } from "react-hook-form";
import { useAddStudent, useGetListParent, useGetListStudent, useGetStudentDetail, useUpdateStudent } from "@/services/admin/accountService";
import _, { set, toInteger } from 'lodash';
import { SearchIcon } from "../../../icons/searchicon";
import { convertStringInstantToDate } from "@/util/dateConverter";
interface IProps {
    isOpen: boolean;
    selectedStudent: IStudent | null;
    onOpenChange: () => void;
}
export const ModalUpdateStudent = ({ isOpen, selectedStudent, onOpenChange }: IProps) => {
    const [parentSearch, setParentSearch] = React.useState<string>("");
    const [searchBy, setSearchBy] = React.useState<any>("PARENT_NAME");

    const debouncedSetParentSearch = _.debounce((value: string) => setParentSearch(value), 500);

    const updateStudentMutation = useUpdateStudent(onOpenChange);
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

    const { data: parentList, isLoading: parentLoading, error: parentError } = useGetListParent({
        id: null,
        name: parentSearch,
        dob: null,
        page: null,
        size: null,
        phoneNumber: null,
        sort: null,
        sortBy: null,
        searchBy: searchBy
    })
    const { data: studentDetail, isLoading: studentLoading, error: studentError } = useGetStudentDetail(selectedStudent?.id);
    useEffect(() => {
        if (studentDetail) {
            setValue("parentId", studentDetail?.result.parentId)
        }
    }, [studentDetail])
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

                        <div className="flex justify-between">
                            <div className="w-2/3">
                                <Autocomplete
                                    items={parentList ? parentList?.result?.content : []}
                                    defaultSelectedKey={studentDetail?.result?.parentId?.toString() || 1}
                                    allowsCustomValue

                                    label="Phụ huynh"
                                    variant="bordered"

                                    onInputChange={(value) => {
                                        debouncedSetParentSearch(value);
                                    }}
                                    onSelectionChange={(value: any) => {
                                        setValue("parentId", value)
                                    }}
                                    onKeyDown={(e: any) => e.continuePropagation()}
                                    defaultInputValue={studentDetail?.result.parentName || "Nguyễn Văn A"}


                                    startContent={<SearchIcon />}
                                    aria-label="Chọn phụ huynh"
                                    placeholder="Tìm kiếm phụ huynh"

                                >
                                    {(parent) => (
                                        <AutocompleteItem key={parent.id.toString()} textValue={parent.name}>
                                            <div className="flex justify-between items-center">
                                                <div className="flex gap-2 items-center">
                                                    <Avatar alt={parent.name} className="flex-shrink-0" size="sm" src={parent.avatar} />
                                                    <div className="flex flex-col">
                                                        <span className="text-small">{parent.name}</span>
                                                        <span className="text-tiny text-default-400">{parent.phoneNumber}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </AutocompleteItem>

                                    )}
                                </Autocomplete>
                            </div>

                            <div className="w-1/3">
                                <div className="ml-1">
                                    <Select label='Tìm kiếm theo' defaultSelectedKeys={[searchBy]}>
                                        <SelectItem value="PARENT_NAME" onClick={() => setSearchBy("PARENT_NAME")} key={"PARENT_NAME"}>
                                            Tên phụ huynh
                                        </SelectItem>
                                        <SelectItem value="PARENT_PHONE_NUMBER" onClick={() => setSearchBy("PARENT_PHONE_NUMBER")} key={"PARENT_PHONE_NUMBER"}>
                                            Số điện thoại
                                        </SelectItem>
                                    </Select>
                                </div>
                            </div>
                        </div>


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
