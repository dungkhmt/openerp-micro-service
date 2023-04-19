import * as React from "react";
import {useParams} from "react-router-dom";

import {Tab, Tabs} from "@mui/material";
import {a11yProps, TabPanelVertical} from "./TabPanel";

import ContestManagerViewListContestProblemSubmissionDetailByTestCase
  from "./ContestManagerViewListContestProblemSubmissionDetailByTestCase";
import CodeSimilarityCheck from "./CodeSimilarityCheck";
import {ContestManagerListProblem} from "./ContestManagerListProblem";
import ContestManagerListParticipant from "./ContestManagerListParticipant";
import ContestManagerListMember from "./ContestManagerListMember";
import ContestManagerListRequestingParticipant from "./ContestManagerListRequestingParticipant";
import ContestManagerListRegisteredParticipant from "./ContestManagerListRegisteredParticipant";

import ContestManagerAddMember from "./ContestManagerAddMember";
import ContestManagerAddMember2Contest from "./ContestManagerAddMember2Contest";

import ContestManagerUserSubmission from "./ContestManagerUserSubmission";
import ContestManagerRankingNew from "./ContestManagerRankingNew";
import ContestResultDistribution from "./ContestResultDistribution";
import {ContestManagerManageProblem} from "./ContestManagerManageProblem";

export function ContestManager() {
  const {contestId} = useParams();

  return (
    <div>
      <Tabs
        value={value}
        onChange={handleChange}
        indicatorColor={"primary"}
        autoFocus
        sx={{
          width: "100%",
          display: "inline-table",
          border: "1px solid transparent ",
          position: "relative",
          borderBottom: "none",
        }}
        aria-label="basic tabs example"
      >
        <Tab
          label="Contest Detail"
          {...a11yProps(0)}
          style={{width: "10%"}}
        />
        <Tab label="List User" {...a11yProps(1)} style={{width: "10%"}}/>
        <Tab label="Register User" {...a11yProps(2)} style={{width: "10%"}}/>
        <Tab label="Add User" {...a11yProps(3)} style={{width: "10%"}}/>
        <Tab label="Ranking" {...a11yProps(4)} style={{width: "10%"}}/>
        <Tab
          label="Result Distribution"
          {...a11yProps(5)}
          style={{width: "10%"}}
        />
        <Tab
          label="User Submission"
          {...a11yProps(6)}
          style={{width: "10%"}}
        />
        <Tab
          label="Submission Detail by TestCase"
          {...a11yProps(7)}
          style={{width: "10%"}}
        />
        <Tab
          label="Check Code Plagiarism"
          {...a11yProps(8)}
          style={{width: "10%"}}
        />
        <Tab
          label="Manage Problems"
          {...a11yProps(9)}
          style={{width: "10%"}}
        />
      </Tabs>

      <TabPanelVertical value={value} index={0}>
        <ContestManagerListProblem contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={1}>
        <ContestManagerListMember contestId={contestId}/>
        <ContestManagerListParticipant contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={2}>
        <ContestManagerListRegisteredParticipant contestId={contestId}/>
        <ContestManagerListRequestingParticipant contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={3}>
        <ContestManagerAddMember2Contest contestId={contestId}/>
        <ContestManagerAddMember contestId={contestId}/>
      </TabPanelVertical>


      <TabPanelVertical value={value} index={4}>
        <ContestManagerRankingNew contestId={contestId}/>
      </TabPanelVertical>

      {/*<TabPanelVertical value={value} index={5}>*/}
      {/*  <ContestManagerUserSubmissionNotEvaluated contestId={contestId} />*/}
      {/*</TabPanelVertical>*/}
      <TabPanelVertical value={value} index={5}>
        <ContestResultDistribution contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={6}>
        <ContestManagerUserSubmission contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={7}>
        <ContestManagerViewListContestProblemSubmissionDetailByTestCase/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={8}>
        <CodeSimilarityCheck contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={9}>
        <ContestManagerManageProblem contestId={contestId}/>
      </TabPanelVertical>
    </div>
  );
}
