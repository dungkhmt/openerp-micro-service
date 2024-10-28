import React, { useEffect, useState } from "react";
import { useParams } from "react-router";
import {
  a11yProps,
  StyledTab,
  StyledTabs,
  TabPanel,
} from "../../../../component/tab";
import LearningSessionStudentViewQuizTestList from "./LearningSessionStudentViewQuizTestList";
import { request } from "../../../../api";
import { makeStyles } from "@material-ui/core/styles";
import ListWhiteBoardStudentView from "../../../../component/education/whiteboard/ListWhiteboardStudentView";
import { Typography } from "@material-ui/core";
import StudentViewInteractiveQuizList from "./StudentViewInteractiveQuizList";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  tabs: {
    backgroundColor: theme.palette.background.paper,
  },
}));

export default function StudentViewLearningSessionDetail() {
  const params = useParams();
  const sessionId = params.sessionId;
  const classes = useStyles();
  const [sessionDetail, setSessionDetail] = useState(null);

  const [activeTab, setActiveTab] = useState(0);

  const handleChange = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };

  function getSessionDetail() {
    request(
      // token,
      // history,
      "get",
      "/edu/class/get-session-detail/" + sessionId,
      (res) => {
        setSessionDetail(res.data);
        console.log("get session, res = ", res.data);
      }
    );
  }

  useEffect(() => {
    //getQuestionList();
    getSessionDetail();
  }, []);

  return (
    <div>
      <div className={classes.tabs}>
        <StyledTabs
          value={activeTab}
          onChange={handleChange}
          aria-label="ant tabs"
          centered
        >
          <StyledTab label="Quiz" {...a11yProps(0)} />
          <StyledTab label="Board" {...a11yProps(1)} />
        </StyledTabs>
        <Typography className={classes.padding} />
      </div>
      <h1>{sessionDetail ? sessionDetail.sessionName : ""}</h1>
      <TabPanel value={activeTab} index={0}>
        {/* <LearningSessionStudentViewQuizTestList sessionId={sessionId} /> */}
        <StudentViewInteractiveQuizList sessionId={sessionId} />
      </TabPanel>
      <TabPanel value={activeTab} index={1}>
        <ListWhiteBoardStudentView />
      </TabPanel>
    </div>
  );
}
