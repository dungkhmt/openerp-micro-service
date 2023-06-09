import * as React from "react";
import {useState} from "react";
import {useParams} from "react-router-dom";

import {Tab, Tabs} from "@mui/material";
import {a11yProps, TabPanelVertical} from "./TabPanel";
import CodeSimilarityCheck from "./CodeSimilarityCheck";
import {ContestManagerListProblem} from "./ContestManagerListProblem";
import ContestManagerListMember from "./ContestManagerListMember";
import ContestManagerListRegisteredParticipant from "./ContestManagerListRegisteredParticipant";
import ContestManagerAddMember2Contest from "./ContestManagerAddMember2Contest";

import ContestManagerUserSubmission from "./ContestManagerUserSubmission";
import ContestManagerRankingNew from "./ContestManagerRankingNew";
import ContestResultDistribution from "./ContestResultDistribution";
import {ContestManagerManageProblem} from "./ContestManagerManageProblem";

export function ContestManager() {
  const {contestId} = useParams();

  const [value, setValue] = useState(0);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

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
          marginBottom: "6px"
        }}
        aria-label="basic tabs example"
      >
        <Tab
          label="Contest Detail"
          {...a11yProps(0)}
          style={{width: "11%"}}
        />
        <Tab
          label="Manage Problems"
          {...a11yProps(1)}
          style={{width: "11%"}}
        />
        <Tab label="List User" {...a11yProps(2)} style={{width: "11%"}}/>
        <Tab label="Register User" {...a11yProps(3)} style={{width: "11%"}}/>
        <Tab label="Add User" {...a11yProps(4)} style={{width: "11%"}}/>
        <Tab label="Ranking" {...a11yProps(5)} style={{width: "11%"}}/>
        <Tab
          label="Result Distribution"
          {...a11yProps(6)}
          style={{width: "11%"}}
        />
        <Tab
          label="User Submissions"
          {...a11yProps(7)}
          style={{width: "11%"}}
        />
        <Tab
          label="Check Plagiarism"
          {...a11yProps(8)}
          style={{width: "11%"}}
        />
      </Tabs>

      <TabPanelVertical value={value} index={0}>
        <ContestManagerListProblem contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={1}>
        <ContestManagerManageProblem contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={2}>
        <ContestManagerListMember contestId={contestId}/>
        {/* <ContestManagerListParticipant contestId={contestId}/> */}
      </TabPanelVertical>

      <TabPanelVertical value={value} index={3}>
        <ContestManagerListRegisteredParticipant contestId={contestId}/>
        {/* <ContestManagerListRequestingParticipant contestId={contestId}/> */}
      </TabPanelVertical>

      <TabPanelVertical value={value} index={4}>
        <ContestManagerAddMember2Contest contestId={contestId}/>
        {/* <ContestManagerAddMember contestId={contestId}/> */}
      </TabPanelVertical>

      <TabPanelVertical value={value} index={5}>
        <ContestManagerRankingNew contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={6}>
        <ContestResultDistribution contestId={contestId}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={7}>
        <ContestManagerUserSubmission contestId={contestId}/>
      </TabPanelVertical>


      <TabPanelVertical value={value} index={8}>
        <CodeSimilarityCheck contestId={contestId}/>
      </TabPanelVertical>
    </div>
  );
}
