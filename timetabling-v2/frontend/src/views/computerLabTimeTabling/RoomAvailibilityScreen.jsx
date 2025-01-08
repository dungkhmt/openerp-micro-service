import { useEffect, useState, useRef } from "react";
import RoomStatusChart from "./components/RoomStatusChart";
import { request } from "api";
import BasicSelect from "./components/SelectBox";
import { weeks_Of_Semester } from "utils/formatter";
import { Box, CircularProgress, LinearProgress } from "@mui/material";
import { set } from "react-hook-form";

function RoomAvailibilityScreen() {
  const [heatData, setHeatData] = useState([]);
  const [classList, setClassList] = useState([]);
  const [roomList, setRoomList] = useState([]);
  const [roomChartKey, setRoomChartKey] = useState(0); // State để điều khiển việc render lại RoomStatusChart
  const [gridData, setGridData] = useState(null); // Dữ liệu để hiển thị lịch trình của từng phòng

  const [semesters, setSemesters] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedWeek, setSelectedWeek] = useState(null);
  const [totalHeight, setTotalHeight] = useState(0);
  const wrapperRef = useRef(null);
  const [dataTransformProgress, setDataTransformProgress] = useState(false); 

  useEffect(() => {
    if (wrapperRef.current) {
      setTotalHeight(wrapperRef.current.clientHeight);
    }
    request("get", "/lab-timetabling/semester/get-all", (res) => {
        setSemesters(res.data);
    }).then();
  }, []);

  const fetch_heat_data = async (semester, week) => {
    if (semester == null) semester = 0;
    if (week == null) week = 0;
    console.log(`/lab-timetabling/heatmap?semesterId=${semester}&week=${week}`);
    const result = await request(
      "get",
      `/lab-timetabling/heatmap?semesterId=${semester}&week=${week}`,
      (res) => {
        return res.data;
      },
      (err) => {
        return [];
      }
    ).then((res) => {
      return res.data;
    });
    return result;
  };

  const heatmap_data_transform = async (heat) => {
    const rooms = await request(
      "get",
      "/lab-timetabling/room/get-all",
      (res) => {
        return res.data;
      },
      (err) => {
        return [];
      }
    ).then((res) => {
      return res.data;
    });
    var heatmap_data = [];
    const num_slots = 12 * 7;
    for (let i = 0; i < num_slots; i++) {
      heatmap_data.push([]);
      for (let j = 0; j < rooms.length; j++) {
        heatmap_data[i].push({value: 0});
      }
    }
    for (let i = 0; i < heat?.length; i++) {
      heatmap_data[heat[i].time_slot][heat[i].room_index] = {value: heat[i].value, class_index: heat[i].class_index};
    }
    var heatmap_data_array = [];
    for (let i = 0; i < num_slots; i++) {
      for (let j = 0; j < rooms.length; j++) {
        heatmap_data_array.push({ x: i, y: j, z: heatmap_data[i][j]});
      }
    }
    console.log(heatmap_data_array);
    return {heatmap_data, heatmap_data_array};
  };
  const handleInputChange = async (semesterValue, weekValue) => {
    setDataTransformProgress(true);
    const res = await fetch_heat_data(semesterValue, weekValue);
    const { heatMap, classList, roomList } = res;
    const {heatmap_data, heatmap_data_array} = await heatmap_data_transform(heatMap.heatValues);
    setGridData(heatMap.heatValues);
    setHeatData(heatmap_data_array);
    setDataTransformProgress(false)
    setClassList(classList);
    setRoomList(roomList);
    setRoomChartKey(prevKey => prevKey + 1);
  };

  const semester_on_change = (e) => {
    const semesterValue = e.target.value;
    setSelectedSemester(semesterValue);
    setSelectedWeek(selectedWeek);
    handleInputChange(semesterValue, selectedWeek);
    console.log(semesterValue);
  };

  const week_on_change = (e) => {
    const weekValue = e.target.value;
    setSelectedSemester(selectedSemester);
    setSelectedWeek(weekValue);
    handleInputChange(selectedSemester, weekValue);
  };
  return (
    <Box sx={{ width: 1}}>
      <div ref={wrapperRef}>
        <div style={{ display: "flex", flexDirection: "row"}}>
          <BasicSelect
            items={semesters}
            label={"Học kì"}
            value={selectedSemester}
            onChange={semester_on_change}
          />
          <div style={{margin: "0 12px"}}>
            <BasicSelect
              items={weeks_Of_Semester}
              label={"Tuần"}
              value={selectedWeek}
              onChange={week_on_change}
              disabled={selectedSemester == null}
              />
          </div>
        </div>
      </div>
      <Box sx={{ width: 1, height: '75%', display: (selectedWeek==null)?'none':'display'}}>
        {(dataTransformProgress?<CircularProgress/>:(<RoomStatusChart key={roomChartKey} classList={classList} roomList={roomList} data={heatData} />))}
      </Box>
    </Box>
  );
}
export default RoomAvailibilityScreen;
