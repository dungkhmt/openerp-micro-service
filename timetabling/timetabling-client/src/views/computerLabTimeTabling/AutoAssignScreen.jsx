import { FileDownload } from "@mui/icons-material";
import {
  Button,
  Checkbox,
  FormControlLabel,
  FormGroup,
  IconButton,
  Select,
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
import { submission_status, weeks_Of_Semester } from "utils/formatter";
import BasicSelect from "./components/SelectBox";
import { download_result_file } from "./service/extractAutoSchedulingResult";
const AutoAssignScreen = () => {
  const [semesters, setSemesters] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [results, setResults] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedSubmission, setSelectedSubmission] = useState({});

  const [consistentWeeklyConstraint, setConsistentWeeklyConstraint] =
    useState(null);
  const [oddWeekSchedulingConstraint, setOddWeekSchedulingConstraint] =
    useState(null);
  const [evenWeekSchedulingConstraint, setEvenWeekSchedulingConstraint] =
    useState(null);

  const [classes, setClasses] = useState([]);
  const [weekConstraintMap, setWeekConstraintMap] = useState({});
  const [avoidWeekMap, setAvoidWeekMap] = useState({});

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
        download_result_file(item, res.data);
      }
    ).then();
  };

  const handleClose = () => {
    setOpen(false);
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
    const filteredWeekConstraintMap = Object.fromEntries(
      Object.entries(weekConstraintMap).filter(([key, value]) => value !== 0)
  );
    const filteredAvoidWeekMap = Object.fromEntries(
      Object.entries(avoidWeekMap).filter(([key, value]) => value !== 0)
  );
    console.log(filteredWeekConstraintMap);
    console.log(filteredAvoidWeekMap);
    var url = `/lab-timetabling/auto-assign/${selectedSemester}`;
    request(
      "post",
      url,
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
      },
      {
        weekConstraintMap: filteredWeekConstraintMap,
        avoidWeekMap: filteredAvoidWeekMap,
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
        {/* Điều kiện (tùy chọn) <br />
        <FormGroup>
          <FormControlLabel
            disabled={selectedSemester ? false : true}
            control={
              <Checkbox
                checked={oddWeekSchedulingConstraint}
                onChange={(e) => {
                  setOddWeekSchedulingConstraint(e.target.checked);
                  console.log(e.target.checked);
                }}
              />
            }
            label="Xếp lịch vào các tuần lẻ"
          />
          <FormControlLabel
            disabled={selectedSemester ? false : true}
            control={
              <Checkbox
                checked={evenWeekSchedulingConstraint}
                onChange={(e) => {
                  setEvenWeekSchedulingConstraint(e.target.checked);
                  console.log(e.target.checked);
                }}
              />
            }
            label="Xếp lịch vào các tuần chẵn"
          />
        </FormGroup> */}
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
                      <BasicSelect
                        items={weeks_Of_Semester}
                        label={"Tuần"}
                        value={avoidWeekMap[item.id]}
                        onChange={(e) => {
                          setAvoidWeekMap({
                            ...avoidWeekMap,
                            [item.id]: e.target.value,
                          });
                        }}
                      />
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
                            [item.id]: 0,
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
      </div>
      <Button
        disabled={selectedSemester ? false : true}
        variant="outlined"
        color="primary"
        onClick={handleAutoAssign}
      >
        Xếp lịch tự động
      </Button>
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
    </Box>
  );
};

export default AutoAssignScreen;
