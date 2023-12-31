import { AntTab, AntTabs, TabPanel, a11yProps } from "component/tab";
import { useState } from "react";
import { useParams } from "react-router-dom";
import CodeSimilarityCheck from "./CodeSimilarityCheck";
import ContestManagerAddMember2Contest from "./ContestManagerAddMember2Contest";
import { ContestManagerDetail } from "./ContestManagerDetail";
import ContestManagerListMember from "./ContestManagerListMember";
import ContestManagerListMemberOfGroup from "./ContestManagerListMemberOfGroup";
import ContestManagerListRegisteredParticipant from "./ContestManagerListRegisteredParticipant";
import { ContestManagerManageProblem } from "./ContestManagerManageProblem";
import ContestManagerRankingGroupNew from "./ContestManagerRankingGroupNew";
import ContestManagerRankingNew from "./ContestManagerRankingNew";
import ContestManagerUserSubmission from "./ContestManagerUserSubmission";
import ContestManagerUserSubmissionGroup from "./ContestManagerUserSubmissionGroup";
import ContestResultDistribution from "./ContestResultDistribution";

export function ContestManager() {
  const { contestId } = useParams();

  const [selectedTab, setSelectedTab] = useState(0);

  const handleChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  return (
    <>
      <AntTabs
        value={selectedTab}
        onChange={handleChange}
        aria-label="contest manager tabs"
        scrollButtons="auto"
        variant="scrollable"
      >
        <AntTab label="General" {...a11yProps(0)} />
        <AntTab label="Problems" {...a11yProps(1)} />
        <AntTab label="List User" {...a11yProps(2)} />
        <AntTab
          label="Registered User"
          {...a11yProps(3)}
          style={{ width: 140 }}
        />
        <AntTab label="Add User" {...a11yProps(4)} />
        <AntTab label="Submissions" {...a11yProps(5)} />
        <AntTab label="Ranking" {...a11yProps(6)} />
        <AntTab
          label="Result Distribution"
          {...a11yProps(7)}
          style={{ width: 160 }}
        />

        <AntTab label="Plagiarism" {...a11yProps(8)} />
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={"ltr"}>
        <ContestManagerDetail contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={1} dir={"ltr"}>
        <ContestManagerManageProblem contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={2} dir={"ltr"}>
        <ContestManagerListMember contestId={contestId} />
        <ContestManagerListMemberOfGroup contestId={contestId} />

        {/* <ContestManagerListParticipant contestId={contestId}/> */}
      </TabPanel>

      <TabPanel value={selectedTab} index={3} dir={"ltr"}>
        <ContestManagerListRegisteredParticipant contestId={contestId} />

        {/* <ContestManagerListRequestingParticipant contestId={contestId}/> */}
      </TabPanel>

      <TabPanel value={selectedTab} index={4} dir={"ltr"}>
        <ContestManagerAddMember2Contest contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={5} dir={"ltr"}>
        <ContestManagerUserSubmission contestId={contestId} />
        <ContestManagerUserSubmissionGroup contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={6} dir={"ltr"}>
        <ContestManagerRankingNew contestId={contestId} />
        <ContestManagerRankingGroupNew contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={7} dir={"ltr"}>
        <ContestResultDistribution contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={8} dir={"ltr"}>
        <CodeSimilarityCheck contestId={contestId} />
      </TabPanel>
    </>
  );
}
