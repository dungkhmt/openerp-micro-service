import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
  Box,
  Button,
  DialogActions,
  DialogContent,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
} from "@mui/material";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import { pink } from "@mui/material/colors";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { HustModal } from "erp-hust/lib/HustModal/HustModal";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/styles.css";
import readXlsxFile from "read-excel-file";
import { v4 as uuidv4 } from "uuid";
import DownloadButton from "./components/DownloadButton";
import BasicSelect from "./components/SelectBox";
import SelectFileButton from "./components/SelectFileButton";
import TagInput from "./components/TagInput/TagInput";

function ClassManagementScreen() {
  const [classes, setClasses] = useState([]);
  const [semesters, setSemesters] = useState([]);
  const [selectedFile, setSelectedFile] = useState();
  const [currentSemester, setCurrentSemester] = useState(null);
  const [assignedData, setAssignedData] = useState([]);
  const [assignsBySemester, setAssignsBySemester] = useState([]);
  const [roomsByDepartment, setRoomsByDepartment] = useState([]);
  const [departments, setDepartments] = useState([]);

  const [redundantAssignedData, setRedundantAssignedData] = useState([]);
  const [staticAssigned, setStaticAssigned] = useState([]);
  const [fileData, setFileData] = useState([]);
  const [selectedClassIds, setSelectedClassIds] = useState([]);

  const [creationModalVisible, setCreationModalVisible] = useState(false);
  const [updateModalVisible, setUpdateModalVisible] = useState(false);
  const [deletionModalVisible, setDeletionModalVisible] = useState(false);
  const [selectFromFileModalVisible, setSelectFromFileModalVisible] =
    useState(false);
  const [selectFromPlanModalVisible, setSelectFromPlanModalVisible] =
    useState(false);

  const [totalLessons, setTotalLessons] = useState(0);

  const [loading, setLoading] = useState(false);
  const [selectedItem, setSelectedItem] = useState({});
  const [idInput, setIdInput] = useState("");
  const [noteInput, setNoteInput] = useState("");
  const [quantityInput, setQuantityInput] = useState("");
  const [numberOfLessonsInput, setNumberOfLessonsInput] = useState("");
  const [periodInput, setPeriodInput] = useState("");
  const [semesterInput, setSemesterInput] = useState("");
  const [departmentInput, setDepartmentInput] = useState("");
  const [avoidWeekInput, setAvoidWeekInput] = useState([]);
  const [weekConstraintInput, setWeekConstraintInput] = useState(0);

  const [invalidRows, setInvalidRows] = useState({});
  useEffect(() => {
    function fetch_initial_data() {
      request(
        "get",
        "/lab-timetabling/semester/get-all",
        (res) => {
          setSemesters(res.data);
        },
        (err) => {
          console.log(err);
        }
      ).then();
      request("get", "/lab-timetabling/department/get-all", (res) => {
        setDepartments(res.data);
      }).then();
    }
    fetch_initial_data();
  }, []);

  const columns = [
    {
      title: "Mã lớp",
      field: "class_code",
    },
    {
      title: "Số lượng SV",
      field: "quantity",
    },
    {
      title: "Tên lớp",
      field: "note",
    },
    {
      title: "Chỉnh sửa",
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
    },
    {
      title: "Xóa",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            delete_btn_onclick(rowData);
          }}
          variant="contained"
          sx={{ color: pink[500] }}
        >
          <DeleteIcon />
        </IconButton>
      ),
    },
  ];

  const update_callback = (type, msg) => {
    toast(msg, {
      position: toast.POSITION.TOP_RIGHT,
      hideProgressBar: false,
      type: type,
    });
  };

  const edit_btn_onclick = (item) => {
    console.log(item);
    request(
      "get",
      `/lab-timetabling/room/department/${item.department_id}`,
      (res) => {
        setRoomsByDepartment(res.data);
      }
    );

    request(
      "get",
      `/lab-timetabling/assign/semester/${currentSemester}`,
      (res) => {
        setAssignsBySemester(res.data);
      },
      (err) => {
        console.log(err);
      }
    ).then();

    setSelectedItem(item);
    // them truong duration vao assign tu item['period']
    item.assigns.forEach((assign) => {
      assign.duration = item.period;
    });
    setAssignedData(item.assigns);
    setStaticAssigned(item.assigns);
    setRedundantAssignedData([]);

    setTotalLessons(10);

    setDepartmentInput(item.department_id);
    setIdInput(item.class);
    setQuantityInput(item.quantity);
    setPeriodInput(item.period);
    setNumberOfLessonsInput(item.lessons_per_semester);
    setNoteInput(item.note);
    setAvoidWeekInput(item.avoid_week_schedule_constraint || []);
    setWeekConstraintInput(item.week_schedule_constraint || 0);

    setUpdateModalVisible(true);
  };

  const delete_btn_onclick = (item) => {
    setSelectedItem(item);
    setDeletionModalVisible(true);
  };

  const closeModal = () => {
    setRedundantAssignedData([]);
    setUpdateModalVisible(false);
    setCreationModalVisible(false);
    setDeletionModalVisible(false);
    setSelectFromFileModalVisible(false);
    setSelectFromPlanModalVisible(false);
    setSelectedClassIds([]);

    setAssignsBySemester([]);
    setAssignedData([]);
    setInvalidRows({});
  };

  const create_btn_onclick = () => {
    setIdInput("");
    setQuantityInput("");
    setPeriodInput("");
    setNumberOfLessonsInput("");
    setNoteInput("");

    setSemesterInput(currentSemester);
    setRedundantAssignedData([]);

    setCreationModalVisible(true);
  };
  const submit_handler = (http_method, url, data) => {
    setLoading(true);
    request(
      http_method,
      url,
      () => {
        update_callback("success", `${http_method} successful`);
      },
      (err) => {
        update_callback("error", `Failed on ${http_method}`);
        console.log(err);
      },
      data
    ).then(() => {
      closeModal();
      setLoading(false);
    });
  };
  const semester_on_change = (value) => {
    var sem = value.target.value;
    setCurrentSemester(sem);
    request(
      "get",
      `/lab-timetabling/class/semester/${sem}`,
      (res) => {
        setClasses(res.data);
        console.log(res.data);
      },
      (err) => {
        console.log(err);
      }
    ).then();
  };

  const columns_name_defined = ["CourseName", "Period", "Note", "Quantity"];
  const name_field_map = [
    { headerName: "CourseName", field: "course_name" },
    { headerName: "Note", field: "note" },
    { headerName: "Period", field: "period" },
    { headerName: "Quantity", field: "quantity" },
  ];

  const update_file_data = (rows) => {
    if (rows.length < 1) return;
    var columns_name = rows[0];

    // tìm chỉ số cột chunng giữa các cột trong file và cột định nghĩa trước
    var commonIndexes = [];
    for (var i = 0; i < rows[0].length; i++) {
      if (columns_name_defined.includes(columns_name[i])) commonIndexes.push(i);
    }
    console.log(commonIndexes);

    if (commonIndexes.length > 0) {
      var data_from_file = [];
      for (var i = 1; i < rows.length; i++) {
        var object = {};
        console.log(columns_name);
        for (let col_index in commonIndexes) {
          object[name_field_map[col_index].field] =
            rows[i][commonIndexes[col_index]];
        }
        object["id"] = i;
        console.log(object);
        data_from_file.push(object);
      }
      setFileData(data_from_file);
      setSelectFromFileModalVisible(true);
    }
  };
  const file_on_change = (file) => {
    if (file != null) {
      setSelectedFile(file);
      readXlsxFile(file).then((rows) => {
        console.log(rows);
        update_file_data(rows);
      });
    }
  };
  const isDisabled = () => {
    for (let key in invalidRows) {
      if (invalidRows[key]) {
        return true; // Nếu có ít nhất một giá trị true thì return true
      }
    }
    return false; // Nếu không có giá trị true nào thì return false
  };
  const submit_data_from_file = () => {
    var data = fileData
      .filter((_class) => selectedClassIds.includes(_class.id))
      .map((_class) => {
        const { id, ...rest } = _class;
        return { ...rest, semester_id: currentSemester };
      });

    console.log(data);

    request(
      "post",
      "/lab-timetabling/class/batch",
      (res) => {
        update_callback(
          "success",
          `${res.data} success, ${data.length - res.data} failure`
        );
      },
      (err) => {},
      data
    );
  };

  const update_invalid_rows = (assignedData_var, assignsBySemester_var) => {
    var assigned_set_array = [];
    assignedData_var.forEach((assign) => {
      if (assignsBySemester_var.filter((a) => a.id == assign.id).length == 0) {
        assigned_set_array.push(assign);
      }
    });
    assignsBySemester_var.forEach((assign) => {
      assigned_set_array.push(assign);
    });
    var invalid = {};
    assignedData_var.forEach((assign) => {
      invalid[assign.id] = is_invalid(assign, assigned_set_array);
    });
    setInvalidRows(invalid);
  };
  const is_invalid = (object, assigned_set_array) => {
    // not null constraint
    for (let key in object) {
      if (object.hasOwnProperty(key)) {
        if (object[key] == null) return true;
      }
    }
    // room capacity constraint
    const room_assigned = roomsByDepartment.filter(
      (room) => room.id == object.room_id
    )[0];
    if (room_assigned.capacity < object.quantity) return true;
    // time assigned constraint
    for (let i = 0; i < assigned_set_array.length; i++) {
      if (assigned_set_array[i].id == object.id) continue;
      const assign = assigned_set_array[i];
      if (assign.week == object.week) {
        if (assign.room_id == object.room_id) {
          const time_slot = (day, period, start) =>
            (day - 2) * 12 + (period - 1) * 6 + start;
          const first_time_slot = time_slot(
            assign.day_of_week,
            assign.period,
            assign.start_slot
          );
          const second_time_slot = time_slot(
            object.day_of_week,
            object.period,
            object.start_slot
          );
          console.log(first_time_slot, second_time_slot);
          if (
            first_time_slot <= second_time_slot &&
            second_time_slot <= first_time_slot + assign.duration - 1
          )
            return true;
          if (
            second_time_slot <= first_time_slot &&
            first_time_slot <= second_time_slot + object.duration - 1
          )
            return true;
        }
      }
    }
    return false;
  };

  const save_class_onclick = () => {
    // const assigned = assignedData?.map((assign) => {
    //   return {
    //     ...assign,
    //     lesson: { id: assign.class_id },
    //     room: { id: assign.room_id },
    //     semester_id: selectedItem.semester_id,
    //   };
    // });

    // const redundant_assigned = redundantAssignedData.map((id) => {
    //   return {
    //     id: id,
    //   };
    // });
    // console.log(assigned);

    const update_class = {
      class_code: idInput,
      quantity: quantityInput,
      note: noteInput,
      period: periodInput,
      department_id: departmentInput,
      lessons_per_semester: numberOfLessonsInput,
      week_schedule_constraint: weekConstraintInput,
      avoid_week_schedule_constraint: avoidWeekInput
    };

    console.log(update_class);
    submit_handler(
      "patch",
      `/lab-timetabling/class/${selectedItem.id}`,
      update_class
    );
    // submit_handler("post", `/lab-timetabling/assign`, {
    //   update: assigned,
    //   remove: redundant_assigned,
    // });
    // console.log(assigned);
  };
  return (
    <div>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          paddingBottom: "12px",
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
          }}
        >
          <BasicSelect
            items={semesters}
            label={"Học kỳ"}
            value={currentSemester}
            onChange={semester_on_change}
          />
        </div>
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
          }}
        >
          {selectedFile?.name}
          <DownloadButton
            label={"Tải xuống template"}
            fileUrl="/static/xlsx/template.xlsx"
            fileName={selectedFile}
          />
          <div style={{ padding: "0 12px" }}>
            <SelectFileButton
              disabled={currentSemester == null}
              onChange={file_on_change}
            />
          </div>
          <Button
            disabled={currentSemester == null}
            variant="outlined"
            onClick={create_btn_onclick}
          >
            <AddIcon />
            Tạo mới
          </Button>
        </div>
      </div>
      <StandardTable
        title="Danh sách lớp học"
        columns={columns}
        data={classes}
        hideCommandBar={true}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
      <Dialog
        open={updateModalVisible}
        onClose={closeModal}
        fullWidth={true}
        maxWidth={"md"}
      >
        <DialogTitle>Cập nhật lớp học</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <div
              style={{ display: "flex", flexDirection: "column", gap: "1rem" }}
            >
              <div>
                <BasicSelect
                  items={departments}
                  label={"Viện"}
                  value={departmentInput}
                  onChange={(e) => setDepartmentInput(e.target.target)}
                />
              </div>
              <TextField
                label="Tên lớp"
                id="outlined-size-small"
                defaultValue={noteInput}
                onChange={(e) => setNoteInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Mã lớp"
                id="outlined-size-small"
                defaultValue={idInput}
                onChange={(e) => setIdInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Số lượng SV"
                id="outlined-size-small"
                defaultValue={quantityInput}
                onChange={(e) => setQuantityInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Số tiết một buổi"
                id="outlined-size-small"
                defaultValue={periodInput}
                onChange={(e) => setPeriodInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Số buổi một kỳ"
                id="outlined-size-small"
                defaultValue={numberOfLessonsInput}
                onChange={(e) => setNumberOfLessonsInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
            </div>
            Điều kiện xếp lịch
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Điều kiện</TableCell>
                    <TableCell>Tránh tuần</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow>
                    <TableCell>
                      <BasicSelect
                        items={[
                          { id: 1, name: "Tuần lẻ" },
                          { id: 2, name: "Tuần chẵn" },
                        ]}
                        label={"Tuần"}
                        value={weekConstraintInput}
                        onChange={(e) => {
                          setWeekConstraintInput(e.target.value);
                        }}
                      />
                    </TableCell>
                    <TableCell>
                      <TagInput
                        placeholder={"Thêm tuần"}
                        tags={avoidWeekInput || []}
                        onTagsChange={(newTags) => {
                          setAvoidWeekInput(newTags);
                        }}
                      />
                    </TableCell>
                    <TableCell>
                    <Button
                        variant="outlined"
                        color="primary"
                        onClick={() => {
                          setWeekConstraintInput(0)
                          setAvoidWeekInput([])
                        }}
                      >
                        Reset
                      </Button>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>
            {/* <div>
              time table (assigned = {selectedItem.assigns?.length}, require ={" "}
              {totalLessons})
              <table sx={{ width: 1 }}>
                {assignedData?.map((assign, idx) => {
                  var row = assign;
                  return (
                    <Box sx={{ width: 1, mt: 1}}>
                      <FormControl error={invalidRows[assign.id]} variant="standard">
                      <tr id={assign.id}>
                        <td>
                          <BasicSelect
                            items={roomsByDepartment}
                            label={"Phòng"}
                            value={row.room_id}
                            onChange={(item) =>
                              handleInputChange(item, assign.id, "room_id")
                            }
                          />
                        </td>
                        <td>
                          <BasicSelect
                            items={weeks_Of_Semester}
                            label={"Tuần"}
                            value={row.week}
                            onChange={(item) =>
                              handleInputChange(item, assign.id, "week")
                            }
                          />
                        </td>
                        <td>
                          <BasicSelect
                            items={days_Of_Week}
                            label={"Ngày"}
                            value={row.day_of_week}
                            onChange={(item) =>
                              handleInputChange(item, assign.id, "day_of_week")
                            }
                          />
                        </td>
                        <td>
                          <BasicSelect
                            items={periods_Of_Day}
                            label={"Buổi"}
                            value={row.period}
                            onChange={(item) =>
                              handleInputChange(item, assign.id, "period")
                            }
                          />
                        </td>
                        <td>
                          <BasicSelect
                            items={slots_Of_Period}
                            label={"Tiết"}
                            value={row.start_slot}
                            onChange={(item) =>
                              handleInputChange(item, assign.id, "start_slot")
                            }
                          />
                        </td>
                        <td>
                          <Button onClick={(e) => remove_btn_onclick(e)}>
                            remove
                          </Button>
                        </td>
                      </tr>
                      <FormHelperText>
                        {invalidRows[assign.id] ? "Invalid assign input" : ""}
                      </FormHelperText>
                      </FormControl>
                    </Box>
                  );
                })}
              </table>
              <Button
                disabled={assignedData.length < totalLessons ? false : true}
                variant="text"
                size="small"
                onClick={add_row_btn_onclick}
              >
                Add row
              </Button>
            </div> */}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeModal}>Cancel</Button>
          <Button onClick={save_class_onclick} disabled={isDisabled()}>
            Save
          </Button>
        </DialogActions>
      </Dialog>

      <HustModal
        open={creationModalVisible}
        onClose={closeModal}
        // textClose="Update"
        title="Tạo mới lớp học"
        isLoading={loading}
        textOk="Tạo"
        onOk={() => {
          submit_handler("post", `/lab-timetabling/class`, {
            semester_id: semesterInput,
            class_code: idInput,
            quantity: quantityInput,
            period: periodInput,
            note: noteInput,
            department_id: departmentInput,
            lessons_per_semester: numberOfLessonsInput,
          });
        }}
      >
        <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          <div style={{ display: "flex", flexDirection: "row", gap: "1rem" }}>
            <BasicSelect
              items={semesters}
              label={"Semester"}
              value={semesterInput}
              onChange={(value) => setSemesterInput(value.target.value)}
            />
            <BasicSelect
              items={departments}
              label={"Department"}
              value={departmentInput}
              onChange={(e) => setDepartmentInput(e.target.value)}
            />
          </div>
          <TextField
            label="Mã lớp"
            id="outlined-size-small"
            defaultValue={idInput}
            onChange={(e) => setIdInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
          <TextField
            label="Tên lớp"
            id="outlined-size-small"
            defaultValue={noteInput}
            onChange={(e) => setNoteInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
          <TextField
            label="Số lượng SV"
            id="outlined-size-small"
            defaultValue={quantityInput}
            onChange={(e) => setQuantityInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
          <TextField
            label="Số tiết một buổi"
            id="outlined-size-small"
            defaultValue={periodInput}
            onChange={(e) => setPeriodInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
          <TextField
            label="Số buổi một kỳ"
            id="outlined-size-small"
            defaultValue={periodInput}
            onChange={(e) => setNumberOfLessonsInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
        </div>
      </HustModal>

      {/*  */}
      <HustModal
        open={deletionModalVisible}
        onClose={closeModal}
        // textClose="Update"
        title="Delete class"
        isLoading={loading}
        textOk="Delete"
        onOk={() => {
          submit_handler(
            "delete",
            `/lab-timetabling/class/${selectedItem.id}`,
            null
          );
        }}
      ></HustModal>

      <HustModal
        open={selectFromFileModalVisible}
        onClose={() => {
          closeModal();
          setSelectedFile(null);
        }}
        title="Select data from file"
        // isLoading={loading}
        textOk="Upload new data"
        onOk={submit_data_from_file}
      >
        <Box sx={{ height: 400, width: "100%" }}>
          <DataGrid
            rows={fileData}
            columns={name_field_map}
            initialState={{
              pagination: {
                paginationModel: {
                  pageSize: 5,
                },
              },
            }}
            pageSizeOptions={[5]}
            checkboxSelection={true}
            disableRowSelectionOnClick
            onRowSelectionModelChange={(newRowSelectionModel) => {
              setSelectedClassIds(newRowSelectionModel);
            }}
            rowSelectionModel={selectedClassIds}
          />
        </Box>
      </HustModal>
    </div>
  );
}
const inputTextStyle = {
  marginBottom: "1rem",
};
export default ClassManagementScreen;
