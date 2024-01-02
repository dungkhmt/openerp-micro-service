import * as React from "react";
import { useTranslation } from "react-i18next";
// import StudentViewProblemList from "./StudentViewProblemList";
import { AntTab, AntTabs, TabPanel, a11yProps } from "component/tab";
import StudentViewProblemList from "./StudentViewProblemListV2";
import StudentViewSubmission from "./StudentViewSubmission";

export default function StudentViewContestDetail() {
  const { t } = useTranslation(
    "education/programmingcontest/studentviewcontestdetail"
  );

  const [selectedTab, setSelectedTab] = React.useState(0);

  const handleChangeTab = (event, newTabValue) => {
    setSelectedTab(newTabValue);
  };

  return (
    <>
      <AntTabs
        value={selectedTab}
        onChange={handleChangeTab}
        aria-label="contest detail tabs"
      >
        <AntTab label={t("problemList.title")} {...a11yProps(0)} />
        <AntTab label={t("submissionList.title")} {...a11yProps(1)} />
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={"ltr"}>
        <StudentViewProblemList />
      </TabPanel>
      <TabPanel value={selectedTab} index={1} dir={"ltr"}>
        <StudentViewSubmission />
      </TabPanel>
    </>
  );
}
