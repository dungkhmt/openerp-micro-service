import React from "react";
import { useParams, useHistory, useLocation } from "react-router-dom";
import { boxChildComponent, boxComponentStyle } from "components/thesisdefensejury/constant";
import { useFetch } from "hooks/useFetch";
import KeywordChip from "components/common/KeywordChip";
import UsersIcon from "@heroicons/react/24/solid/UsersIcon";
import InfoCard from "components/dashboard/InfoCard";
import { Box, Typography, Grid } from "@mui/material";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import ModalLoading from "components/common/ModalLoading";
/**
 * Component hiển thị thông tin hội đồng bảo vệ
 * 
 */

function DefenseJuryDetail(props) {
  const { juryId } = useParams();
  const history = useHistory();
  const location = useLocation();
  const columns = [
    { title: "Tên đồ án", field: "thesisName" },
    {
      title: "Keywords",
      field: "keywords",
      render: (rowData) =>
        rowData.keywords.map((item) => (
          <KeywordChip key={item} keyword={item} />
        )),
    },
    {
      title: "Sinh viên",
      field: "studentName",
    },
    { title: "Giáo viên", field: "supervisor" },
    {
      title: "Giáo viên phản biện",
      field: "scheduledReviewer",
      render: (rowData) => rowData?.scheduledReviewer !== '' ? rowData?.scheduledReviewer : "Đang chờ phân công"
    }
  ];
  const { loading, data: defenseJury } = useFetch(`/defense-jury/${juryId}`);
  const defenseJuryTeacherRoles = defenseJury?.defenseJuryTeacherRoles.map(
    (item) => ({
      id: item.id,
      role: item?.role?.name,
      teacher: item?.teacher.teacherName,
    })
  );

  const thesisList = defenseJury?.thesisList.map((item) => ({
    id: item?.id,
    keywords: item?.academicKeywordList.map((item) => item?.keyword),
    studentName: item?.studentName,
    thesisName: item?.thesisName,
    supervisor: item?.supervisor,
    scheduledReviewer: item?.scheduledReviewer ? item?.scheduledReviewer : ''
  }));
  return (
    <>
      <Box sx={{ ...boxComponentStyle, minHeight: "600px" }}>
        {loading && <ModalLoading loading={loading} />}
        <Typography variant="h4" mb={1} component={"h4"}>
          {defenseJury?.name}
        </Typography>
        <Box sx={{ display: 'flex' }}>
          <Box sx={{ marginRight: 5, display: 'flex' }}>
            Ngày tổ chức :
            <Typography variant="body1" sx={{ marginLeft: 5, fontWeight: 'bold', fontFamily: 'Roboto Condensed', color: 'black' }}> {defenseJury?.defenseDate?.split("T")[0]}</Typography>
          </Box>
          <Box sx={{ marginRight: 5, display: 'flex' }}>
            Ca tổ chức :
            <Typography variant="body1" sx={{ marginLeft: 5, fontWeight: 'bold', fontFamily: 'Roboto Condensed', color: 'black' }}> {defenseJury?.defenseSession?.map(({ name }) => name)?.join(" & ")}</Typography>
          </Box>
          <Box sx={{ marginRight: 5, display: 'flex' }}>
            Phân ban :
            <Typography variant="body1" sx={{ marginLeft: 5, fontWeight: 'bold', fontFamily: 'Roboto Condensed', color: 'black' }}> {defenseJury?.juryTopic?.name}</Typography>
          </Box>
        </Box>
        {defenseJury?.juryTopic?.academicKeywordList.map(({ keyword, description }) => (
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
                <Grid item xs={3.2} key={item?.id}>
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
                history.push(`${location.pathname}/create`);
              }}
              variant="contained"
              color="error"
            >
              Tạo hội đồng mới
            </PrimaryButton>
          </Box>
        )}
      </Box>
    </>
  );
}

export default DefenseJuryDetail;
