import { Box } from "@material-ui/core/";
import PrimaryButton from "component/button/PrimaryButton";
import { useAssignTeacherRole, useAssignThesis } from "action";
import { request } from "api";
import { successNoti, errorNoti } from "utils/notification";
import { useNavigate } from "react-router-dom";

export default function AssignTeacherThesisButton({ juryId }) {
  const navigate = useNavigate();

  const assignedTeacher = useAssignTeacherRole(
    (state) => state.assignedTeacher
  );
  const assignedThesis = useAssignThesis((state) => state.assignedThesis);
  const clearAssignedTeacher = useAssignTeacherRole(
    (state) => state.clearAssignedTeacher
  );

  const clearAssignedThesis = useAssignThesis(
    (state) => state.clearAssignedThesis
  );
  const onAssignTeacherAndThesis = () => {
    if (assignedTeacher?.length === 0) {
      return errorNoti("Bạn hãy lựa chọn giáo viên", true);
    }
    if (assignedThesis?.length === 0) {
      return errorNoti("Bạn hãy lựa chọn đồ án vào hội đồng", true);
    }
    request(
      "post",
      "/defense-jury/assign",
      (res) => {
        successNoti("Phân chia hội đồng thành công", true);
        clearAssignedTeacher();
        clearAssignedThesis();
        return navigate(-1);
      },
      {
        onError: (e) => {
          const errorMessage = e?.message;
          console.log(e?.message, true);
          errorNoti(errorMessage, true);
        },
      },
      {
        defenseJuryId: juryId,
        defenseJuryTeacherRole: assignedTeacher?.map((item) => ({
          teacherName: item?.id,
          roleId: item?.role,
        })),
        thesisIdList: assignedThesis,
      }
    ).then();
  };

  return (
    <Box display={"flex"} flexDirection={"row-reverse"} marginTop={3}>
      <PrimaryButton onClick={onAssignTeacherAndThesis}>
        Tạo hội đồng
      </PrimaryButton>
    </Box>
  );
}
