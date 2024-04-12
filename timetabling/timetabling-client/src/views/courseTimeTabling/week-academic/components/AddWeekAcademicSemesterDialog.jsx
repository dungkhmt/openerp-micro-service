import React, { useState } from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import GeneralSemesterAutoComplete from "views/general-time-tabling/common-components/GeneralSemesterAutoComplete";
import { request } from "api";
import { toast } from "react-toastify";

const AddWeekAcademicSemesterDialog = ({ open, setOpen, setWeeks, setUpdateSelectedSemester }) => {
  const [numberOfWeeks, setNumOfWeeks] = useState("");
  const [startDate, setStartDate] = useState(new Date());
  const handleClose = () => {
    setOpen(false);
  };
  const [isLoading, setLoading] = useState(false);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const handleSubmit = () => {
    setLoading(true);
    console.log(
      formatDateToString(startDate),
      numberOfWeeks,
      selectedSemester.semester
    );
    request(
      "post",
      "/academic-weeks/",
      (res) => {
        setLoading(false);
        console.log(res.data);
        setWeeks(res.data);
        setUpdateSelectedSemester(selectedSemester);
        handleClose();
        toast.info("Tạo danh sách tuần thành công!");
      },
      (error) => {
        setLoading(false);
        console.log(error);
        toast.error("Lỗi, tạo danh sách tuần thất bại!");
      },
      {
        semester: selectedSemester.semester,
        startDate: formatDateToString(startDate),
        numberOfWeeks: Number(numberOfWeeks),
      }
    );
  };

  const formatDateToString = (date) => {
    const day = date.getDate();
    const month = date.getMonth() + 1;
    const year = date.getFullYear();

    // Pad single digits with leading zero
    const formattedDay = String(day).padStart(2, "0");
    const formattedMonth = String(month).padStart(2, "0");

    return `${formattedDay}/${formattedMonth}/${year}`;
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Tạo danh sách tuần</DialogTitle>
      <DialogContent dividers={true}>
        <div className="flex gap-2 flex-col py-2">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <TextField
            value={numberOfWeeks}
            onChange={(e) => {
              setNumOfWeeks(e.target.value);
            }}
            label={"Nhập số tuần"}
          />
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DatePicker
              renderInput={(props) => (
                <TextField {...props} value={startDate} />
              )}
              label="Chọn ngày bắt đầu kỳ học"
              value={startDate}
              onChange={(newValue) => {
                console.log(new Date(newValue)?.getDate());
                setStartDate(new Date(newValue));
              }}
            />
          </LocalizationProvider>
        </div>
      </DialogContent>
      <DialogActions>
        {isLoading ? <FacebookCircularProgress /> : null}
        <Button
          variant="outlined"
          disabled={
            isLoading ||
            selectedSemester === null ||
            Number.isInteger(numberOfWeeks)
          }
          onClick={handleSubmit}
          type="submit"
        >
          Xác nhận
        </Button>
        <Button variant="outlined" onClick={handleClose}>
          Hủy
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddWeekAcademicSemesterDialog;
