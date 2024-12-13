import { Box } from "@mui/system";
import { request } from "api";
import { useEffect, useState } from "react";
import AssignTable from "./components/AssignTable";
import BasicSelect from "./components/SelectBox";
import { CircularProgress } from "@mui/material";

const TimetableScreen = () => {
  const [semesters, setSemesters] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [classesBySemester, setClassesBySemester] = useState(null);
  const [rooms, setRooms] = useState([]);
  const [chartData, setChartData] = useState(null);
  const [dataTransformProgress, setDataTransformProgress] = useState(false);

  const transform_data = (data) => {
    var res = new Array();
    const cols_max = data.reduce(
      (max, arr) => Math.max(max, arr.assigns.length),
      0
    );
    for (let i = 0; i < data.length; i++) {
      var value = null;
      for (let j = 0; j < cols_max; j++) {
        if (data[i].assigns[j] != null) {
          var assign = data[i].assigns[j];
          value = `room ${assign.room_id}`;
        }
        var cell = {
          x: j,
          y: i,
          z: value,
        };
        res.push(cell);
      }
    }
    return res;
  };
  const semester_on_change = (e) => {
    setDataTransformProgress(true);
    const semesterValue = e.target.value;
    setSelectedSemester(semesterValue);
    request("get", `/lab-timetabling/class/semester/${semesterValue}`, (res) => {
      setClassesBySemester(res.data);
      setChartData(transform_data(res.data));
      setDataTransformProgress(false);
    }).then();
  };

  useEffect(() => {
    request("get", "/lab-timetabling/semester/get-all", (res) => {
      setSemesters(res.data);
    }).then();
    request("get", "/lab-timetabling/room/get-all", (res) => {
      setRooms(res.data);
    }).then();
  }, []);

  return (
    <Box sx={{ width: 1 }}>
      <div style={{ display: "flex", flexDirection: "row" }}>
        <BasicSelect
          items={semesters}
          label={"Học kì"}
          value={selectedSemester}
          onChange={semester_on_change}
        />
      </div>
      <Box
        sx={{
          width: 1,
          height: "75%",
          display: selectedSemester == null ? "none" : "display",
        }}
      >
        {dataTransformProgress?<CircularProgress/>:((classesBySemester?.length>0)?<AssignTable data={classesBySemester} />:"Không có dữ liệu")}
      </Box>
    </Box>
  );
};

export default TimetableScreen;
