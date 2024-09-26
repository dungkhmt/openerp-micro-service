import * as React from 'react';
import { Box, Tab } from '@mui/material';
import { TabContext, TabList, TabPanel } from '@mui/lab';
import RevuenueReport from './revenueReport';
import ProductCategoryMonthlyReport from './productCategoryMonthlyReport';
import ProductsReport from './productsReport';

const PresidentReport = () => {
  const [value, setValue] = React.useState('1');

  const handleChange = (event, newValue) => {
    setValue(newValue);
  }

  return <div>
    <Box sx={{ width: '100%', typography: 'body1' }}>
      <TabContext value={value}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <TabList onChange={handleChange} >
            <Tab label="Doanh thu - Lợi nhuận" value="1" />
            <Tab label="Lợi nhuận theo nhóm hàng" value="2" />
            <Tab label="Hàng tồn kho" value="3" />
          </TabList>
        </Box>
        <TabPanel value="1"><RevuenueReport /></TabPanel>
        <TabPanel value="2"><ProductCategoryMonthlyReport /></TabPanel>
        <TabPanel value="3"><ProductsReport /></TabPanel>
      </TabContext>
    </Box>
  </div>
}

export default PresidentReport;