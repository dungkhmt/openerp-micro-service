import { useDeleteParent } from '@/services/admin/accountService';
import { Modal, ModalContent, ModalHeader, ModalBody, ModalFooter, Button } from '@nextui-org/react';
import React from 'react';


interface IProps {
    isOpenDelete: boolean;
    onOpenChangeDelete: () => void;
    selectedParent: IParent | null;
}
const ModalDeleteParent: React.FC<IProps> = (
    { isOpenDelete, onOpenChangeDelete, selectedParent }
) => {
    const useDeleteParentMutaion = useDeleteParent();

    const handleDeleteParent = (id: number) => {
        useDeleteParentMutaion.mutate(id);
    }
    return (
        <Modal isOpen={isOpenDelete} onOpenChange={onOpenChangeDelete}>
            <ModalContent>
                {(onCloseDelete) => (
                    <>
                        <ModalHeader className="flex flex-col gap-1">Xoá phụ huynh {selectedParent?.name}</ModalHeader>
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
                                if (selectedParent?.id !== undefined) {
                                    handleDeleteParent(selectedParent.id);
                                    onOpenChangeDelete();
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
    );
};

export default ModalDeleteParent;