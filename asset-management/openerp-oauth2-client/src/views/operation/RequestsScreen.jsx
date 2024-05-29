import { a11yProps, AntTab, AntTabs, TabPanel } from 'components/tab';
import React, { useState } from 'react'
import { LocationScreen } from 'views/settings/LocationScreen';
import RequestTable from './request/RequestTable';

const RequestsScreen = () => {
    const tabsLabel = [
        "All Request",
        "Send To Me",
        "Created By Me"
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
                <RequestTable/>
            </TabPanel>
            <TabPanel value={activeTab} index={1}>
                <RequestTable/>
            </TabPanel>
            <TabPanel value={activeTab} index={2}>
                <RequestTable/>
            </TabPanel>
        </div>
    );
};

export default RequestsScreen;