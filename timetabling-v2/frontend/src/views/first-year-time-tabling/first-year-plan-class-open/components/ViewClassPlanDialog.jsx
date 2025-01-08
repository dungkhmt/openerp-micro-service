import { request } from "api";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { usePlanFirstYearTableCol } from "../hooks/usePlanFirstYearTableCol";
import { Button, Dialog, DialogContent, DialogTitle } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";

const ViewClassPlanDialog = ({
  planClassId,
  semester,
  isOpen,
  closeDialog,
  row,
}) => {
  const [firstYearClasses, setFirstYearClasses] = useState([]);
  console.log(row);

  useEffect(() => {
    if (planClassId) {
      setFirstYearClasses([]);
      request(
        "get",
        `/plan-general-classes/view-class?semester=${semester}&planClassId=${planClassId}`,
        (res) => {
          console.log(res.data);
          setFirstYearClasses(res.data);
        },
        (err) => {
          toast.error("Có lỗi khi truy vấn các lớp học!");
        },
        null,
        null,
        null
      );
    }
  }, [planClassId]);

  const handleMakeFirstYearClass = () => {
    request(
      "post",
      `/plan-general-classes/make-class`,
      (res) => {
        console.log(res.data);
        setFirstYearClasses((prevClasses) => {
          return [...prevClasses, res.data];
        });
        toast.success("Thêm lớp thành công!");
      },
      (err) => {
        toast.error("Thêm lớp thất bại");
      },
      {
        planClassId: planClassId,
        ...row,
      },
      null,
      null
    );
  };

  return (
    <Dialog maxWidth="lg" open={isOpen} onClose={() => closeDialog()}>
      <DialogTitle>Các lớp học của mã lớp: {planClassId}</DialogTitle>
      <DialogContent>
        <div className="flex p-2 justify-end gap-4">
          <Button onClick={handleMakeFirstYearClass} variant="contained">
            Thêm lớp
          </Button>
        </div>
        <DataGrid
          sx={{ height: 550 }}
          rowSelection={true}
          columns={usePlanFirstYearTableCol(setFirstYearClasses)}
          rows={firstYearClasses}
        />
      </DialogContent>
    </Dialog>
  );
};

export default ViewClassPlanDialog;
