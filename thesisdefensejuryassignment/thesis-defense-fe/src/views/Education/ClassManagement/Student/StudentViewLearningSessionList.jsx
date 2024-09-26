import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {Link, Typography} from "@material-ui/core/";
import {TabPanel,} from "../../../../component/tab";
import MaterialTable from "material-table";
import {Link as RouterLink} from "react-router-dom";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  tabs: {
    backgroundColor: theme.palette.background.paper,
  },
}));

export default function StudentViewLearningSessionList(props) {
  const classId = props.classId;
  const classes = useStyles();
  const [sessions, setSessions] = useState([]);

  const [activeTab, setActiveTab] = useState(0);

  const handleChange = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };

  const columns = [
    {
      title: "Session",
      field: "sessionId",
      render: (rowData) => (
        <Link
          component={RouterLink}
          to={`/edu/student/class/session/detail/${rowData["sessionId"]}`}
        >
          {rowData["sessionName"]}
        </Link>
      ),
    },
    { title: "Description", field: "description" },
    { title: "Created By", field: "createdByUserLoginId" },
    { title: "Status", field: "statusId" },
  ];

  function getSessionsOfClass() {
    request(
      "get",
      "/edu/class/get-sessions-of-class/" + classId,
      (res) => {
        console.log(res);
        setSessions(res.data);
      },
      { 401: () => {} }
    );
  }

  useEffect(() => {
    getSessionsOfClass();
  }, []);

  return (
    <TabPanel value={activeTab} index={0}>
      <MaterialTable
        title="Sessions"
        columns={columns}
        data={sessions}
        //icons={tableIcons}
        localization={{
          header: {
            actions: "",
          },
          body: {
            emptyDataSourceMessage: "No records",
            filterRow: {
              filterTooltip: "Lọc",
            },
          },
        }}
        options={{
          search: true,
          sorting: false,
          actionsColumnIndex: -1,
          pageSize: 8,
          tableLayout: "fixed",
        }}
        style={{
          fontSize: 14,
        }}
      />
    </TabPanel>
  );
}
