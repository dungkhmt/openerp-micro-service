import { AntTab, AntTabs, TabPanel, a11yProps } from "component/tab";
import withScreenSecurity from "component/withScreenSecurity";
import { useState } from "react";
import { useParams } from "react-router-dom";
import CodeSimilarityCheck from "./CodeSimilarityCheck";
import { ContestManagerDetail } from "./ContestManagerDetail";
import ContestManagerListMember from "./ContestManagerListMember";
import ContestManagerListMemberOfGroup from "./ContestManagerListMemberOfGroup";
import ContestManagerListRegisteredParticipant from "./ContestManagerListRegisteredParticipant";
import { ContestManagerManageProblem } from "./ContestManagerManageProblem";
import ContestManagerRankingGroupNew from "./ContestManagerRankingGroupNew";
import ContestManagerRankingNew from "./ContestManagerRankingNew";
import ContestManagerRankingPercentageNew from "./ContestManagerRankingPercentageNew";

import ContestManagerUserSubmission from "./ContestManagerUserSubmission";
import ContestManagerUserSubmissionGroup from "./ContestManagerUserSubmissionGroup";
import ContestResultDistribution from "./ContestResultDistribution";

function ContestManager() {
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
        <AntTab label="Member" {...a11yProps(2)} />
        <AntTab
          label="Registered User"
          {...a11yProps(3)}
          style={{ width: 140 }}
        />
        {/* <AntTab label="Add Member" {...a11yProps(4)} /> */}
        <AntTab label="Submissions" {...a11yProps(4)} />
        <AntTab label="Ranking" {...a11yProps(5)} />
        <AntTab
          label="Result Distribution"
          {...a11yProps(6)}
          style={{ width: 160 }}
        />

        <AntTab label="Plagiarism" {...a11yProps(7)} />
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

      {/* <TabPanel value={selectedTab} index={4} dir={"ltr"}>
        <ContestManagerAddMember2Contest contestId={contestId} />
      </TabPanel> */}

      <TabPanel value={selectedTab} index={4} dir={"ltr"}>
        <ContestManagerUserSubmission contestId={contestId} />
        <ContestManagerUserSubmissionGroup contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={5} dir={"ltr"}>
        <ContestManagerRankingNew contestId={contestId} />
        <ContestManagerRankingPercentageNew contestId={contestId} />
        <ContestManagerRankingGroupNew contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={6} dir={"ltr"}>
        <ContestResultDistribution contestId={contestId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={7} dir={"ltr"}>
        <CodeSimilarityCheck contestId={contestId} />
      </TabPanel>
    </>
  );
}

const screenName = "SCR_CONTEST_MANAGER";
export default withScreenSecurity(ContestManager, screenName, true);
