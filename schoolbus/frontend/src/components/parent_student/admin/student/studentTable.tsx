import { Table, TableHeader, TableColumn, TableBody, TableRow, TableCell, Pagination, useDisclosure, Button, Input } from '@nextui-org/react';
import React from 'react';
import { ParentRenderCell } from '../parent/parent-render-cell';
import { useGetListParent, useGetListStudent } from '@/services/admin/accountService';
import { StudentRenderCell } from './student-render-cell';
import { ExportIcon } from '../../../icons/export-icon';
import _ from 'lodash';
import { AddStudent } from './add-student';
import { ModalUpdateStudent } from './update-student';
import ModalDeleteStudent from './delete-student';

const columns = [
    { name: 'HỌ VÀ TÊN', uid: 'name' },
    { name: 'TÊN LỚP', uid: 'studentClass' },
    { name: "SỐ ĐIỆN THOẠI", uid: "phoneNumber" },
    { name: 'ACTIONS', uid: 'actions' },
];
const StudentTable: React.FC = () => {
    // search field
    const [name, setName] = React.useState('');
    const [studentClass, setStudentClass] = React.useState('');
    const [phoneNumber, setPhoneNumber] = React.useState('');

    const debouncedSetName = _.debounce((value: string) => setName(value), 500);
    const debouncedSetPhoneNumber = _.debounce((value: string) => setPhoneNumber(value), 500);
    const debouncedSetStudentClass = _.debounce((value: string) => setStudentClass(value), 500);

    const [selectStudent, setSelectedStudent] = React.useState<IStudent | null>(null);

    // handle open modal
    const { isOpen, onOpen, onOpenChange } = useDisclosure();
    const handleOpenChangeStudent = () => onOpenChange();

    const { isOpen: isOpenDelete, onOpen: onOpenDelete, onOpenChange: onOpenChangeDelete } = useDisclosure();
    const handleOpenChangeDeleteStudent = () => onOpenChangeDelete();

    const [page, setPage] = React.useState(1);

    const { data, isLoading, error } = useGetListStudent({
        id: null,
        name: name,
        dob: null,
        phoneNumber: phoneNumber,
        studentClass: studentClass,
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
            <h3 className="text-xl font-semibold">Danh sách Học Sinh</h3>
            <div className="flex justify-between flex-wrap gap-4 items-center">
                <div className="flex items-center gap-3 flex-wrap md:flex-nowrap w-2/3 m-1 mb-8">
                    <Input
                        classNames={{
                            input: "w-full",
                            mainWrapper: "w-full",
                        }}
                        size='sm'
                        label="Họ và tên"
                        onChange={(e) => debouncedSetName(e.target.value)}
                    />
                    <Input
                        classNames={{
                            input: "w-full",
                            mainWrapper: "w-full",
                        }}
                        size='sm'
                        label="Tên lớp"
                        onChange={(e) => debouncedSetStudentClass(e.target.value)}
                    />
                    <Input
                        classNames={{
                            input: "w-full",
                            mainWrapper: "w-full",
                        }}
                        size='sm'
                        label="Số điện thoại"
                        onChange={(e) => debouncedSetPhoneNumber(e.target.value)}
                    />
                </div>
                <div className="flex flex-row flex-wrap m-1 mb-8">
                    <AddStudent />
                </div>
            </div>
            <Table aria-label="Example table with custom cells"
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
                                        {StudentRenderCell({
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
            <ModalUpdateStudent isOpen={isOpen} onOpenChange={onOpenChange} selectedStudent={selectStudent} />
        </>

    );
};

export default StudentTable;