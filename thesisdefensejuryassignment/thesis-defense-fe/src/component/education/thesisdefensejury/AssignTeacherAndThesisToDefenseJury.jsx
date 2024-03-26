import React, { useState } from "react";
import { Box, Typography } from "@material-ui/core/";
import { useLocation, useParams, useNavigate } from "react-router-dom";
import { boxChildComponent } from "./constant";
import ElementAddTeacher from "./ElementAddTeacher";
import ElementAddThesis from "./ElementAddThesis";
import { useFetchData } from "hooks/useFetchData";
import PrimaryButton from "component/button/PrimaryButton";
import { useAssignTeacherThesis } from "context/AssignTeacherThesisContext";
import { request } from "api";
import { successNoti } from "utils/notification";

function AssignTeacherAndThesisToDefenseJury() {
  const { id, juryId } = useParams();
  const navigate = useNavigate();
  const { register, handleSubmit, assignedTeacher, assignedThesis } =
    useAssignTeacherThesis();
  const teacherList = useFetchData("/defense-jury/teachers");
  const availableThesisList = useFetchData(
    `/defense-jury/thesis/get-all-available/${id}`
  );
  const [open, setOpen] = useState(false);
  const onAssignTeacherAndThesis = (data) => {
    const defenseJuryTeacherRole = [];
    for (const teacherName in data) {
      if (data.hasOwnProperty(teacherName)) {
        const roleId = data[teacherName];
        defenseJuryTeacherRole.push({ teacherName, roleId });
      }
    }
    request(
      "post",
      "/defense-jury/assign",
      (res) => res.data,
      {
        onError: (e) => {
          console.log(e);
        },
      },
      {
        defenseJuryId: juryId,
        defenseJuryTeacherRole,
        thesisIdList: assignedThesis,
      }
    ).then((data) => {
      successNoti("Phân chia hội đồng thành công");
    });
  };
  return (
    <>
      {!open ? (
        <Box
          component="section"
          sx={{
            ...boxChildComponent,
            margin: "5px 2px 5px 2px",
          }}
        >
          <Typography
            variant="overline"
            color="#111927"
            sx={{ fontWeight: 500, fontSize: "14px" }}
            component={"div"}
          >
            Hội đồng này chưa được phân công
          </Typography>
          <PrimaryButton
            onClick={() => {
              return setOpen(true);
            }}
            variant="contained"
            color="error"
          >
            Tạo hội đồng mới
          </PrimaryButton>
        </Box>
      ) : (
        <form onSubmit={handleSubmit(onAssignTeacherAndThesis)}>
          <Box sx={{ ...boxChildComponent, margin: "8px 0px 8px 0px" }}>
            {teacherList && (
              <ElementAddTeacher
                teacherList={teacherList}
                register={register}
              />
            )}
          </Box>
          {availableThesisList && (
            <ElementAddThesis availableThesisList={availableThesisList} />
          )}
          <PrimaryButton type="submit">Tạo hội đồng</PrimaryButton>
        </form>
      )}
    </>
  );
}

export default AssignTeacherAndThesisToDefenseJury;
