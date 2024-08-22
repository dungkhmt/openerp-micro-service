"use client";

import { Avatar, Button, Card, User, useDisclosure } from '@nextui-org/react';
import React from 'react';
import { EditIcon } from "@/components/icons/table/edit-icon";
import { useGetParentDetailClient } from '@/services/client/clientAccountService';
import { convertStringInstantToDate } from '@/util/dateConverter';
import ClientStudentTable from '@/components/parent_student/client/student/client-student-table';
import ModalUpdateParentClient from '@/components/parent_student/client/parent/update-parent-client';

const ClientPage: React.FC = () => {
    const { data: parentDetail, isLoading: parentDetailLoading, error: parentDetailError } = useGetParentDetailClient();
    const { isOpen: isOpenView, onOpen: onOpenView, onOpenChange: onOpenChangeView } = useDisclosure();
    const handleOpenChangeViewParent = () => onOpenChangeView();
    return (
        <div>
            <div className="flex justify-between flex-wrap gap-4 items-left m-4">
                <h3 className="text-xl font-semibold">Thông tin phụ huynh</h3>
                <div className="flex flex-row flex-wrap m-1 mb-8">

                </div>
            </div>

            <Card className='mx-4 flex flex-row items-center justify-between p-4 mb-16'>
                <div className="flex flex-row gap-2 m-2 w-auto">
                    <User
                        avatarProps={{
                            // src: "https://www.w3schools.com/howto/img_avatar.png",
                        }}
                        name=""
                    >
                    </User>

                    <div className="flex flex-row gap-4">
                        <div className="flex flex-col gap-2">
                            <div className="flex flex-row gap-4">
                                <div className="font-semibold">Họ và tên:</div>
                                <div>{parentDetail?.result.name}</div>
                            </div>
                            <div className="flex flex-row gap-4">
                                <div className="font-semibold">Số điện thoại:</div>
                                <div>{parentDetail?.result.phoneNumber}</div>
                            </div>
                            <div className="flex flex-row gap-4">
                                <div className="font-semibold">Email: {parentDetail?.result.username}</div>
                                <div>
                                    <a href="mailto:" className="text-blue-500" />
                                </div>
                            </div>
                        </div>
                        <div className="flex flex-col gap-2">
                            <div className="flex flex-row gap-4">
                                <div className="font-semibold">Ngày sinh:</div>
                                <div>{convertStringInstantToDate(parentDetail?.result.dob)}</div>
                            </div>
                            {/* <div className="flex flex-row gap-4">
                                <div className="font-semibold">Địa chỉ:</div>
                                <div>{parentDetail?.result.</div>
                            </div> */}
                        </div>
                    </div>
                </div>

                <Button
                    onClick={handleOpenChangeViewParent}
                    color="primary"
                    endContent={
                        <EditIcon size={20} fill="#000000" />
                    }
                >
                    Chỉnh sửa thông tin
                </Button>
            </Card>

            <ClientStudentTable />
            <ModalUpdateParentClient
                isOpen={isOpenView}
                onOpenChange={handleOpenChangeViewParent}
            />
        </div>
    );
};

export default ClientPage;