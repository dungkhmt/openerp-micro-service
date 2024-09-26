"use client";
import ParentTable from '@/components/parent_student/admin/parent/parentTable';

import StudentTable from '@/components/parent_student/admin/student/studentTable';
import React from 'react';


const ParentsStudentsPage: React.FC = () => {
    return (
        <div className="flex flex-wrap justify-center">
            <div className='sm:w-full xl:w-1/2'>
                <div className='m-2'>
                    <ParentTable />
                </div>

            </div>

            <div className='sm:w-full xl:w-1/2'>
                <div className='m-2'>
                    <StudentTable />
                </div>

            </div>
        </div>
    )
}

export default ParentsStudentsPage;