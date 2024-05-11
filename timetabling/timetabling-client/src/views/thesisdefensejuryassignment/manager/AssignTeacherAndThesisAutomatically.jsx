import React from "react";
import { Box, Typography } from "@mui/material";
import { useParams, useHistory } from "react-router-dom";
import { useFetch } from "hooks/useFetch";
import { boxChildComponent, boxComponentStyle } from "components/thesisdefensejury/constant";
// import AssignTeacherThesisButton from "component/common/AssignTeacherThesisButton";
// import ElementAddTeacher from "component/education/thesisdefensejury/ElementAddTeacher";
// import { request } from "api";
// import { successNoti, errorNoti } from "utils/notification";

function AssignTeacherAndThesisAutomatically() {
    const { id } = useParams();
    const history = useHistory();

    const { data: teacherList } = useFetch("/defense-jury/teachers");

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
            // defenseJuryTeacherRole: assignedTeacher?.map((item) => ({
            //     teacherName: item?.id,
            //     roleId: 1,
            // })),
        })
    };

    return (
        <>
            <Box sx={{ ...boxComponentStyle, minHeight: "600px" }}>
                <form>
                    {/* <Typography variant="h5">
                        Lựa chọn giáo viên tham gia bảo vệ
                    </Typography>
                    <Box sx={{ ...boxChildComponent, margin: "8px 0px 8px 0px" }}>
                        {teacherList && <ElementAddTeacher teacherList={teacherList} />}
                    </Box>
                    <AssignTeacherThesisButton onClick={onAssignTeacherAndThesis} /> */}
                </form>
            </Box>
        </>
    );
}

export default AssignTeacherAndThesisAutomatically;
