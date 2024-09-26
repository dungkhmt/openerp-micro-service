import {Card, CardContent, Paper} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import MaterialTable from "material-table";
import {useEffect, useRef, useState} from "react";
import {useHistory} from "react-router";
import {request} from "../../../../api";
import displayTime from "../../../../utils/DateTimeUtils";
import changePageSize, {localization,} from "../../../../utils/MaterialTableUtils";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
}));

function AssignmentTab({ classId }) {
  const classes = useStyles();

  const history = useHistory();

  // Tables.
  const [assigns, setAssigns] = useState([]);

  // Table refs.
  const assignTableRef = useRef(null);

  const assignCols = [
    // {
    //   field: "id",
    //   title: "Mã bài tập",
    //   headerStyle: {
    //     textAlign: "center",
    //   },
    //   width: 150,
    //   cellStyle: {
    //     textAlign: "center",
    //     fontSize: "1rem",
    //     padding: 5,
    //   },
    // },
    {
      field: "name",
      title: "Tên bài tập",
    },
    {
      field: "closeTime",
      title: "Hạn nộp",
      render: (rowData) => {
        let closeTime = new Date(rowData.closeTime);
        return displayTime(closeTime);
      },
    },
  ];

  const getAssign = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/class/${classId}/assignments/student`,
      (res) => {
        changePageSize(res.data.length, assignTableRef);
        setAssigns(res.data);
      }
    );
  };

  useEffect(() => {
    getAssign();
  }, []);

  return (
    <Card className={classes.card}>
      <CardContent>
        <MaterialTable
          title="Bài tập"
          columns={assignCols}
          localization={localization}
          tableRef={assignTableRef}
          data={assigns}
          components={{
            Container: (props) => <Paper {...props} elevation={0} />,
          }}
          options={{
            pageSize: 10,
            search: false,
            debounceInterval: 300,
            sorting: false,
            cellStyle: {
              whiteSpace: "normal",
              wordBreak: "break-word",
            },
            toolbarButtonAlignment: "left",
          }}
          onRowClick={(event, rowData) => {
            // console.log(rowData);
            history.push(
              `/edu/student/class/${classId}/assignment/${rowData.id}`
            );
          }}
        />
      </CardContent>
    </Card>
  );
}

export default AssignmentTab;
