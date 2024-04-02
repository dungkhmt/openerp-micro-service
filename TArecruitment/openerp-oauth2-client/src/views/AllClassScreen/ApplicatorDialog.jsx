import { Dialog, DialogContent, DialogTitle, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { request } from "api";
import { DataGrid } from "@mui/x-data-grid";

const ApplicatorDialog = ({ open, handleClose, classId }) => {
  const [applicators, setApplicators] = useState([]);

  useEffect(() => {
    request(
      "get",
      `/application/get-application-by-class/${classId}`,
      (res) => {
        setApplicators(res.data);
        console.log(res.data);
      }
    );
  }, [classId]);

  const dataGridColumns = [
    {
      field: "mssv",
      headerName: "MSSV",
      align: "center",
      headerAlign: "center",
    },
    {
      field: "name",
      headerName: "Tên sinh viên",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
    {
      field: "phoneNumber",
      headerName: "Số điện thoại",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
    {
      field: "email",
      headerName: "Email",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
  ];

  const dataGridRows = applicators.map((applicator, index) => ({
    id: index,
    mssv: applicator.mssv,
    name: applicator.name,
    phoneNumber: applicator.phoneNumber,
    email: applicator.email,
  }));

  return (
    <div>
      <Dialog
        open={open}
        onClose={handleClose}
        fullWidth
        maxWidth="lg"
        sx={{
          "& .MuiDialog-paper": {
            height: 550,
          },
        }}
      >
        <DialogTitle sx={{ textAlign: "center", marginTop: "1%" }}>
          <Typography variant="h5" fontWeight="bold">
            Danh sách sinh viên đăng ký mã lớp {classId}
          </Typography>
        </DialogTitle>
        <DialogContent>
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
            pageSizeOptions={[5]}
            checkboxSelection={false}
            disableRowSelectionOnClick
          />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ApplicatorDialog;
