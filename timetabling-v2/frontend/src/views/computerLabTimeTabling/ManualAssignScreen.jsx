import EditCalendarIcon from "@mui/icons-material/EditCalendar";
import {
  Box,
  Button,
  DialogActions,
  DialogContent,
  FormControl,
  FormHelperText,
  IconButton,
} from "@mui/material";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/styles.css";
import readXlsxFile from "read-excel-file";
import writeXlsxFile from "write-excel-file";
import FileDownloadIcon from "@mui/icons-material/FileDownload";
import {
  days_Of_Week,
  periods_Of_Day,
  slots_Of_Period,
  weeks_Of_Semester,
  datetimeForFN,
} from "utils/formatter";
import { v4 as uuidv4 } from "uuid";
import BasicSelect from "./components/SelectBox";

function ManualAssignScreen() {
  const [classes, setClasses] = useState([]);
  const [semesters, setSemesters] = useState([]);
  const [selectedFile, setSelectedFile] = useState();
  const [currentSemester, setCurrentSemester] = useState(null);
  const [assignedData, setAssignedData] = useState([]);
  const [assignsBySemester, setAssignsBySemester] = useState([]);
  const [roomsByDepartment, setRoomsByDepartment] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [conflicts, setConflicts] = useState(null);

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
      title: "Xếp lịch",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            edit_btn_onclick(rowData);
          }}
          variant="contained"
          color="success"
        >
          <EditCalendarIcon />
        </IconButton>
      ),
    },
  ];
  const download_viz_schedule_onclick = (assigns) => {
    function createFixed2DArray(rows, columns) {
      const arr = [];
      for (let i = 0; i < rows; i++) {
        const row = [];
        for (let j = 0; j < columns; j++) {
          row.push(null); // Giá trị ban đầu của mỗi phần tử có thể là gì tùy thuộc vào yêu cầu của bạn
        }
        arr.push(row);
      }
      return arr;
    }
    function generateRandomColorMap(idArray) {
      const colorMap = {};
      const randomColor = () => {
        let color = Math.floor(Math.random() * 16777215).toString(16);
        /* sometimes the returned value does not have
         * the 6 digits needed, so we do it again until
         * it does
         */
        while (color.length < 6) {
          color = Math.floor(Math.random() * 16777215).toString(16);
        }
        let red = parseInt(color.substring(0, 2), 16);
        let green = parseInt(color.substring(2, 4), 16);
        let blue = parseInt(color.substring(4, 6), 16);
        let brightness = red * 0.299 + green * 0.587 + blue * 0.114;
        /* if (red*0.299 + green*0.587 + blue*0.114) > 180
         * use #000000 else use #ffffff
         */
        if (brightness > 180) {
          return {
            backgroundColor: "#" + color,
            textColor: "#000000",
          };
        } else
          return {
            backgroundColor: "#" + color,
            textColor: "#ffffff",
          };
      };
      idArray.forEach((id) => {
        colorMap[id] = randomColor();
      });
      return colorMap;
    }

    const idArray = assigns.map((assign) => assign.class_id);
    const randomRGBMap = generateRandomColorMap(idArray);
    // get unique rooms
    let uniqueRooms = [];
    let roomsSet = new Set();
    for (let obj of assigns) {
      console.log(obj);
      roomsSet.add(obj.room_id);
    }
    uniqueRooms = Array.from(roomsSet);
    var data = createFixed2DArray(20 * 7 + 10, 12 * uniqueRooms.length);
    // fill in the first row
    for (var room of uniqueRooms) {
      data[0][uniqueRooms.indexOf(room) * 12 + 2] = {
        value: assigns.filter((assign) => assign.room_id == room)[0].room,
        span: 12,
        align: "center",
        alignVertical: "center",
        borderStyle: "thick",
        borderColor: "#000000",
      };
      for (var i = 1; i <= 12; i++) {
        data[1][uniqueRooms.indexOf(room) * 12 + i + 1] = {
          value: "Tiết " + i,
          align: "center",
          alignVertical: "center",
          borderStyle: "thick",
          borderColor: "#000000",
        };
      }
    }

    for (var i = 1; i <= 140 - 7; i += 7) {
      data[i + 1][0] = {
        // +1 because of the first row is for room
        value: "Tuần " + (Math.floor((i - 1) / 7) + 1),
        rowSpan: 7,
        borderStyle: "thick",
        borderColor: "#000000",
        align: "center",
        alignVertical: "center",
      };
    }
    for (var i = 1; i <= 140; i++) {
      data[i + 1][1] = {
        // +1 because of the first row is for room
        value: (i % 7) + 1 == 1 ? "CN" : "T" + ((i % 7) + 1),
        borderStyle: "thick",
        borderColor: "#000000",
        align: "center",
        alignVertical: "center",
      };
    }
    for (var assign of assigns) {
      const start =
        (assign.day_of_week - 2) * 12 +
        (assign.period - 1) * 6 +
        assign.start_slot;
      let day = (assign.week - 1) * 7 + Math.floor(start / 12) + 1;
      // +1 because of the first row is for room
      const x = day + 1;
      // +2 because of the first 2 columns are for week and day
      const y = (start % 12) - 1 + 2 + uniqueRooms.indexOf(assign.room_id) * 12;
      var conflict_count = 0;
      for (var i = 0; i < assign.duration; i++) {
        if (data[x][y + i] != null) {
          conflict_count++;
        }
      }
      data[x][y] = {
        value: assign.lesson,
        span: assign.duration - conflict_count,
        backgroundColor: randomRGBMap[assign.class_id]["backgroundColor"],
        color: randomRGBMap[assign.class_id]["textColor"],
        width: 20,
        height: 20,
        fontSize: 12,
        fontWeight: "bold",
        align: "center",
        alignVertical: "center",
      };
    }
    if (conflicts?.length > 0) {
      const sortedData = conflicts.sort((a, b) => {
        return a.assignResponse.duration - b.assignResponse.duration;
      });
      for (var conflict of sortedData) {
        if (conflict.conflictType === "CAP_EXCEPT_CONFLICT") continue;
        const start =
          (assign.day_of_week - 2) * 12 +
          (assign.period - 1) * 6 +
          assign.start_slot;
        const assign = conflict.assignResponse;
        let day = (assign.week - 1) * 7 + Math.floor(start / 12) + 1;
        // +1 because of the first row is for room
        const x = day + 1;
        // +2 because of the first 2 columns are for week and day
        const y =
          (start % 12) - 1 + 2 + uniqueRooms.indexOf(assign.room_id) * 12;
        for (var i = 0; i < assign.duration; i++) {
          data[x][y + i] = null;
        }
        data[x][y] = {
          value: " ",
          span: assign.duration,
          backgroundColor: "#000000",
          width: 20,
          height: 20,
        };
      }
    }

    writeXlsxFile(data, {
      fileName: `visualize_schedule_${datetimeForFN(new Date())}.xlsx`,
    });
  };
  const update_callback = (type, msg) => {
    toast(msg, {
      position: toast.POSITION.TOP_RIGHT,
      hideProgressBar: false,
      type: type,
    });
  };

  const edit_btn_onclick = (item) => {
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

    setTotalLessons(item.lessons_per_semester);

    setDepartmentInput(item.department_id);
    setIdInput(item.class);
    setQuantityInput(item.quantity);
    setPeriodInput(item.period);
    setNumberOfLessonsInput(item.number_of_lessons);
    setNoteInput(item.note);

    setUpdateModalVisible(true);
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

  const submit_handler = (http_method, url, data) => {
    console.log(data);
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
      },
      (err) => {
        console.log(err);
      }
    ).then();

    request(
      "get",
      `/lab-timetabling/assign/semester/${sem}`,
      (res) => {
        setAssignsBySemester(res.data);
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
  const isDisabled = () => {
    for (let key in invalidRows) {
      if (invalidRows[key]) {
        return true; // Nếu có ít nhất một giá trị true thì return true
      }
    }
    return false; // Nếu không có giá trị true nào thì return false
  };

  const add_row_btn_onclick = () => {
    var updatedData = [...assignedData];
    if (assignedData.length < totalLessons) {
      const id = uuidv4();
      const newRow = {
        id: id,
        class_id: selectedItem.id,
        room_id: null,
        week: null,
        day_of_week: null,
        period: null,
        start_slot: null,
        quantity: selectedItem.quantity,
        duration: selectedItem.period,
      };
      const updatedData = [...assignedData, newRow];
      setAssignedData(updatedData);
    }
    update_invalid_rows(updatedData, assignsBySemester);
  };

  const remove_btn_onclick = (item) => {
    var tr_id = item.target.parentNode.parentNode.getAttribute("id");
    const updatedData = assignedData.filter((assign) => assign.id != tr_id);
    const updatedAssignsBySemester = assignsBySemester.filter(
      (assign) => assign.id != tr_id
    );

    var r = redundantAssignedData.slice();
    staticAssigned.forEach((a) => {
      if (a.id == tr_id) {
        if (!r.includes(a.id)) {
          r.push(a.id);
        }
        return;
      }
    });
    setRedundantAssignedData(r);
    update_invalid_rows(updatedData, updatedAssignsBySemester);
    setAssignedData((prev) => prev.filter((assign) => assign.id != tr_id));
    setAssignsBySemester((prev) => prev.filter((assign) => assign.id != tr_id));
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

  const handleInputChange = (item, tr_id, property) => {
    const copy = assignedData?.map((row) => {
      if (row.id === tr_id) {
        const new_row_obj = { ...row, [property]: item.target.value };
        return new_row_obj;
      }
      return row;
    });
    console.log(copy);
    update_invalid_rows(copy, assignsBySemester);
    setAssignedData(copy);
  };
  const save_class_onclick = () => {
    const assigned = assignedData?.map((assign) => {
      return {
        ...assign,
        lesson: { id: assign.class_id },
        room: { id: assign.room_id },
        semester_id: selectedItem.semester_id,
      };
    });

    const redundant_assigned = redundantAssignedData.map((id) => {
      return {
        id: id,
      };
    });
    console.log(assigned);

    const update_class = {
      class_code: idInput,
      quantity: quantityInput,
      note: noteInput,
      period: periodInput,
      number_of_lessons: numberOfLessonsInput,
      department_id: departmentInput,
    };

    submit_handler("post", `/lab-timetabling/assign`, {
      update: assigned,
      remove: redundant_assigned,
    });
    console.log(assigned);
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
          <div style={{padding: '0 12px'}}>
            <Button
              onClick={(e) => download_viz_schedule_onclick(assignsBySemester)}
              variant="outlined"
              disabled={assignsBySemester.length === 0}
            >
              Tải xuống lịch màu
              <FileDownloadIcon />
            </Button>
          </div>
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
        <DialogTitle>Xếp lịch thủ công</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <div>
              (số tuần đã xếp = {selectedItem.assigns?.length}, số tuần yêu cầu
              = {totalLessons})
              <table sx={{ width: 1 }}>
                {assignedData?.map((assign, idx) => {
                  var row = assign;
                  return (
                    <Box sx={{ width: 1, mt: 1 }}>
                      <FormControl
                        error={invalidRows[assign.id]}
                        variant="standard"
                      >
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
                                handleInputChange(
                                  item,
                                  assign.id,
                                  "day_of_week"
                                )
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
                          {invalidRows[assign.id]
                            ? "Xếp lịch không hợp lệ"
                            : ""}
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
            </div>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeModal}>Hủy</Button>
          <Button onClick={save_class_onclick} disabled={isDisabled()}>
            Lưu
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
export default ManualAssignScreen;
