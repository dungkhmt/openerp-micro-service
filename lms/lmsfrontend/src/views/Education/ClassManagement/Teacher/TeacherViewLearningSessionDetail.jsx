import React, { useState } from "react";
import { useParams, useHistory } from "react-router";
import {
  a11yProps,
  StyledTab,
  StyledTabs,
  TabPanel,
} from "../../../../component/tab";
import { makeStyles } from "@material-ui/core/styles";
import { Typography } from "@material-ui/core";
import LearningSessionTeacherViewQuizTestList from "./LearningSessionTeacherViewQuizTestList";
import ListWhiteBoard from "../../../../component/education/whiteboard/ListWhiteBoard";

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

export default function TeacherViewLearningSessionDetail() {
  const history = useHistory();
  const isCourse = history.location.pathname.includes("course");
  const params = useParams();
  const classes = useStyles();
  const sessionId = params.sessionId;

  const [activeTab, setActiveTab] = useState(0);

  const handleChange = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };

  return (
    <div>
      <h1>Session detail {sessionId}</h1>
      <div className={classes.tabs}>
        <StyledTabs
          value={activeTab}
          onChange={handleChange}
          aria-label="ant tabs"
          centered
        >
          <StyledTab label="Quiz" {...a11yProps(0)} />
          <StyledTab label="Bảng viết" {...a11yProps(1)} />
        </StyledTabs>
        <Typography className={classes.padding} />
      </div>
      <TabPanel value={activeTab} index={0}>
        <LearningSessionTeacherViewQuizTestList
          isCourse={isCourse}
          sessionId={sessionId}
        />
      </TabPanel>
      <TabPanel value={activeTab} index={1}>
        <ListWhiteBoard />
      </TabPanel>
    </div>
  );
}
