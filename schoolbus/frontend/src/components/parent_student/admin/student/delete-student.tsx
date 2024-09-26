import { useDeleteParent, useDeleteStudent } from '@/services/admin/accountService';
import { useDeleteStudentClient } from '@/services/client/clientAccountService';
import { Modal, ModalContent, ModalHeader, ModalBody, ModalFooter, Button } from '@nextui-org/react';
import React from 'react';


interface IProps {
    isOpenDelete: boolean;
    onOpenChangeDelete: () => void;
    selectedStudent: IStudent | null;
}
const ModalDeleteStudent: React.FC<IProps> = (
    { isOpenDelete, onOpenChangeDelete, selectedStudent }
) => {
    const useDeleteStudentMutation = useDeleteStudentClient();

    const handleDeleteStudent = (id: number) => {
        useDeleteStudentMutation.mutate(id);
    }
    return (
        <Modal isOpen={isOpenDelete} onOpenChange={onOpenChangeDelete}>
            <ModalContent>
                {(onCloseDelete) => (
                    <>
                        <ModalHeader className="flex flex-col gap-1">Xoá học sinh {selectedStudent?.name}</ModalHeader>
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
                                if (selectedStudent?.id !== undefined) {
                                    handleDeleteStudent(selectedStudent.id);
                                    onOpenChangeDelete();
                                } else {

                                }
                            }}>
                                Xác nhận
                            </Button>
                        </ModalFooter>
                    </>
                )}
            </ModalContent>
        </Modal>
    );
};

export default ModalDeleteStudent;