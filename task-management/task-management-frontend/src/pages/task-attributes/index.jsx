import { Box, Tab, Tabs } from "@mui/material";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import SkillManage from "../../views/task-attributes/SkillManage";
import StatusManage from "../../views/task-attributes/StatusManage";
import CategoryManage from "../../views/task-attributes/CategoryManage";
import PriorityManage from "../../views/task-attributes/PriorityManage";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";

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

const AttributeManager = () => {
  const [value, setValue] = useState(0);
  const { ref, updateMaxHeight } = usePreventOverflow();

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  useEffect(() => {
    updateMaxHeight();
    ref.current?.click();
  }, [window?.innerHeight, ref]);

  return (
    <Box
      sx={{
        display: "flex",
      }}
    >
      <Tabs
        orientation="vertical"
        value={value}
        onChange={handleChange}
        aria-label="Vertical tabs example"
        sx={{ borderRight: 1, borderColor: "divider" }}
      >
        <Tab label="Quản lý kỹ năng" {...a11yProps(0)} />
        <Tab label="Quản lý trạng thái" {...a11yProps(1)} />
        <Tab label="Quản lý danh mục" {...a11yProps(2)} />
        <Tab label="Quản lý độ ưu tiên" {...a11yProps(3)} />
      </Tabs>
      <Box
        ref={ref}
        sx={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          overflowY: "auto",
        }}
      >
        <TabPanel value={value} index={0}>
          <SkillManage />
        </TabPanel>
        <TabPanel value={value} index={1}>
          <StatusManage />
        </TabPanel>
        <TabPanel value={value} index={2}>
          <CategoryManage />
        </TabPanel>
        <TabPanel value={value} index={3}>
          <PriorityManage />
        </TabPanel>
      </Box>
    </Box>
  );
};

export { AttributeManager };
