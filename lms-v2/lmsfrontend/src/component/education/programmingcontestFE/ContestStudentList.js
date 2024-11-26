import { LinearProgress } from "@mui/material";
import { request } from "api";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toFormattedDateTime } from "utils/dateutils";
import { AntTab, AntTabs, TabPanel, a11yProps } from "component/tab"; 

export default function ContestStudentList() {
  const [contests, setContests] = useState([]); 
  const [publicContests, setPublicContests] = useState([]); 
  const [loading, setLoading] = useState(true);
  const [selectedTab, setSelectedTab] = useState(0); 

  const registeredColumns = [
    {
      title: "Contest",
      field: "contestName",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/student-view-contest-detail/" +
              rowData["contestId"],
          }}
        >
          {rowData["contestName"]}
        </Link>
      ),
    },
    { title: "Status", field: "status" },
    { title: "Created By", field: "createdBy" },
    { title: "Created At", field: "createdAt" },
  ];

  const publicColumns = [
    {
      title: "Contest",
      field: "contestName",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/student-view-contest-detail/" +
              rowData["contestId"],
          }}
        >
          {rowData["contestName"]}
        </Link>
      ),
    },
    { title: "Status", field: "status" },
    { title: "Created By", field: "createdBy" }, 
    { title: "Created At", field: "createdAt" }, 
  ];

  function getContestList() {
    request("get", "/students/contests", (res) => {
      const data = res.data.contests.map((e, index) => ({
        index: index + 1,
        contestId: e.contestId,
        contestName: e.contestName,
        status: e.statusId,
        createdBy: e.userId,
        createdAt: toFormattedDateTime(e.startAt),
      }));
      setContests(data);
    }).then(() => setLoading(false));
  }

  function getPublicContestList() {
    request("get", "/contests/public", (res) => { 
      const data = res.data.contests.map((e, index) => ({
        index: index + 1,
        contestId: e.contestId,
        contestName: e.contestName,
        status: e.statusId,
        createdBy: e.userId,
        createdAt: toFormattedDateTime(e.startAt),
      }));
      setPublicContests(data);
    });
    console.log("alo")
  }

  useEffect(() => {
    getPublicContestList(); 
    getContestList();
  }, []);

  const handleChange = (event, newValue) => {
    setSelectedTab(newValue); 
  };

  return (
    <>
      {loading && <LinearProgress />}
      <AntTabs
        value={selectedTab}
        onChange={handleChange}
        aria-label="contest tabs"
        scrollButtons="auto"
        variant="scrollable"
      >
        <AntTab label="Public Contest" {...a11yProps(0)} /> 
        <AntTab label="Registered Contest" {...a11yProps(1)} />
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={"ltr"}>
        <StandardTable
          title={"Public Contest"}
          columns={publicColumns} 
          data={publicContests}
          hideCommandBar
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
        />
      </TabPanel>

      <TabPanel value={selectedTab} index={1} dir={"ltr"}>
        <StandardTable
          title={"Registered Contest"}
          columns={registeredColumns} 
          data={contests}
          hideCommandBar
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
        />
      </TabPanel>
    </>
  );
}
