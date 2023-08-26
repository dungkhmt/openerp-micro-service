import React, {useEffect, useState} from 'react';
import {request} from "../../../api";
import {errorNoti} from "../../../utils/notification";
import StandardTable from "../../../component/table/StandardTable";
import {Card, CardContent} from "@material-ui/core";
import {useHistory} from "react-router-dom";

export default function StudentList(props) {
  const history = useHistory();
  const [studentsOfCurrentPage, setStudentsOfCurrentPage] = useState({ content: [], totalElements: 0 });
  const [filterParams, setFilterParams] = useState({ search: '', page: 0, size: 20 });

  useEffect(getStudentsOfCurrentPage, [filterParams]);

  function getStudentsOfCurrentPage() {
    const params = {
      ...filterParams
    }
    let successHandler = res => setStudentsOfCurrentPage({
      content: res.data.content,
      totalElements: res.data.totalElements
    });
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", "/statistics/users", successHandler, errorHandlers,  null, { params })
  }

  function navigateToLeaningProfilesPageOfStudent(studentLoginId) {
    history.push(`/admin/data/view-learning-profiles/users/${studentLoginId}`)
  }

  const columns = [
    { title: "User ID", field: "userLoginId" },
    { title: "First Name", field: "firstName" },
    { title: "Last Name", field: "lastName" },
    { title: "Email", field: "email" }
  ]

  return (
    <Card>
      <CardContent>
        <StandardTable
          title="Danh sách sinh viên"
          columns={columns}
          data={studentsOfCurrentPage.content}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
            pageSize: filterParams.size,
            searchText: filterParams.search,
            debounceInterval: 800
          }}
          page={filterParams.page}
          totalCount={studentsOfCurrentPage.totalElements}
          onChangePage={ (page, size) => setFilterParams({...filterParams, page, size}) }
          onSearchChange={ search => setFilterParams({page: 0, size: filterParams.size, search}) }
          onRowClick={ (event, student) => navigateToLeaningProfilesPageOfStudent(student.userLoginId) }
        />
      </CardContent>
    </Card>
  );
}