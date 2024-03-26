import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { boxComponentStyle, boxChildComponent } from "./constant";
import { useFetchData } from "hooks/useFetchData";
import KeywordChip from "component/common/KeywordChip";
import UsersIcon from "@heroicons/react/24/solid/UsersIcon";
import InfoCard from "component/dashboard/InfoCard";
import { Box, Typography, Grid } from "@mui/material";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "component/button/PrimaryButton";
import AssignTeacherAndThesisToDefenseJury from "./AssignTeacherAndThesisToDefenseJury";
function DefenseJuryDetail(props) {
  const params = useParams();
  const columns = [
    { title: "Tên đồ án", field: "thesisName" },
    {
      title: "Sinh viên",
      field: "studentName",
    },
    { title: "Giáo viên", field: "supervisor" },
    {
      title: "Keywords",
      field: "keywords",
      render: (rowData) =>
        rowData.keywords.map((item) => (
          <KeywordChip key={item} keyword={item} />
        )),
    },
  ];
  const defenseJury = useFetchData(`/defense-jury/${params?.juryId}`);
  const defenseJuryTeacherRoles = defenseJury?.defenseJuryTeacherRoles.map(
    (item) => ({
      id: item.id,
      role: item?.role?.role,
      teacher: item?.teacher.teacherName,
    })
  );

  const thesisList = defenseJury?.thesisList.map((item) => ({
    id: item?.id,
    keywords: item?.academicKeywordList.map((item) => item?.keyword),
    studentName: item?.studentName,
    thesisName: item?.thesisName,
    supervisor: item?.supervisor.teacherName,
  }));
  return (
    <>
      <Box sx={{ ...boxComponentStyle, minHeight: "600px" }}>
        <Typography variant="h4" mb={1} component={"h4"}>
          {defenseJury?.name}
        </Typography>
        <div className="defense-jury-info">
          Ngày tổ chức: {defenseJury?.defenseDate?.split("T")[0]}
        </div>
        {defenseJury?.academicKeywordList.map(({ keyword, description }) => (
          <KeywordChip key={keyword} keyword={description} />
        ))}

        {defenseJuryTeacherRoles?.length > 0 ? (
          <>
            <Grid
              container
              justifyContent="space-between"
              sx={{ marginTop: "16px" }}
            >
              {defenseJuryTeacherRoles?.map((item) => (
                <Grid item xs={2.75}>
                  <InfoCard
                    icon={UsersIcon}
                    iconColor="#000"
                    mainTitle={item.teacher}
                    subTitle={item.role}
                  />
                </Grid>
              ))}
            </Grid>
            <StandardTable
              title={"Danh sách đồ án"}
              data={thesisList}
              columns={columns}
              options={{
                selection: false,
                pageSize: 5,
                search: true,
                sorting: true,
              }}
            />
          </>
        ) : (
          <AssignTeacherAndThesisToDefenseJury />
        )}
      </Box>
    </>
  );
}

export default DefenseJuryDetail;
