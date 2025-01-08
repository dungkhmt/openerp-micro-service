import FileDownloadIcon from "@mui/icons-material/FileDownload";
import { Button } from "@mui/material";
import { request } from "api";
import Cookies from 'js-cookie';
import { useEffect, useState } from "react";
import { datetimeForFN, days_Of_Week } from "utils/formatter";
import writeXlsxFile from "write-excel-file";
import BasicSelect from "./components/SelectBox";

const ConflictCheckingScreen = () => {
  const [assignsBySemester, setAssignsBySemester] = useState([]);
  const [conflicts, setConflicts] = useState(null);
  const [semesters, setSemesters] = useState([]);
  const [currentSemester, setCurrentSemester] = useState("");
  const [loading, setLoading] = useState(false);
  const [id, alert] = useState("");
  const [socket, setSocket] = useState(null); 
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
  }, []);

  const semester_on_change = (value) => {
    var sem = value.target.value;
    setCurrentSemester(sem);
    request(
      "get",
      `/lab-timetabling/assign/semester/${sem}`,
      (res) => {
        setAssignsBySemester(res.data);
        console.log(res.data);
      },
      (err) => {
        console.log(err);
      }
    ).then();
  };

  const printAssign = (assign) => {
    console.log(assign);
    return (
      <span>
          Class <strong>{assign.lesson}</strong> in room{" "}
          <strong>{assign.room}</strong> at week <strong>{assign.week}</strong>{" "}
          in <strong>{days_Of_Week.filter(day=>day.id=assign.day_of_week)[0].name}</strong> 
      </span>
    );
  };
  const printAssignConflict = (assignConflict) => {
    return (
      <span>
        {printAssign(assignConflict.assignResponse)} -{" "}
        {assignConflict.conflictType}
      </span>
    );
  };

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
      console.log(obj)
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
      const start = (assign.day_of_week-2)*12 + (assign.period-1)*6 + assign.start_slot
      let day = (assign.week - 1) * 7 + Math.floor(start / 12) + 1;
      // +1 because of the first row is for room
      const x = day + 1;
      // +2 because of the first 2 columns are for week and day
      const y =
        (start % 12) - 1 + 2 + uniqueRooms.indexOf(assign.room_id) * 12;
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
    if(conflicts?.length>0){
      const sortedData = conflicts.sort((a, b) => {
        return a.assignResponse.duration - b.assignResponse.duration;
      });
      for (var conflict of sortedData) {
        if (conflict.conflictType === "CAP_EXCEPT_CONFLICT") continue;
        const start = (assign.day_of_week-2)*12 + (assign.period-1)*6 + assign.start_slot
        const assign = conflict.assignResponse;
        let day = (assign.week - 1) * 7 + Math.floor(start / 12) + 1;
        // +1 because of the first row is for room
        const x = day + 1;
        // +2 because of the first 2 columns are for week and day
        const y = (start % 12) - 1 + 2 + uniqueRooms.indexOf(assign.room_id) * 12;
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

  return (
    <div>
      <div
        style={{ display: "flex", flexDirection: "row", alignItems: "center" }}
      >
        <BasicSelect
            items={semesters}
            label={"Hoc kì"}
            value={currentSemester}
            onChange={semester_on_change}
          />
      </div>
      <div style={{ display: "flex", flexDirection: "row" }}>
          <Button
            onClick={(e)=>download_viz_schedule_onclick(assignsBySemester)}
            variant="outlined"
            disabled={assignsBySemester.length === 0}
          >
            visual schedule
            <FileDownloadIcon />
          </Button>
      </div>
      {id}
      <ul>
        {
          (conflicts==0)?<li>No conflict found</li>:
          conflicts?.map((conflict, index) => {
            return <li key={index}>{printAssignConflict(conflict)}</li>;
          })
        }
      </ul>
    </div>
  );
};

export default ConflictCheckingScreen;
