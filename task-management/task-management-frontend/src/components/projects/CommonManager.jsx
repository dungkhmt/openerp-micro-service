import { Box, Tab, Tabs } from "@mui/material";
import PropTypes from "prop-types";
import { useState } from "react";
import { boxComponentStyle } from "../utils/constant";
import CategoryManage from "./manage/CategoryManage";
import PriorityManage from "./manage/PriorityManage";
import SkillManage from "./manage/SkillManage";
import StatusManage from "./manage/StatusManage";

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <Box flexGrow={1} display={value !== index ? "none" : ""}>
      <div
        role="tabpanel"
        id={`vertical-tabpanel-${index}`}
        aria-labelledby={`vertical-tab-${index}`}
        {...other}
      >
        {value === index && <Box sx={{ px: 3, py: 1 }}>{children}</Box>}
      </div>
    </Box>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `vertical-tab-${index}`,
    "aria-controls": `vertical-tabpanel-${index}`,
  };
}

const CommonManager = () => {
  const [value, setValue] = useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  return (
    <Box sx={boxComponentStyle} display={"flex"}>
      <Tabs
        orientation="vertical"
        value={value}
        onChange={handleChange}
        aria-label="Vertical tabs example"
        sx={{ borderRight: 1, borderColor: "divider" }}
      >
        <Tab label="Quản lý danh mục" {...a11yProps(0)} />
        <Tab label="Quản lý độ ưu tiên" {...a11yProps(1)} />
        <Tab label="Quản lý trạng thái" {...a11yProps(2)} />
        <Tab label="Quản lý kỹ năng" {...a11yProps(3)} />
      </Tabs>
      <TabPanel value={value} index={0}>
        <CategoryManage />
      </TabPanel>
      <TabPanel value={value} index={1}>
        <PriorityManage />
      </TabPanel>
      <TabPanel value={value} index={2}>
        <StatusManage />
      </TabPanel>
      <TabPanel value={value} index={3}>
        <SkillManage />
      </TabPanel>
    </Box>
  );
};

export default CommonManager;
