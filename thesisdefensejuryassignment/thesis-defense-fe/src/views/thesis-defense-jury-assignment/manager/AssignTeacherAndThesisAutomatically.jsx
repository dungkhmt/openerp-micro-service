import React from "react";
import { Box, Typography } from "@material-ui/core/";
import { useParams, useNavigate } from "react-router-dom";
import { boxChildComponent, boxComponentStyle } from "component/education/thesisdefensejury/constant";
import ElementAddTeacher from "component/education/thesisdefensejury/ElementAddTeacher";
import { useFetchData } from "hooks/useFetchData";
import AssignTeacherThesisButton from "component/common/AssignTeacherThesisButton";
import { useAssignTeacherRole, useAssignThesis } from "action";
import { request } from "api";
import { successNoti, errorNoti } from "utils/notification";

function AssignTeacherAndThesisAutomatically() {
    const { id } = useParams();
    const navigate = useNavigate();

    const teacherList = useFetchData("/defense-jury/teachers");
    const assignedTeacher = useAssignTeacherRole(
        (state) => state.assignedTeacher
    );
    const clearAssignedTeacher = useAssignTeacherRole(
        (state) => state.clearAssignedTeacher
    );

    const onAssignTeacherAndThesis = () => {
        // if (assignedTeacher?.length === 0) {
        //     return errorNoti("Bạn hãy lựa chọn giáo viên", true);
        // }
        // request(
        //     "post",
        //     "/defense-jury/assign",
        //     (res) => {
        //         successNoti("Phân chia hội đồng thành công", true);
        //         clearAssignedTeacher();
        //         return navigate(-1);
        //     },
        //     {
        //         onError: (e) => {
        //             const errorMessage = e?.message;
        //             console.log(e?.message, true);
        //             errorNoti(errorMessage, true);
        //         },
        //     },
        //     {
        //         defenseJuryTeacherRole: assignedTeacher?.map((item) => ({
        //             teacherName: item?.id,
        //             roleId: item?.role,
        //         })),
        //     }
        // ).then();
        console.log({
            id,
            defenseJuryTeacherRole: assignedTeacher?.map((item) => ({
                teacherName: item?.id,
                roleId: 1,
            })),
        })
    };

    return (
        <>
            <Box sx={{ ...boxComponentStyle, minHeight: "600px" }}>
                <form>
                    <Typography variant="h5">
                        Lựa chọn giáo viên tham gia bảo vệ
                    </Typography>
                    <Box sx={{ ...boxChildComponent, margin: "8px 0px 8px 0px" }}>
                        {teacherList && <ElementAddTeacher teacherList={teacherList} />}
                    </Box>
                    <AssignTeacherThesisButton onClick={onAssignTeacherAndThesis} />
                </form>
            </Box>
        </>
    );
}

export default AssignTeacherAndThesisAutomatically;
