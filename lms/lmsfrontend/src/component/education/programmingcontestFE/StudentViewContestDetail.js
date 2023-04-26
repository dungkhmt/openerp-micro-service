import Box from "@mui/material/Box";
import Tab from "@mui/material/Tab";
import Tabs from "@mui/material/Tabs";
import Typography from "@mui/material/Typography";
import * as React from "react";
import {useTranslation} from "react-i18next";
// import StudentViewProblemList from "./StudentViewProblemList";
import StudentViewProblemList from "./StudentViewProblemListV2";
import StudentViewSubmission from "./StudentViewSubmission";
import HustContainerCard from "../../common/HustContainerCard";

function TabPanel(props) {
  const {children, value, index, ...other} = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{paddingTop: 3}}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

export default function StudentViewContestDetail() {
  const {t} = useTranslation("education/programmingcontest/studentviewcontestdetail");

  const [tab, setTab] = React.useState(0);

  const handleChangeTab = (event, newTabValue) => {
    setTab(newTabValue);
  };

  return (
    <HustContainerCard>
      <Box sx={{borderBottom: 1, borderColor: "divider"}}>
        <Tabs
          value={tab}
          onChange={handleChangeTab}
          variant="fullWidth"
        >
          <Tab label={t("problemList.title")} {...a11yProps(0)} />
          <Tab label={t("submissionList.title")} {...a11yProps(1)} />
        </Tabs>
      </Box>
      <TabPanel value={tab} index={0}>
        <StudentViewProblemList/>
      </TabPanel>
      <TabPanel value={tab} index={1}>
        <StudentViewSubmission/>
      </TabPanel>
    </HustContainerCard>
  );
}
