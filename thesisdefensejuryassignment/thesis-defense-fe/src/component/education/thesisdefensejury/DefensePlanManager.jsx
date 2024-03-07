import * as React from "react";
import { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
// import { Tab, Tabs } from "@material-ui/core";
// import { a11yProps, TabPanelVertical } from "../programmingcontestFE/TabPanel";
// import DefenseJuryBelongPlan from "./DefenseJuryBelongPlan";
// import DefensePlanDetail from "./DefensePlanDetail";
// import ThesisBelongPlan from "./ThesisBelongPlan";
// import TeacherBelongToPlan from "./TeacherBelongToPlan";
import { request } from "../../../api";
import PrimaryButton from "component/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
export default function DefensePlanManager() {
  const params = useParams();
  const navigate = useNavigate();
  const columns = [
    { title: "Tên hội đồng", field: "name" },
    { title: "Ngày", field: "defenseDate" },
    { title: "Số luận án tối đa", field: "maxThesis" },
    { title: "Keywords", field: "keywords" },
    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <PrimaryButton
          onClick={() => {
            console.log(`/thesis/thesis_defense_plan/${rowData.id}`);
          }}
          variant="contained"
          color="error"
        >
          Xem hội đồng
        </PrimaryButton>
      ),
    },
  ];

  const [dejenseJuries, setDefenseJuries] = useState([]);
  const handleChange = (event, newValue) => {
    setDefenseJuries(newValue);
  };

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      `/thesis-defense-plan/${params.id}`,
      (res) => {
        const data = res.data.defenseJuries;
        setDefenseJuries(
          data.map((item) => ({
            ...item,
            keywords: item?.academicKeywordList
              .map((item) => item.keyword)
              .toString(),
          }))
        );
      }
    );
  }

  useEffect(() => {
    getAllPlan();
  }, []);

  return (
    <div>
      {/* <Tabs
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
      > */}
      {/* <Tab
          label="Thesis Defense Plan Detail"
          {...a11yProps(0)}
          style={{width: "10%"}}
        />
        <Tab label="List Thesis Defense" {...a11yProps(1)} style={{width: "10%"}}/>
        <Tab label="List Thesis" {...a11yProps(2)} style={{width: "10%"}}/>
        <Tab label="List Teacher" {...a11yProps(3)} style={{width: "10%"}}/> */}
      {/* <Tab label="List Student" {...a11yProps(3)} style={{ width: "10%" }} /> */}
      {/* </Tabs> */}
      {/* <TabPanelVertical value={value} index={0}>
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
      </TabPanelVertical> */}
      <PrimaryButton
        onClick={() => {
          navigate(`/thesis/defense_jury/create`);
        }}
        variant="contained"
        color="error"
      >
        Xem hội đồng
      </PrimaryButton>
      <StandardTable
        title={"Danh sách hội đồng bảo vệ"}
        data={dejenseJuries}
        columns={columns}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
