import { useState, useEffect } from "react";
import { request } from "../../api";
import { Button } from "@mui/material";
import { useHistory } from "react-router-dom";
import { SEMESTER } from "config/localize";
import { DataGrid } from "@mui/x-data-grid";

const AllRegisterClassScreen = () => {
  const history = useHistory();
  const [classes, setClasses] = useState([]);
  const [registeredClass, setRegisteredClass] = useState([]);

  useEffect(() => {
    request("get", `/class-call/get-class-by-semester/${SEMESTER}`, (res) => {
      setClasses(res.data);
    }).then();
    request("get", `/class-call/get-my-registered-class/${SEMESTER}`, (res) => {
      setRegisteredClass(res.data);
    });
  }, []);

  const handleRegister = (klass) => {
    history.push("/student/class-register/", { classId: klass.id });
  };

  const actionCell = (params) => {
    const rowData = params.row;
    const isRegistered = registeredClass.some((item) => item.id === rowData.id);
    return (
      <Button
        variant="contained"
        disabled={isRegistered}
        onClick={() => handleRegister(rowData)}
        style={{ width: "130px" }}
      >
        {isRegistered ? "ĐÃ ĐĂNG KÝ" : "Đăng ký"}
      </Button>
    );
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã lớp",
      align: "center",
      headerAlign: "center",
    },
    { field: "subjectId", headerName: "Mã môn học", flex: 1 },
    {
      field: "subjectName",
      headerName: "Tên môn học",
      flex: 1,
    },
    {
      field: "day",
      headerName: "Thời gian",
      flex: 1,
    },
    {
      headerName: "Hành động",
      flex: 1,
      align: "center",
      headerAlign: "center",
      renderCell: actionCell,
    },
  ];

  const dataGridRows = classes.map((klass) => ({
    id: klass.id,
    subjectId: klass.subjectId,
    subjectName: klass.subjectName,
    day: `Thứ ${klass.day}, tiết ${klass.startPeriod} - ${klass.endPeriod}`,
    actions: { rowData: klass },
  }));

  return (
    <div>
      <h1>Danh sách lớp học</h1>
      <DataGrid
        rowHeight={60}
        sx={{ fontSize: 16 }}
        rows={dataGridRows}
        columns={dataGridColumns}
        autoHeight
        initialState={{
          pagination: {
            paginationModel: {
              pageSize: 5,
            },
          },
        }}
        pageSizeOptions={[5, 10, 20]}
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </div>
  );
};

export default AllRegisterClassScreen;
