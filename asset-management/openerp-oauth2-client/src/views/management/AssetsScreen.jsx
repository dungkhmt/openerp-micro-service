import { AntTab, AntTabs, TabPanel, a11yProps } from "components/tab";
import { useState } from "react";
import AssetTable from "./AssetTable";
import AssetTableToMe from "./AssetTableToMe";
import AssetTableByMe from "./AssetTableByMe";

const AssetsScreen = () => {    
    const tabsLabel = [
        "All Assets",
        "Assign To Me",
        "Manage By Me"
    ];

    const [activeTab, setActiveTab] = useState(0);
    const handleChangeTab = (event, tabIndex) => {
        setActiveTab(tabIndex);
    };

    return (
        <div>
            <AntTabs
                value={activeTab}
                onChange={handleChangeTab}
                aria-label="student-view-class-detail-tabs"
                scrollButtons="auto"
                variant="scrollable"
            >
                {tabsLabel.map((label, idx) => (
                <AntTab key={label} label={label} {...a11yProps(idx)} />
                ))}
            </AntTabs>
            <TabPanel value={activeTab} index={0}>
                <AssetTable/>
            </TabPanel>
            <TabPanel value={activeTab} index={1}>
                <AssetTableToMe/>
            </TabPanel>
            <TabPanel value={activeTab} index={2}>
                <AssetTableByMe/>
            </TabPanel>
        </div>
    )
};

export default AssetsScreen;
