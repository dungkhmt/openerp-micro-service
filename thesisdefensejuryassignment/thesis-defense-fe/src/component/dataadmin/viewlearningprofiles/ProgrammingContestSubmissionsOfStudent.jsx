import React, {useEffect, useState} from 'react';
import {errorNoti} from "../../../utils/notification";
import {request} from "../../../api";
import {makeStyles, MuiThemeProvider} from "@material-ui/core/styles";
import {Card, CardContent} from "@material-ui/core";
import StandardTable from "../../table/StandardTable";
import {defaultDatetimeFormat} from "../../../utils/dateutils";

const useStyles = makeStyles(theme => ({
  tableWrapper: {
    '& table thead tr': {
      '& th:nth-child(4)': {
        maxWidth: '80px !important'
      },
      '& th:nth-child(5)': {
        maxWidth: '80px !important'
      },
      '& th:nth-child(6)': {
        maxWidth: '80px !important'
      },
      '& th:nth-child(7)': {
        maxWidth: '80px !important'
      }
    }
  }
}))

export default function ProgrammingContestSubmissionsOfStudent(props) {
  const classes = useStyles();
  const studentLoginId = props.studentLoginId;
  const [contestSubmissionsOfStudent, setContestSubmissionsOfStudent] = useState({ content: [], totalElements: 0 });
  const [filterParams, setFilterParams] = useState({ search: '', page: 0, size: 20 });

  useEffect(getContestSubmissionsOfStudent, [filterParams]);

  function getContestSubmissionsOfStudent() {
    let successHandler = res => setContestSubmissionsOfStudent({
      content: res.data.content,
      totalElements: res.data.totalElements
    });
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request(
      "GET", `/admin/data/programming-contests/submissions/${studentLoginId}`,
      successHandler, errorHandlers, null, { params: filterParams }
    );
  }

  const columns = [
    { title: "Tên contest", field: "contestName" },
    { title: "Tên bài toán", field: "problemName" },
    { title: "Submission ID", field: "submissionId" },
    {
      title: "Trạng thái", field: "status",
      cellStyle: { maxWidth: '80px' }
    },
    {
      title: "Test cases", field: "testCasePass",
      cellStyle: { maxWidth: '80px' }
    },
    {
      title: "Điểm số", field: "point",
      cellStyle: { maxWidth: '80px'}
    },
    {
      title: "Ngôn ngữ", field: "sourceCodeLanguage",
      cellStyle: { maxWidth: '80px' }
    },
    {
      title: "Ngày nộp", field: "submitAt",
      render: submission => defaultDatetimeFormat(submission),
      cellStyle: { maxWidth: '160px' }
    }
  ]

  return (
    <MuiThemeProvider>
      <Card>
        <CardContent className={classes.tableWrapper}>
          <StandardTable
            title="Lịch sử làm contest"
            columns={columns}
            data={contestSubmissionsOfStudent.content}
            hideCommandBar
            options={{
              selection: false,
              search: true,
              sorting: true,
              pageSize: filterParams.size,
              searchText: filterParams.search,
              debounceInterval: 1000
            }}
            page={filterParams.page}
            totalCount={contestSubmissionsOfStudent.totalElements}
            onChangePage={ (page, size) => setFilterParams({...filterParams, page, size}) }
            onSearchChange={ search => setFilterParams({page: 0, size: filterParams.size, search}) }
          />
        </CardContent>
      </Card>
    </MuiThemeProvider>
  );
}