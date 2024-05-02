import { Button, Checkbox, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, FormControlLabel, FormGroup, IconButton, Modal } from "@mui/material";
import { Box } from "@mui/system";
import BasicSelect from "./components/SelectBox";
import { useEffect, useState } from "react";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import EditIcon from "@mui/icons-material/Edit";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const AutoAssignScreen = () => {
  const [semesters, setSemesters] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [results, setResults] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedResult, setSelectedResult] = useState({});

  const semester_on_change = (e) => {
    const semesterValue = e.target.value;
    setSelectedSemester(semesterValue);
  };
  const edit_btn_onclick = (item)=>{
    setSelectedResult(item);
    setOpen(true);
  }

  const handleClose = () => {
    setOpen(false);
  };

  const columns = [
    {title: "Created time", field: "created_time"},
    {title: "Học kì", field: "semester.semester"},
    {
      title: "Kết quả",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            edit_btn_onclick(rowData);
          }}
          variant="contained"
          color="success"
        >
          <EditIcon />
        </IconButton>
      ),
    },  ]

  useEffect(() => {
    request("get", "/lab-timetabling/semester/get-all", (res) => {
      setSemesters(res.data);
    }).then();
    request("get", `/lab-timetabling/auto-assign/get-all`, 
    (res) => {
      console.log(res.data);
      setResults(res.data);
    },
    (err) => {
      console.log(err);
    }
    ).then();
  }, []);
  const handleAutoAssign = () => {
    request(
      "post",
      `/lab-timetabling/auto-assign/${selectedSemester}`,
      (res) => {
        toast("Đã tạo request", {
          position: toast.POSITION.TOP_RIGHT,
          hideProgressBar: false,
          type: "success",
        });
      },
      (err) => {
        console.log(err);
        toast("Lỗi khi tạo request", {
          position: toast.POSITION.TOP_RIGHT,
          hideProgressBar: false,
          type: "error",
        });
      }
    ).then();
  };
  return (
    <Box sx={{ width: 1 }}>
      <div style={{ display: "flex", flexDirection: "row" }}>
        <BasicSelect
          items={semesters}
          label={"Học kì"}
          value={selectedSemester}
          onChange={semester_on_change}
        />
      </div>
      <div>
        Điều kiện (tùy chọn) <br />
        <FormGroup>
          <FormControlLabel disabled={(selectedSemester)?false:true} control={<Checkbox defaultChecked />} label="Thời gian giống nhau ở các tuần" />
        </FormGroup>
      </div>
      <Button disabled={(selectedSemester)?false:true} variant="outlined" color="primary" onClick={handleAutoAssign}>
        Xếp lịch tự động
      </Button>
      <div style={{marginTop: '12px'}}>
        <StandardTable
          title="Kết quả xếp lịch tự động"
          columns={columns}
          data={results}
          hideCommandBar={true}
          options={{
            selection: false,
            pageSize: 5,
            search: true,
            sorting: true,
          }}
        >
        </StandardTable>
        <Dialog
          open={open}
          onClose={handleClose}
          maxWidth="md"
          fullWidth={true}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"Kết quả xếp lịch tự động"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            <pre>{selectedResult.result}</pre>
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} autoFocus>
            close
          </Button>
        </DialogActions>
      </Dialog>
      </div>
    </Box>
  );
};

export default AutoAssignScreen;
