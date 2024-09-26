import React, {useEffect, useState} from 'react';
import {request} from "../../../api";
import {errorNoti} from "../../../utils/notification";
import {Card, CardContent} from "@material-ui/core";
import StandardTable from "../../table/StandardTable";
import {makeStyles, MuiThemeProvider} from "@material-ui/core/styles";
import {defaultDatetimeFormat} from "../../../utils/dateutils";

const useStyles = makeStyles(theme => ({
  tableWrapper: {
    '& table thead tr': {
      '& th:nth-child(1)': {
        maxWidth: '120px !important'
      },
      '& th:nth-child(3)': {
        maxWidth: '80px !important'
      },
      '& th:nth-child(4)': {
        maxWidth: '80px !important'
      },
      '& th:nth-child(7)': {
        maxWidth: '160px !important'
      }
    }
  }
}))

export default function ViewClassMaterialLogsOfStudent(props) {
  const classes = useStyles();
  const studentLoginId = props.studentLoginId;
  const [viewMaterialLogsOfStudent, setViewMaterialLogsOfStudent] = useState({ content: [], totalElements: 0 });
  const [filterParams, setFilterParams] = useState({ search: '', page: 0, size: 20 });

  useEffect(getViewMaterialLogsOfStudent, [filterParams]);

  function getViewMaterialLogsOfStudent() {
    let successHandler = res => setViewMaterialLogsOfStudent({
      content: res.data.content,
      totalElements: res.data.totalElements
    });
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request(
      "GET", `/admin/data/education/view-class-material-logs/${studentLoginId}`,
      successHandler, errorHandlers, null, { params: filterParams }
    );
  }

  const columns = [
    {
      title: "Mã học phần", field: "courseId",
      cellStyle: { maxWidth: '120px' }
    },
    { title: "Tên học phần", field: "courseName" },
    {
      title: "Mã lớp", field: "classCode",
      cellStyle: { maxWidth: '80px' }
    },
    {
      title: "Học kỳ", field: "semester",
      cellStyle: { maxWidth: '80px' }
    },
    { title: "Chương", field: "chapterName" },
    { title: "Tài liệu", field: "materialName" },
    {
      title: "Ngày xem", field: "viewAt",
      render: log => defaultDatetimeFormat(log.viewAt),
      cellStyle: { maxWidth: '160px' }
    }
  ]

  return (
    <MuiThemeProvider>
      <Card>
        <CardContent className={classes.tableWrapper}>
          <StandardTable
            title="Lịch sử xem học liệu"
            columns={columns}
            data={viewMaterialLogsOfStudent.content}
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
            totalCount={viewMaterialLogsOfStudent.totalElements}
            onChangePage={ (page, size) => setFilterParams({...filterParams, page, size}) }
            onSearchChange={ search => setFilterParams({page: 0, size: filterParams.size, search}) }
          />
        </CardContent>
      </Card>
    </MuiThemeProvider>
  );
}