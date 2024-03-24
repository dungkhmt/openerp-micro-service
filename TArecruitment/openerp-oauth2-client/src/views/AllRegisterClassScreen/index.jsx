import { useState, useEffect } from "react";
import { request } from "../../api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { Button } from "@mui/material";
import { useHistory } from "react-router-dom";
import { SEMESTER } from "config/localize";

const AllRegisterClassScreen = () => {
  const history = useHistory();
  const [classes, setClasses] = useState([]);

  useEffect(() => {
    request("get", `/class-call/get-class-by-semester/${SEMESTER}`, (res) => {
      setClasses(res.data);
    }).then();
  }, []);

  const columns = [
    {
      title: "Mã lớp",
      field: "id",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Mã môn học",
      field: "subjectId",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Tên môn học",
      field: "subjectName",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Thời gian",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
      render: (rowData) => (
        <span>
          Thứ {rowData.day}, tiết {rowData.startPeriod} - {rowData.endPeriod}
        </span>
      ),
      customSort: (a, b) => {
        if (a.day === b.day) {
          return a.startPeriod - b.startPeriod;
        }
        return a.day - b.day;
      },
    },
    {
      title: "Hành động",
      render: (rowData) => (
        <Button variant="contained" onClick={() => handleRegister(rowData)}>
          Đăng ký
        </Button>
      ),
    },
  ];

  const handleRegister = (klass) => {
    console.log(klass.id);
    history.push("/student/class-register/", { classId: klass.id });
  };

  return (
    <div>
      <h1>Danh sách lớp học</h1>
      <StandardTable
        title=""
        columns={columns}
        data={classes}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
};

export default AllRegisterClassScreen;
