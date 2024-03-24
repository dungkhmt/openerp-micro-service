import { Dialog, DialogContent, DialogTitle, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";

const ApplicatorDialog = ({ open, handleClose, classId }) => {
  const [applicators, setApplicators] = useState([]);

  useEffect(() => {
    request(
      "get",
      `/application/get-application-by-class/${classId}`,
      (res) => {
        setApplicators(res.data);
      }
    );
  }, [classId]);

  const columns = [
    {
      title: "Mã số sinh viên",
      field: "mssv",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Tên sinh viên",
      field: "name",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Số điện thoại",
      field: "phoneNumber",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Email",
      field: "email",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
  ];

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
        {/**
         * @TODO Change the table library, this shit is shit
         */}
        <DialogContent>
          <StandardTable
            title=""
            columns={columns}
            data={applicators}
            hideCommandBar
            options={{
              selection: false,
              pageSize: 5,
              search: false,
              sorting: true,
            }}
          />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ApplicatorDialog;
