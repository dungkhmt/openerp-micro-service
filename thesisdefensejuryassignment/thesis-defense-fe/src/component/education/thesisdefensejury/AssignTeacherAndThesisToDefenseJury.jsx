import React, { useState } from "react";
import { Box, Typography, Button } from "@material-ui/core/";
import { useParams } from "react-router-dom";
import { boxChildComponent, boxComponentStyle } from "./constant";
import ElementAddTeacher from "./ElementAddTeacher";
import ElementAddThesis from "./ElementAddThesis";
import { useFetchData } from "hooks/useFetchData";
import KeywordChip from "component/common/KeywordChip";
import AssignTeacherThesisButton from "component/common/AssignTeacherThesisButton";
import { a11yProps, AntTab, AntTabs, TabPanel } from "component/tab";
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
function AssignTeacherAndThesisToDefenseJury() {
  const { id, juryId } = useParams();
  const defenseJury = useFetchData(`/defense-jury/${juryId}`);
  const teacherList = useFetchData("/defense-jury/teachers");
  const availableThesisList = useFetchData(
    `/defense-jury/thesis/get-all-available/${id}`
  );
  const [activeTab, setActiveTab] = useState(0);
  const handleChangeTab = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };
  const tabsLabel = [
    "Danh sách giáo viên",
    "Danh sách đồ án",
  ];

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

        <form>
          <AntTabs value={activeTab}
            onChange={handleChangeTab}
            aria-label="student-view-class-detail-tabs"
            scrollButtons="auto"
            variant="scrollable">
            {tabsLabel.map((label, idx) => (
              <AntTab key={label} label={label} {...a11yProps(idx)} />
            ))}
          </AntTabs>
          <TabPanel value={activeTab} index={0}>
            <Box sx={{ ...boxChildComponent, margin: "8px 0px 8px 0px" }}>
              {teacherList && <ElementAddTeacher teacherList={teacherList} />}
            </Box>
            <Box display={"flex"} flexDirection={"row-reverse"} sx={{ width: "100%" }}>
              <Button type="text" sx={{ color: 'blue' }} endIcon={<ArrowForwardIcon />} onClick={(e) => { setActiveTab((prevActiveTab) => prevActiveTab + 1) }}>
                Lựa chọn đồ án
              </Button>
            </Box>
          </TabPanel>
          <TabPanel value={activeTab} index={1}>
            {availableThesisList && (
              <ElementAddThesis availableThesisList={availableThesisList} />
            )}
            <AssignTeacherThesisButton juryId={juryId} />
          </TabPanel>
        </form>
      </Box>
    </>
  );
}

export default AssignTeacherAndThesisToDefenseJury;
