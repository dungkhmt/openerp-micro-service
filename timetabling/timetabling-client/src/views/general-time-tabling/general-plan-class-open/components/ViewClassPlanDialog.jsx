import { Button, Dialog, DialogContent, DialogTitle } from "@mui/material";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { request } from "api";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { usePlanGeneralTableCol } from "../hooks/usePlanGeneralTableCol";

const ViewClassPlanDialog = ({
  planClassId,
  semester,
  isOpen,
  closeDialog,
  row,
}) => {
  const [generalClasses, setGeneralClasses] = useState([]);
  console.log(row);

  useEffect(() => {
    if (planClassId) {
      setGeneralClasses([]);
      request(
        "get",
        `/plan-general-classes/view-class?semester=${semester}&planClassId=${planClassId}`,
        (res) => {
          console.log(res.data);
          setGeneralClasses(res.data);
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

  const handleMakeGeneralClass = () => {
    request(
      "post",
      `/plan-general-classes/make-class`,
      (res) => {
        console.log(res.data);
        setGeneralClasses((prevClasses) => {
          return [...prevClasses, res.data];
        });
        toast.success("Thêm lớp thành công!");
      },
      (error) => {
        if (error.response.status == 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Thêm lớp thất bại");
        }
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
          <Button onClick={handleMakeGeneralClass} variant="contained">
            Thêm lớp
          </Button>
        </div>
        <DataGrid
          initialState={{
            filter: {
              filterModel: {
                items: [],
                quickFilterValues: [""],
              },
            },
          }}
          slots={{ toolbar: GridToolbar }}
          slotProps={{
            toolbar: {
              printOptions: { disableToolbarButton: true },
              csvOptions: { disableToolbarButton: true },
              showQuickFilter: true,
            },
          }}
          disableColumnSelector
          disableDensitySelector
          sx={{ height: 550 }}
          rowSelection={true}
          columns={usePlanGeneralTableCol(setGeneralClasses)}
          rows={generalClasses}
        />
      </DialogContent>
    </Dialog>
  );
};

export default ViewClassPlanDialog;
