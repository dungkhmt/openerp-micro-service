import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { request } from "api";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { usePlanGeneralTableCol } from "../hooks/usePlanGeneralTableCol";
import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from "@mui/material";

const ViewClassPlanDialog = ({
  planClassId,
  semester,
  isOpen,
  closeDialog,
  row,
}) => {
  const [generalClasses, setGeneralClasses] = useState([]);
  const [openInputDialog, setOpenInputDialog] = useState(false);
  const [inputData, setInputData] = useState({
    numberOfClasses: "",
    classType: "LT",
  });

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

  const handleOpenInputDialog = () => {
    setOpenInputDialog(true);
  };

  const handleCloseInputDialog = () => {
    setOpenInputDialog(false);
    setInputData({
      numberOfClasses: "",
      classType: "LT",
    });
  };

  const handleMakeGeneralClass = () => {
    request(
      "post",
      `/plan-general-classes/make-multiple-classes`,
      (res) => {
        console.log(res.data);
        setGeneralClasses((prevClasses) => {
          return [...prevClasses, ...res.data];
        });
        toast.success("Thêm lớp thành công!");
        handleCloseInputDialog();
      },
      (error) => {
        if (error.response.status == 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Thêm lớp thất bại");
        }
      },
      {
        classRequest: {
          id: row.id,
          quantityMax: row.quantityMax,
          exerciseMaxQuantity: row.exerciseMaxQuantity,
          lectureExerciseMaxQuantity: row.lectureExerciseMaxQuantity,
          lectureMaxQuantity: row.lectureMaxQuantity,
          classType: row.classType,
          nbClasses: row.nbClasses,
          mass: row.mass,
          programName: row.programName,
          moduleCode: row.moduleCode,
          moduleName: row.moduleName,
          semester: semester,
          learningWeeks: row.learningWeeks,
          crew: row.crew,
          weekType: row.weekType,
          duration: row.duration
        },
        quantity: parseInt(inputData.numberOfClasses),
        classType: inputData.classType
      },
      null,
      null
    );
  };

  return (
    <>
      <Dialog maxWidth="lg" open={isOpen} onClose={() => closeDialog()}>
        <DialogTitle>Các lớp học của mã lớp: {planClassId}</DialogTitle>
        <DialogContent>
          <div className="flex p-2 justify-end gap-4">
            <Button onClick={handleOpenInputDialog} variant="contained">
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

      <Dialog open={openInputDialog} onClose={handleCloseInputDialog}>
        <DialogTitle>Thêm lớp mới</DialogTitle>
        <DialogContent>
          <div className="flex flex-col gap-4 p-4">
            <TextField
              label="Số lượng lớp"
              type="number"
              value={inputData.numberOfClasses}
              onChange={(e) =>
                setInputData({ ...inputData, numberOfClasses: e.target.value })
              }
              fullWidth
            />
            <FormControl fullWidth>
              <InputLabel>Loại lớp</InputLabel>
              <Select
                value={inputData.classType}
                onChange={(e) =>
                  setInputData({ ...inputData, classType: e.target.value })
                }
                label="Loại lớp"
              >
                <MenuItem value="LT">LT</MenuItem>
                <MenuItem value="BT">BT</MenuItem>
                <MenuItem value="LT+BT">LT + BT</MenuItem>
              </Select>
            </FormControl>
            <Button
              onClick={handleMakeGeneralClass}
              variant="contained"
              color="primary"
            >
              Xác nhận
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default ViewClassPlanDialog;
