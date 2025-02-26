import {a11yProps, AntTab, AntTabs, TabPanel} from "component/tab";
import withScreenSecurity from "component/withScreenSecurity";
import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import ListProblemContent from "./ListProblemContent";

function ListProblemV2() {
  const {t} = useTranslation("education/programmingcontest/problem");
  const [selectedTab, setSelectedTab] = useState(0);

  const handleChangeTab = (event, newValue) => {
    setSelectedTab(newValue);
  };

  return (
    <>
      <AntTabs
        value={selectedTab}
        onChange={handleChangeTab}
        aria-label="list problem tabs"
        scrollButtons="auto"
        variant="scrollable"
      >
        <AntTab label={t("myProblems")} {...a11yProps(0)} />
        <AntTab label={t("sharedProblems")} {...a11yProps(1)} />
        <AntTab label={t("publicProblems")} {...a11yProps(2)} />
        {/*<AntTab label={t("allProblems")} {...a11yProps(3)} />*/}
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={"ltr"}>
        <ListProblemContent type={0}/>
      </TabPanel>

      <TabPanel value={selectedTab} index={1} dir={"ltr"}>
        <ListProblemContent type={1}/>
      </TabPanel>

      <TabPanel value={selectedTab} index={2} dir={"ltr"}>
        <ListProblemContent type={2}/>
      </TabPanel>

      {/*
      <TabPanel value={selectedTab} index={3} dir={"ltr"}>
        <ListProblemContent type={3}/>
      </TabPanel>
        */}
    </>
  );
}

const screenName = "SCR_MANAGE_PROBLEMS";
export default withScreenSecurity(ListProblemV2, screenName, true);
