import * as React from "react";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {Tab, Tabs} from "@material-ui/core";
import {a11yProps, TabPanelVertical} from "../programmingcontestFE/TabPanel";
import DefenseJuryBelongPlan from './DefenseJuryBelongPlan'
import DefensePlanDetail from './DefensePlanDetail'
import ThesisBelongPlan from './ThesisBelongPlan'
import TeacherBelongToPlan from './TeacherBelongToPlan'


export default function DefensePlanManager() {
  const params = useParams();

  const [value, setValue] = useState(0);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };


//   const location = useLocation();
//   useEffect(() => {
//     console.log(location.state.valueTab); 
//     setValue(location.state.valueTab);
//  }, [location]);


  useEffect(() => {


  }, []);


  return (
    <div>
      <Tabs
        value={value}
        onChange={handleChange}
        indicatorColor={"primary"}
        autoFocus
        style={{
          width: "100%",
          display: "inline-table",
          border: "1px solid transparent ",
          position: "relative",
          borderBottom: "none",
        }}
        // variant={"fullWidth"}
        aria-label="basic tabs example"
      >
        <Tab
          label="Thesis Defense Plan Detail"
          {...a11yProps(0)}
          style={{width: "10%"}}
        />
        <Tab label="List Thesis Defense" {...a11yProps(1)} style={{width: "10%"}}/>
        <Tab label="List Thesis" {...a11yProps(2)} style={{width: "10%"}}/>
        <Tab label="List Teacher" {...a11yProps(3)} style={{width: "10%"}}/>
        {/* <Tab label="List Student" {...a11yProps(3)} style={{ width: "10%" }} /> */}

      </Tabs>

      <TabPanelVertical value={value} index={0}>
        <DefensePlanDetail defensePlanId={params.id}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={1}>
        <DefenseJuryBelongPlan defensePlanId={params.id}/>
      </TabPanelVertical>

      <TabPanelVertical value={value} index={2}>
        <ThesisBelongPlan defensePlanId={params.id}/>
      </TabPanelVertical>
      <TabPanelVertical value={value} index={3}>
        <TeacherBelongToPlan defensePlanId={params.id}/>
      </TabPanelVertical>


    </div>
  );
}
