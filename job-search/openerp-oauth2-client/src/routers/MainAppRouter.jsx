import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect, useState } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import { request } from "api";
import { createState } from '@hookstate/core';

import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import DemoScreen from "views/DemoScreen";
import CreateNewCvForm from "views/cv/CreateCV";
import UserSkill from "views/skill/Skill";
import CreateJobPost from "views/jobpost/CreateJobPost";
import ViewAllJobPost from "views/jobpost/ViewAllJobPost";
import ViewJobPostDetail from "views/jobpost/ViewJobPostDetail";
import ViewAllCV from "views/cv/viewAllCV";
import ViewAllApplicant from "views/jobpost/ViewAllApplicant";
import ViewAllJobPostApplicant from "views/jobpost/ViewAllJobPostApplicant";
import ViewAllApplicantStatus from "views/cv/ViewAllApplicantStatus";
import FindJob from "views/FindJob";
import UserCompany from "views/Company";
import UserSkills from "views/personalInfo/UserSkill";
import UserEducation from "views/personalInfo/UserEducation";
import UserExperience from "views/personalInfo/UserExperience";
import AnalyseCV from "views/AnalyseCV";
import TestPage from "views/mbti-page/test/TestPage";
import { ChakraProvider, extendTheme } from '@chakra-ui/react';
import theme from '../theme'
import TestResult from "components/test/test-result";
import TestResultPage from "views/mbti-page/test/result/testResultId";
import CVScanner from "views/cvscanner/CVScanner";
import ViewAllApplicant2 from "views/jobpost/ViewAllApplicant2";
import ViewAllJobPost2 from "views/jobpost/ViewAllJobPost2";

const styles = {
  loadingProgress: {
    position: "fixed",
    top: 0,
    left: -drawerWidth,
    width: "calc(100% + 300px)",
    zIndex: 1202,
    "& div": {
      top: "0.5px",
    },
  },
};

function MainAppRouter(props) {
  const userDataState = createState(null);

  const location = useLocation();
  const notificationState = useNotificationState();
  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);

//   useEffect(() => {
//     request("get", "/user/get-user-data", (res) => {
//     console.log(res.data)
//     userDataState.set({user: res.data})
//   }).then(res => {
//   });}, []
// )

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>
          <Route component={() => <></>} exact path="/" />
          <PrivateRoute component={DemoScreen} exact path="/demo" />
          <PrivateRoute component={TeacherRouter} path="/teacher" />
          <PrivateRoute component={CreateNewCvForm} path="/create-cv" />
          <PrivateRoute component={UserSkill} path = "/user-skill" />
          <PrivateRoute component={CreateJobPost} path = "/create-job-post" />
          <PrivateRoute component={ViewAllApplicant} path = "/view-job-post/user" />
          <PrivateRoute component={ViewAllApplicant2} path = "/view-job-posts/user/:id" />
          <PrivateRoute component={ViewJobPostDetail} path = "/view-job-post/:id" /> 
          <PrivateRoute component={ViewAllJobPost2} path = "/view-jobs-posts" />          
          <PrivateRoute component={ViewAllJobPost} path = "/view-job-posts/employer" />                    
          <PrivateRoute component={ViewAllJobPostApplicant} path = "/view-job-post-applicant/:id" />
          <PrivateRoute component={ViewAllApplicantStatus} path = "/view-all-applicant" />
          <PrivateRoute component={ViewAllCV} path = "/view-all-cv" />
          <PrivateRoute component={FindJob} path = "/find-job" />
          <PrivateRoute component={UserCompany} path = "/company" />
          <PrivateRoute component={UserSkills} path = "/skill" />
          <PrivateRoute component={UserExperience} path = "/experience" />
          <PrivateRoute component={UserEducation} path = "/education" />
          <PrivateRoute component={AnalyseCV} path = "/analyse" />
          <PrivateRoute component={CVScanner} path = "/cv-scanner" />          
          <ChakraProvider theme={theme}>
          <PrivateRoute component={TestPage} path = "/test-page" />
          <PrivateRoute component={TestResultPage} path = "/test/result/:id" />
          </ChakraProvider>
          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
