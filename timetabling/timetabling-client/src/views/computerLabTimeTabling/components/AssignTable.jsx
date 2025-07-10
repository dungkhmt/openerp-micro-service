import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import TextField from "@mui/material/TextField";
import { request } from "api";
import React, { useEffect, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import { toast } from "react-toastify";

import {
  days_Of_Week,
  periods_Of_Day,
  time_By_Slot,
  weeks_Of_Semester,
  slots_Of_Period
} from "utils/formatter";
import BasicSelect from "./SelectBox";

function AssignTable({ data }) {
  const [semesters, setSemesters] = useState([]);
  const [currentSemester, setCurrentSemester] = useState(null);
  const [assignedData, setAssignedData] = useState([]);
  const [assignsBySemester, setAssignsBySemester] = useState([]);
  const [roomsByDepartment, setRoomsByDepartment] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [redundantAssignedData, setRedundantAssignedData] = useState([]);
  const [staticAssigned, setStaticAssigned] = useState([]);
  const [selectedClassIds, setSelectedClassIds] = useState([]);

  const [updateModalVisible, setUpdateModalVisible] = useState(false);

  const [totalLessons, setTotalLessons] = useState(0);

  const [loading, setLoading] = useState(false);
  const [selectedItem, setSelectedItem] = useState({});
  const [idInput, setIdInput] = useState("");
  const [noteInput, setNoteInput] = useState("");
  const [quantityInput, setQuantityInput] = useState("");
  const [numberOfLessonsInput, setNumberOfLessonsInput] = useState("");
  const [periodInput, setPeriodInput] = useState("");
  const [departmentInput, setDepartmentInput] = useState("");

  const [invalidRows, setInvalidRows] = useState({});
  const [renderKey, setRenderKey] = useState(0);
  const edit_btn_onclick = (e) => {
    const item_id = e.target.parentNode.parentNode.getAttribute("id")
    const item  = data.filter((item) => item.id == item_id)[0];
    request("get", `/lab-timetabling/room/department/${item.department_id}`, (res) => {
      setRoomsByDepartment(res.data);
    });

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
    setNumberOfLessonsInput(item.number_of_lessons);
    setNoteInput(item.note);

    setUpdateModalVisible(true);
  };

  const closeModal = () => {
    setRedundantAssignedData([]);
    setUpdateModalVisible(false);
    setSelectedClassIds([]);

    setAssignsBySemester([]);
    setAssignedData([]);
    setInvalidRows({});
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
      invalid[assign.id] = checkConstraints(assign, assigned_set_array);
    });
    setInvalidRows(invalid);
  };
  // Check if any property of the object is null
const hasNullProperty = (object) => {
  for (let key in object) {
    if (object.hasOwnProperty(key)) {
      if (object[key] == null) return true;
    }
  }
  return false;
};

// Check room capacity constraint
const checkRoomCapacity = (object, roomsByDepartment) => {
  const roomAssigned = roomsByDepartment.find(room => room.id == object.room_id);
  return roomAssigned && roomAssigned.capacity < object.quantity;
};

// Check time assigned constraint
const checkTimeAssignedConstraint = (object, assignedSetArray) => {
  for (let i = 0; i < assignedSetArray.length; i++) {
    if (assignedSetArray[i].id == object.id) continue;
    const assign = assignedSetArray[i];
    if (assign.week == object.week && assign.room_id == object.room_id) {
      const timeSlot = (day, period, start) => (day - 2) * 12 + (period - 1) * 6 + start;
      const firstTimeSlot = timeSlot(assign.day_of_week, assign.period, assign.start_slot);
      const secondTimeSlot = timeSlot(object.day_of_week, object.period, object.start_slot);
      if ((firstTimeSlot <= secondTimeSlot && secondTimeSlot <= firstTimeSlot + assign.duration - 1) ||
          (secondTimeSlot <= firstTimeSlot && firstTimeSlot <= secondTimeSlot + object.duration - 1)) {
        return true;
      }
    }
  }
  return false;
};
  const checkConstraints = (object, assignedSetArray) => {
    return hasNullProperty(object) || checkRoomCapacity(object, roomsByDepartment) || checkTimeAssignedConstraint(object, assignedSetArray);
  };


  const handleInputChange = (item, tr_id, property) => {
    const copy = assignedData?.map((row) => {
      if (row.id === tr_id) {
        const new_row_obj = { ...row, [property]: item.target.value };
        return new_row_obj;
      }
      return row;
    });
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

    const update_class = {
      class_code: idInput,
      quantity: quantityInput,
      note: noteInput,
      period: periodInput,
      number_of_lessons: numberOfLessonsInput,
      department_id: departmentInput,
    };

    submit_handler("patch", `/lab-timetabling/class/${selectedItem.id}`, update_class);
    submit_handler("post", `/lab-timetabling/assign`, {
      update: assigned,
      remove: redundant_assigned,
    });
    console.log(assigned);
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
  const update_callback = (type, msg) => {
    toast(msg, {
      position: toast.POSITION.TOP_RIGHT,
      hideProgressBar: false,
      type: type,
    });
  };
  useEffect(() => {
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
    request("get", "/lab-timetabling/room/get-all", (res) => {
      setRooms(res.data);
    }).then();

  }, []);
  return (
    <div className="container">
      <table>
        <thead>
          <tr>
            <th>Mã lớp</th>
            <th>Lớp</th>
            <th>Thời khóa biểu</th>
          </tr>
        </thead>
        <tbody renderKey={renderKey}>
          {data?.map((obj, index) => (
            <tr style={{ height: "100px", maxHeight: "100px" }} id={obj.id} key={obj.id}>
              <td>
                {obj.class_code}
              </td>
              <td style={{ padding: "0 12px" }}>
                  {obj.note}
              </td>
              <td style={{ backgroundColor: "#f3f3f3" }}>
                <ul style={{ display: "flex", flexDirection: "row" }}>
                  {obj.assigns?.map((child, childIndex) => (
                    <li
                      style={{ padding: "0 12px", whiteSpace: "nowrap" }}
                      key={childIndex}
                    >
                      <div
                        style={{
                          display: "flex",
                          flexDirection: "column",
                          alignContent: "center",
                          alignItems: "center",
                        }}
                      >
                        <span style={{ padding: "2px" }}>
                          Tuần {child.week}
                        </span>
                        <span style={{ padding: "2px" }}>
                          {"Thứ "}{
                            child.day_of_week
                          }{" "}
                          -{" "}
                          {
                            periods_Of_Day.filter(
                              (p) => p.id == child.period
                            )[0]?.name
                          }{" "}
                          -{" "}
                          {
                            time_By_Slot.filter(
                              (t) => t.id == (child.period-1)*6+child.start_slot
                            )[0].time
                          }
                        </span>
                        <span style={{ padding: "2px" }}>
                          {rooms.filter((r) => r.id == child.room_id)[0]?.name}
                        </span>
                      </div>
                    </li>
                  ))}
                </ul>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <Dialog
        open={updateModalVisible}
        onClose={closeModal}
        fullWidth={true}
        maxWidth={"md"}
      >
        <DialogTitle>Update class</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 1 }}>
            <div
              style={{ display: "flex", flexDirection: "column", gap: "1rem" }}
            >
              <div>
                <BasicSelect
                  items={departments}
                  label={"Department"}
                  value={departmentInput}
                  onChange={(e) => setDepartmentInput(e.target.target)}
                />
              </div>
              <TextField
                label="Note"
                id="outlined-size-small"
                defaultValue={noteInput}
                onChange={(e) => setNoteInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Class ID"
                id="outlined-size-small"
                defaultValue={idInput}
                onChange={(e) => setIdInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Quantity"
                id="outlined-size-small"
                defaultValue={quantityInput}
                onChange={(e) => setQuantityInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
              <TextField
                label="Period"
                id="outlined-size-small"
                defaultValue={periodInput}
                onChange={(e) => setPeriodInput(e.target.value)}
                size="small"
                style={inputTextStyle}
              />
            </div>
            <div>
              time table (assigned = {selectedItem.assigns?.length}, require ={" "}
              {totalLessons})
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
            </div>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeModal}>Cancel</Button>
          <Button onClick={save_class_onclick}>Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
const inputTextStyle = {
  marginBottom: "1rem",
};
export default AssignTable;
