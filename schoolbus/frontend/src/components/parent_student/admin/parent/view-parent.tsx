import { useGetParentDetail } from '@/services/admin/accountService';
import { convertStringInstantToDate } from '@/util/dateConverter';
import { Skeleton, Modal, ModalContent, ModalHeader, ModalBody, Accordion, AccordionItem, User, Tooltip, ModalFooter, Button, Input } from '@nextui-org/react';
import React from 'react';
import { EyeIcon } from '../../../icons/table/eye-icon';
interface IProps {
    isOpen: boolean;
    selectedParent: IParent | null;
    onOpenChange: () => void;
}

const ModalViewParent: React.FC<IProps> = (
    { selectedParent, onOpenChange, isOpen }
) => {

    const { data: parentDetail, isLoading, error } = useGetParentDetail(selectedParent?.id);

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
                size='2xl'
            >
                <ModalContent>
                    <ModalHeader className="flex flex-col gap-1">
                        Chi tiết phụ huynh {parentDetail?.result.name}
                    </ModalHeader>
                    <form
                        className="space-y-4"
                    >
                        <ModalBody>
                            <div className='flex justify-between gap-2'>
                                <div className='flex flex-col gap-3 w-full'>
                                    <Input
                                        label="id"
                                        variant="bordered"
                                        defaultValue={parentDetail?.result.id.toString() || "123"}
                                        className='hidden'
                                        disabled
                                    />


                                    <Input
                                        label="Họ và tên"
                                        variant="bordered"
                                        defaultValue={parentDetail?.result.name || "123"}
                                        disabled
                                    />

                                    <Input
                                        label="Ngày sinh"
                                        variant="bordered"
                                        type="date"
                                        defaultValue={convertStringInstantToDate(parentDetail?.result.dob) || "2021-01-01"}
                                        disabled
                                    />

                                    <Input
                                        label="Số điện thoại"
                                        variant="bordered"
                                        type="number"
                                        defaultValue={parentDetail?.result.phoneNumber || "0123456789"}
                                        disabled
                                    />
                                </div>
                                <div className='flex flex-col gap-3 w-full'>
                                    <Input
                                        label="Thời gian tạo"
                                        variant="bordered"
                                        defaultValue={convertStringInstantToDate(parentDetail?.result.created_at) || ""}
                                    />
                                    <Input
                                        label="Thời gian cập nhật"
                                        variant="bordered"
                                        defaultValue={convertStringInstantToDate(parentDetail?.result.updated_at) || ""}
                                    />
                                </div>
                            </div>


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
                            <Accordion defaultExpandedKeys={'all'}>
                                <AccordionItem title='Tài khoản'>
                                    <Input
                                        label="Email đăng nhập"
                                        type="email"
                                        variant="bordered"
                                        defaultValue={parentDetail?.result.username || ""}
                                        disabled
                                    >
                                    </Input>
                                </AccordionItem>
                            </Accordion>

                        </ModalBody>
                        <ModalFooter>
                            <Button color="danger" variant="flat" onPress={onOpenChange}>
                                Đóng
                            </Button>
                        </ModalFooter>
                    </form>
                </ModalContent>
            </Modal>
        </div>
    );
};

export default ModalViewParent;