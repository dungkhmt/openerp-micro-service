import { FileDownload } from "@mui/icons-material";
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import { Box } from "@mui/system";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { submission_status } from "utils/formatter";
import BasicSelect from "./components/SelectBox";
import {
  download_result_file,
  produce_result_data,
} from "./service/extractAutoSchedulingResult";
import FindInPageIcon from "@mui/icons-material/FindInPage";
import {time_limit_input} from 'utils/formatter'

const AutoAssignScreen = () => {
  const [semesters, setSemesters] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [results, setResults] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedSubmission, setSelectedSubmission] = useState({});
  const [selectedResult, setSelectedResult] = useState([]);
  const [timeLimitInput, setTimeLimitInput] = useState(null);

  const [classes, setClasses] = useState([]);
  const [weekConstraintMap, setWeekConstraintMap] = useState({});
  const [avoidWeekMap, setAvoidWeekMap] = useState({});
  const [tags, setTags] = useState([]);
  const [viewResultModal, setViewResultModal] = useState(false);
  const [resultProgressLoading, setResultProgressLoading] = useState(true);
  const [autoSchedulingInProgress, setAutoSchedulingInProgress] = useState(false);

  const semester_on_change = (e) => {
    const semesterValue = e.target.value;
    request(
      "get",
      `/lab-timetabling/class/semester/${semesterValue}`,
      (res) => {
        setClasses(res.data);
      }
    ).then();
    setSelectedSemester(semesterValue);
  };
  const result_file_download = (item) => {
    const submission_id = item.id;
    setSelectedSubmission(item);
    request(
      "get",
      `/lab-timetabling/auto-assign/result/${submission_id}`,
      (res) => {
        produce_result_data(item, res.data).then((res) => download_result_file(res));
      }
    ).then();
  };

  const view_result = (item) => {
    setResultProgressLoading(true);
    const submission_id = item.id;
    setSelectedSubmission(item);
    request(
      "get",
      `/lab-timetabling/auto-assign/result/${submission_id}`,
      (res) => {
        produce_result_data(item, res.data).then((res) => {
          setResultProgressLoading(false);
          setSelectedResult(res.slice(2));
        });
      }
    ).then();
    setViewResultModal(true);
  };

  const handleClose = () => {
    setSelectedResult([]);
    setViewResultModal(false);
    setSelectedSubmission({});
  };

  const columns = [
    {
      title: "Created time",
      field: "created_time",
      render: (data) => {
        const date = new Date(data.created_time);
        return date.toLocaleString();
      },
    },
    { title: "Học kì", field: "semester.semester" },
    {
      title: "Trạng thái",
      field: "status",
      render: (data) => {
        const c = submission_status.find((item) => item.id === data.status);
        return c?.name;
      },
    },
    {
      title: "Kết quả",
      sorting: false,
      render: (rowData) => (
        <IconButton
          disabled={rowData.status != 1}
          onClick={() => {
            view_result(rowData);
          }}
          variant="contained"
          color="success"
        >
          <FindInPageIcon />
        </IconButton>
      ),
    },
    {
      title: "Kết quả",
      sorting: false,
      render: (rowData) => (
        <IconButton
          disabled={rowData.status != 1}
          onClick={() => {
            result_file_download(rowData);
          }}
          variant="contained"
          color="success"
        >
          <FileDownload />
        </IconButton>
      ),
    },
  ];

  useEffect(() => {
    request("get", "/lab-timetabling/semester/get-all", (res) => {
      setSemesters(res.data);
    }).then();
    request(
      "get",
      `/lab-timetabling/auto-assign/get-all`,
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
    toast("Đã tạo request", {
      position: toast.POSITION.TOP_RIGHT,
      hideProgressBar: false,
      type: "success",
      autoClose: false
    });
    setAutoSchedulingInProgress(true);
    const filteredWeekConstraintMap = Object.fromEntries(
      Object.entries(weekConstraintMap).filter(([key, value]) => value !== 0)
    );
    const filteredAvoidWeekMap = Object.fromEntries(
      Object.entries(avoidWeekMap).filter(([key, value]) => value.length !== 0)
    );
    var url = `/lab-timetabling/auto-assign/${selectedSemester}`;
    request(
      "post",
      url,
      (res) => {
        toast(res.data.msg, {
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
      },
      {
        weekConstraintMap: filteredWeekConstraintMap,
        avoidWeekMap: filteredAvoidWeekMap,
        solvingTimeLimit: timeLimitInput
      }
    ).then(e=>setAutoSchedulingInProgress(false));
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
      {/* <div>
        {(classes.length == 0)?'Không có dữ liệu':
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Lớp</TableCell>
                <TableCell>Điều kiện</TableCell>
                <TableCell>Tránh tuần</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {classes.map((item) => {
                return (
                  <TableRow key={item.id}>
                    <TableCell>{item.note}</TableCell>
                    <TableCell>
                      <BasicSelect
                        items={[
                          { id: 1, name: "Tuần lẻ" },
                          { id: 2, name: "Tuần chẵn" },
                        ]}
                        label={"Tuần"}
                        value={weekConstraintMap[item.id]}
                        onChange={(e) => {
                          setWeekConstraintMap({
                            ...weekConstraintMap,
                            [item.id]: e.target.value,
                          });
                        }}
                      />
                    </TableCell>
                    <TableCell>
                      <TagInput placeholder={"Thêm tuần"} tags={avoidWeekMap[item.id] || []} onTagsChange={(newTags)=>{
                        setAvoidWeekMap({
                          ...avoidWeekMap,
                          [item.id]: newTags
                        });
                      }}/>
                    </TableCell>
                    <TableCell>
                      <Button
                        variant="outlined"
                        color="primary"
                        onClick={() => {
                          setWeekConstraintMap({
                            ...weekConstraintMap,
                            [item.id]: 0,
                          });
                          setAvoidWeekMap({
                            ...avoidWeekMap,
                            [item.id]: [],
                          });
                        }}
                      >
                        Reset
                      </Button>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>}
      </div> */}
      <div style={{display: 'flex', flexDirection: 'row', marginTop: "12px" }}>
      <Button
        disabled={(selectedSemester ? false : true) && !autoSchedulingInProgress}
        variant="outlined"
        color="primary"
        onClick={handleAutoAssign}
        >
        Xếp lịch tự động
      </Button>
      <BasicSelect
          disabled={selectedSemester ? false : true}
          items={time_limit_input}
          label={"Thời gian giới hạn"}
          value={timeLimitInput}
          onChange={e=>{
            setTimeLimitInput(e.target.value)
          }}
        />
      {(autoSchedulingInProgress)?<CircularProgress/>:null}
      </div>
      <div style={{ marginTop: "12px" }}>
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
        ></StandardTable>
      </div>
      <Dialog
        fullWidth={true}
        maxWidth={"lg"}
        open={viewResultModal}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {`Chi tiết kết quả xếp lịch tự động kì ${selectedSubmission?.semester?.semester}`}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {resultProgressLoading ? <LinearProgress /> : null}
            <TableContainer>
              <Table sx={{ minWidth: 650 }} aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell>STT</TableCell>
                    <TableCell>Tên lớp</TableCell>
                    <TableCell>Tuần</TableCell>
                    <TableCell>Thứ</TableCell>
                    <TableCell>Buổi</TableCell>
                    <TableCell>Tiết</TableCell>
                    <TableCell>Phòng</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {selectedResult?.map((row, i) => (
                    <TableRow
                      key={i}
                      sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                    >
                      {row.map((cell, j) => (
                        <TableCell key={j}>{cell.value}</TableCell>
                      ))}
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Đóng</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AutoAssignScreen;
