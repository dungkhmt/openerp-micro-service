import React, { useEffect, useState } from "react";
import { Box, Typography, Button, Grid } from "@mui/material";
import { useParams, useHistory } from "react-router-dom";
import { boxChildComponent, boxComponentStyle } from "components/thesisdefensejury/constant";
import ElementAddTeacher from "components/thesisdefensejury/ElementAddTeacher";
import ElementAddThesis from "components/thesisdefensejury/ElementAddThesis";
import { useFetch } from "hooks/useFetch";
import KeywordChip from "components/common/KeywordChip";
import AssignTeacherThesisButton from "components/common/AssignTeacherThesisButton";
import { a11yProps, AntTab, AntTabs, TabPanel } from "components/tab";
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { request } from "api";
import { successNoti, errorNoti } from "utils/notification";
import useAssignTeacherThesis from "hooks/useAssignTeacherThesis";
import PrimaryButton from "components/button/PrimaryButton";
// Màn phân công giáo viên và đồ án
function AssignTeacherAndThesisToDefenseJury() {
  const { id, juryId } = useParams();
  const history = useHistory();
  const [loading, setLoading] = useState(true);
  const { data: defenseJury } = useFetch(`/defense-jury/${juryId}`);
  const { data: availableThesisList } = useFetch(
    `/defense-jury/thesis/get-all-available/${id}`
  );
  const { data: teacherList } = useFetch("/defense-jury/teachers");
  const [activeTab, setActiveTab] = useState(0);
  const sortThesisInJuryTopicInFront = (thesisList) => {
    function compare(a, b) {
      if (a?.juryTopicName === defenseJury?.juryTopic?.name) {
        return -1;
      } else if (b?.juryTopicName === defenseJury?.juryTopic?.name) {
        return 1;
      }
      return a?.juryTopicName?.localeCompare(b?.juryTopicName);
    }
    thesisList?.sort(compare)
    return thesisList;
  }
  const sortedThesisList = sortThesisInJuryTopicInFront(availableThesisList);
  const handleChangeTab = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };
  const tabsLabel = [
    "Danh sách đồ án",
    "Danh sách giáo viên",
  ];
  const { assignedTeacher, assignedThesis, handleAssignRole, handleSelectTeacher, handleSelectThesis, clearAssignedTeacher, clearAssignedThesis, handleSelectTeacherList } = useAssignTeacherThesis();
  console.log(assignedTeacher);
  const onAssignTeacherAndThesis = () => {
    if (assignedTeacher?.length === 0) {
      return errorNoti("Bạn hãy lựa chọn giáo viên", true);
    }
    if (assignedThesis?.length === 0) {
      return errorNoti("Bạn hãy lựa chọn đồ án vào hội đồng", true);
    }
    if (assignedThesis?.length > defenseJury?.maxThesis) {
      return errorNoti(`Hội đồng chỉ có tối đa ${defenseJury?.maxThesis} đồ án`, true);
    }

    request(
      "post",
      "/defense-jury/assign",
      (res) => {
        if (res?.data) {
          if (res?.data === "Successed") {
            successNoti("Phân chia hội đồng thành công", true);
            clearAssignedTeacher();
            clearAssignedThesis();
            return history.goBack();
          }
          else {
            errorNoti(res?.data, true);
          }
        }
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

  // Tính năng gợi ý giáo viên phù hợp hội đồng
  const handleAssignTeacherAuto = (e) => {
    setLoading(true);
    request("POST", `/defense-jury/assign-automatically/${id}/${juryId}`, (res) => {
      console.log(res.data);
      handleSelectTeacherList(res.data);
      setLoading(false)
    }, {
      onError: (e) => {
        const errorMessage = e?.message;
        console.log(e?.message, true);
        errorNoti(errorMessage, true);
      },
    },
      {
        thesisIdList: assignedThesis
      }
    ).then()
  }
  useEffect(() => {
    if (availableThesisList && teacherList && defenseJury) {
      setLoading(false);
    }
  }, [availableThesisList, teacherList, defenseJury])
  return (
    <>
      <Box sx={{ ...boxComponentStyle, minHeight: "600px" }}>
        <Typography variant="h4" mb={1} component={"h4"}>
          {defenseJury?.name}
        </Typography>
        <div className="defense-jury-info">
          Ngày tổ chức: {defenseJury?.defenseDate?.split("T")[0]}
        </div>
        {defenseJury?.juryTopic?.academicKeywordList?.map(({ keyword, description }) => (
          <KeywordChip key={keyword} keyword={description} />
        ))}

        <form>
          <AntTabs value={activeTab}
            onChange={handleChangeTab}
            aria-label="student-view-class-detail-tabs"
            scrollButtons="auto"
            variant="scrollable">
            {tabsLabel?.map((label, idx) => (
              <AntTab key={label} label={label} {...a11yProps(idx)} />
            ))}
          </AntTabs>
          <TabPanel value={activeTab} index={0}>
            <ElementAddThesis availableThesisList={sortedThesisList} handleSelectThesis={handleSelectThesis} assignedThesis={assignedThesis} />
            <Box display={"flex"} flexDirection={"row-reverse"} sx={{ width: "100%" }}>
              <Button type="text" sx={{ color: 'blue' }} endIcon={<ArrowForwardIcon />} onClick={(e) => { setActiveTab((prevActiveTab) => prevActiveTab + 1) }}>
                Lựa chọn giáo viên
              </Button>
            </Box>
          </TabPanel>
          <TabPanel value={activeTab} index={1}>
            <Box display={"flex"} flexDirection={"row-reverse"} sx={{ width: "100%" }}>
              <PrimaryButton onClick={handleAssignTeacherAuto}>Gợi ý giáo viên</PrimaryButton>
            </Box>
            <Box sx={{ ...boxChildComponent, margin: "8px 0px 8px 0px" }}>
              <ElementAddTeacher loading={loading} teacherList={teacherList} assignedTeacher={assignedTeacher} handleAssignRole={handleAssignRole} handleSelectTeacher={handleSelectTeacher} />
            </Box>
            <Grid
              container
              justifyContent="space-between"
              sx={{ marginTop: "16px", width: '100%' }}
            >
              <Grid item xs={3.2}>
                <Button type="text" sx={{ color: 'blue' }} endIcon={<ArrowBackIcon />} onClick={(e) => { setActiveTab((prevActiveTab) => prevActiveTab - 1) }}>
                  Lựa chọn đồ án
                </Button>
              </Grid>
              <Grid justifyContent="right" item xs={"auto"}>
                <AssignTeacherThesisButton onClick={onAssignTeacherAndThesis} >Tạo hội đồng</AssignTeacherThesisButton>
              </Grid>
            </Grid>
          </TabPanel>
        </form>
      </Box>
    </>
  );
}

export default AssignTeacherAndThesisToDefenseJury;
