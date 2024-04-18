import React, { useEffect, useState } from "react";
import { Chart } from "react-google-charts";
import { useRoomOccupations } from "../hooks/useRoomOccupations";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const RoomUsageChart = ({ semester, selectedWeek, startDate }) => {
  const { loading, error, data } = useRoomOccupations(semester, startDate);
  const [isEmptyRooms, setEmptyRoom] = useState(false);
  const [roomData, setRoomData] = useState([
    [
      { type: "string", id: "Room" },
      { type: "string", id: "Name" },
      { type: "date", id: "Start" },
      { type: "date", id: "End" },
    ],
  ]);

  let initRoomData = [
    [
      { type: "string", id: "Room" },
      { type: "string", id: "Name" },
      { type: "date", id: "Start" },
      { type: "date", id: "End" },
    ],
  ];

  useEffect(() => {
    if (selectedWeek === null) return;
    if (data?.length > 1) {
      let weekendDate = new Date(startDate);
      weekendDate.setDate(startDate.getDate() + 7);
      setRoomData((prevRoomData) =>{
        if(data?.length === 0) setEmptyRoom(true);
        setEmptyRoom(false);
        return initRoomData.concat(
          data?.filter(
            (room, index) =>
              room?.at(2) > startDate && room?.at(3) < weekendDate
          )
        )}
      );
    }
  }, [selectedWeek]);
  
  return data ? (
    <Chart
      width={"100%"}
      height={"600px"}
      chartType="Timeline"
      loader={
        <div className="flex flex-row gap-4">
          <FacebookCircularProgress />
          <p>Loading Chart...</p>
        </div>
      }
      data={roomData}
      options={{
        timeline: {
          colorByRowLabel: true,
        },
        allowHtml: true,
      }}
    />
  ) : (
    <div>Chọn kỳ học để hiển thị sơ đồ...</div>
  );
};

export default RoomUsageChart;
