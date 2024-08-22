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
import DemoScreen from "views/UserListScreen";
import CreateNewCvForm from "views/cv/CreateCV";
import CreateJobPost from "views/jobpost/CreateJobPost";
import ViewAllJobPost from "views/jobpost/ViewAllJobPost";
import ViewJobPostDetail from "views/jobpost/ViewJobPostDetail";
import ViewAllCV from "views/cv/viewAllCV";
import ViewAllApplicant from "views/jobpost/ViewAllApplicant";
import ViewAllJobPostApplicant from "views/jobpost/ViewAllJobPostApplicant";
import ViewAllApplicantStatus from "views/cv/ViewAllApplicantStatus";
import FindJob from "views/findingJob/FindJob";
import UserCompany from "views/company/Company";
import UserSkills from "views/personalInfo/UserSkill";
import UserEducation from "views/personalInfo/UserEducation";
import UserExperience from "views/personalInfo/UserExperience";
import AnalyseCV from "views/analyse_cv/AnalyseCV";
import TestPage from "views/mbti-page/test/TestPage";
import { ChakraProvider, extendTheme } from '@chakra-ui/react';
import theme from '../theme'
import TestResult from "components/test/test-result";
import TestResultPage from "views/mbti-page/test/result/testResultId";
import CVScanner from "views/cvscanner/CVScanner";
import ViewAllApplicant2 from "views/jobpost/ViewAllApplicant2";
import ViewAllJobPost2 from "views/jobpost/ViewAllJobPost2";
import DashboardOverView from "views/Dashboard";

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
  

  const location = useLocation();
  const notificationState = useNotificationState();
  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);
   
  const [userData, setUserData] = useState({})

  useEffect(() => {
    request("get", "/user/get-user-data", (res) => {
    console.log(res.data)
    setUserData(res.data)
  }).then(res => {
  });}, []
)

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>
          <Route component={DashboardOverView} exact path="/" />
          <PrivateRoute component={DemoScreen} exact path="/demo" />
          <PrivateRoute component={TeacherRouter} exact path="/teacher" />
          <PrivateRoute component={CreateNewCvForm} exact path="/create-cv" />
          <PrivateRoute component={CreateJobPost} exact path = "/create-job-post" />
          <PrivateRoute component={ViewAllApplicant} exact path = "/view-job-post/user" />
          <PrivateRoute component={ViewAllApplicant2} exact path = "/view-job-posts/user/:id" />
          <PrivateRoute component={ViewJobPostDetail} exact path = "/view-job-post/:id" /> 
          <PrivateRoute component={ViewAllJobPost2} exact path = "/view-jobs-posts" />          
          <PrivateRoute component={ViewAllJobPost} exact path = "/view-job-posts/employer" />                    
          <PrivateRoute component={ViewAllJobPostApplicant} exact path = "/view-job-post-applicant/:id" />
          <PrivateRoute component={ViewAllApplicantStatus} exact path = "/view-all-applicant" />
          <PrivateRoute component={ViewAllCV} exact path = "/view-all-cv" />
          <PrivateRoute component={FindJob} exact path = "/find-job" />
          <PrivateRoute component={UserCompany} exact  path = "/company" />
          <PrivateRoute component={UserSkills} exact path = "/skill" />
          <PrivateRoute component={UserExperience} exact path = "/experience" />
          <PrivateRoute component={UserEducation} exact path = "/education" />
          <PrivateRoute component={AnalyseCV} exact path = "/analyse" />
          <PrivateRoute component={CVScanner} exact path = "/cv-scanner" />          
          <ChakraProvider theme={theme}>
          <PrivateRoute component={TestPage} exact path = "/test-page" />
          <PrivateRoute component={TestResultPage} exact path = "/test/result/:id" />
          </ChakraProvider>
          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} path="*"/>
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
