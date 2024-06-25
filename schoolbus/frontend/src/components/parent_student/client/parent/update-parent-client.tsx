import { useAddParent, useGetListStudent, useGetParentDetail, useUpdateParent } from '@/services/admin/accountService';
import { Accordion, AccordionItem, Button, Input, Modal, ModalBody, ModalContent, ModalFooter, ModalHeader, Select, SelectItem, Skeleton, Tooltip, User, useDisclosure } from '@nextui-org/react';
import { register } from 'module';
import React, { useState } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { PlusIcon } from '../../../icons/plus';
import { EyeIcon } from '../../../icons/table/eye-icon';
import { convertStringInstantToDate } from '@/util/dateConverter';
import { useGetParentDetailClient, useUpdateParentClient } from '@/services/client/clientAccountService';

interface IProps {
    isOpen: boolean;
    onOpenChange: () => void;
}

const ModalUpdateParentClient: React.FC<IProps> = (
    { onOpenChange, isOpen }
) => {

    const { data: parentDetail, isLoading, error } = useGetParentDetailClient();

    const updateParentMutation = useUpdateParentClient(onOpenChange);
    const {
        register,
        handleSubmit,
        watch,
        setValue,
        formState: { errors },
    } = useForm<IParentUpdate>();
    const handlerUpdateParent: SubmitHandler<IParentUpdate> = (data) => {
        console.log("data: ", data)
        updateParentMutation.mutate(data)
    };

    if (isLoading) {
        return (
            <Skeleton className="rounded-lg">
                <div className="h-24 rounded-lg bg-default-300"></div>
            </Skeleton>)
    }

    return (
        <div>
            <Modal
                isOpen={isOpen}
                onOpenChange={onOpenChange}
                placement="top-center"
            >
                <ModalContent>
                    <ModalHeader className="flex flex-col gap-1">
                        Cập nhật phụ huynh {parentDetail?.result.name}
                    </ModalHeader>
                    <form
                        className="space-y-4"
                        onSubmit={handleSubmit(handlerUpdateParent)}
                    >
                        <ModalBody>
                            <Input
                                label="id"
                                variant="bordered"
                                {...register("id")}
                                defaultValue={parentDetail?.result.id.toString() || "123"}
                                className='hidden'
                            />


                            <Input
                                label="Họ và tên"
                                variant="bordered"
                                {...register("name",
                                    { required: true, maxLength: 50 })}
                                defaultValue={parentDetail?.result.name || "123"}
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
                                defaultValue={convertStringInstantToDate(parentDetail?.result.dob) || "2021-01-01"}
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
                                defaultValue={parentDetail?.result.phoneNumber || "0123456789"}
                            />
                            {errors.phoneNumber && errors.phoneNumber.type === "required" && (
                                <p className="text-red-500 text-sm">*Số điện thoại không được để trống</p>
                            )}
                            {errors.phoneNumber && errors.phoneNumber.type === "pattern" && (
                                <p className="text-red-500 text-sm">*Số điện thoại phải là 10 hoặc 11 số</p>
                            )}
                            <Accordion defaultExpandedKeys={'all'}>
                                <AccordionItem title="Danh sách học sinh">
                                    {parentDetail?.result.students?.map((student) => (
                                        <div className='flex justify-between mt-2 mb-2' key={student.id}>
                                            <User
                                                name={student.name || ""}
                                                description={student.studentClass || ""}
                                                avatarProps={{
                                                    // src: student.avatar || ""
                                                }} />

                                            <div className="flex items-center gap-4 ">
                                                <div>
                                                    <Tooltip content="Details">
                                                        <button>
                                                            <EyeIcon size={20} fill="#979797" />
                                                        </button>
                                                    </Tooltip>
                                                </div>
                                            </div>

                                        </div>

                                    ))}
                                </AccordionItem>
                            </Accordion>
                            <Accordion>
                                <AccordionItem title='Thay đổi tài khoản'>
                                    <Input
                                        label="Email đăng nhập"
                                        type="email"
                                        variant="bordered"
                                        {...register("username", {
                                            required: false,
                                            pattern: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/
                                        })}
                                        defaultValue={parentDetail?.result.username || ""}



                                    >
                                    </Input>
                                    {errors.username && errors.username.type === "required" && (
                                        <p className="text-red-500 text-sm">*Email không được để trống</p>
                                    )}
                                    {errors.username && errors.username.type === "pattern" && (
                                        <p className="text-red-500 text-sm">*Email không hợp lệ</p>
                                    )}
                                    <Input
                                        label="Mật khẩu"
                                        type="password"
                                        variant="bordered"
                                        {...register("password", {
                                            required: false,
                                            minLength: 6,
                                        })}
                                    >
                                    </Input>
                                    {errors.password && errors.password.type === "required" && (
                                        <p className="text-red-500 text-sm">*Mật khẩu không được để trống</p>
                                    )}
                                    {errors.password && errors.password.type === "minLength" && (
                                        <p className="text-red-500 text-sm">*Mật khẩu phải dài hơn 6 ký tự</p>
                                    )}
                                </AccordionItem>
                            </Accordion>

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
        </div>
    );
};

export default ModalUpdateParentClient;