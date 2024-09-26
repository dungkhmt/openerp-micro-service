import { Table, TableHeader, TableColumn, TableBody, TableRow, TableCell, Pagination, useDisclosure, Button, Input } from '@nextui-org/react';
import React from 'react';
import { useGetListStudent } from '@/services/admin/accountService';
import _ from 'lodash';
import { AddStudent } from '../../admin/student/add-student';
import { ModalUpdateStudent } from '../../admin/student/update-student';
import ModalDeleteStudent from '../../admin/student/delete-student';
import { ClientStudentRenderCell } from './client-student-render-cell';
import { ClientModalUpdateStudent } from './client-update-student-modal';
import { AddStudentClient } from './add-student-client';
import { useGetListStudentClient } from '@/services/client/clientAccountService';

const columns = [
    { name: 'HỌ VÀ TÊN', uid: 'name' },
    { name: 'TÊN LỚP', uid: 'studentClass' },
    { name: "SỐ ĐIỆN THOẠI", uid: "phoneNumber" },
    { name: 'Ngày sinh', uid: 'dob' },
    { name: 'ACTIONS', uid: 'actions' },
];
const ClientStudentTable: React.FC = () => {

    const [selectStudent, setSelectedStudent] = React.useState<IStudent | null>(null);

    // handle open modal
    const { isOpen, onOpen, onOpenChange } = useDisclosure();
    const handleOpenChangeStudent = () => onOpenChange();

    const { isOpen: isOpenDelete, onOpen: onOpenDelete, onOpenChange: onOpenChangeDelete } = useDisclosure();
    const handleOpenChangeDeleteStudent = () => onOpenChangeDelete();

    const [page, setPage] = React.useState(1);

    const { data, isLoading, error } = useGetListStudentClient({
        id: null,
        name: null,
        dob: null,
        phoneNumber: null,
        studentClass: null,
        parent_id: null,
        page: page - 1,
        size: 10,
        sort: null,
        sortBy: '-createdAt'
    });
    const bottomTable = (
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
        <>
            <div className="flex justify-between flex-wrap gap-4 items-left m-4">
                <h3 className="text-xl font-semibold">Thông tin học sinh</h3>
                <div className="flex flex-row flex-wrap m-1 mb-4">
                    <AddStudentClient />
                </div>
            </div>
            <Table aria-label="Example table with custom cells"
                className='m-4 w-auto'
                bottomContent={bottomTable}>
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
                            <TableRow key={item.id}>
                                {(columnKey) => (
                                    <TableCell>
                                        {ClientStudentRenderCell({
                                            data: item as IStudent,
                                            columnKey: columnKey,
                                            handleOpenChange: () => { handleOpenChangeStudent() },
                                            setSelectedStudent: (student: IStudent) => setSelectedStudent(student),
                                            handleOpenChangeDelete: () => { handleOpenChangeDeleteStudent() },
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
            <ModalDeleteStudent isOpenDelete={isOpenDelete} onOpenChangeDelete={onOpenChangeDelete} selectedStudent={selectStudent} />
            <ClientModalUpdateStudent isOpen={isOpen} onOpenChange={onOpenChange} selectedStudent={selectStudent} />
        </>

    );
};

export default ClientStudentTable;