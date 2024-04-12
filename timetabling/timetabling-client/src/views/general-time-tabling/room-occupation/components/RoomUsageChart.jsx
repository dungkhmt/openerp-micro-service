import React, { useEffect, useState } from "react";
import { Chart } from "react-google-charts";
import { useRoomOccupations } from "../hooks/useRoomOccupations";

const RoomUsageChart = ({ semester, selectedWeek, startDate }) => {
  const { loading, error, data } = useRoomOccupations(semester, startDate);
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
  ]

  useEffect(() => {
    if (selectedWeek === null) return;
    if (data?.length > 1) {
      let weekendDate = new Date(startDate);
      weekendDate.setDate(startDate.getDate() + 7);
      setRoomData(prevRoomData => initRoomData.concat(
        data?.filter(
          (room, index) => room?.at(2) > startDate && room?.at(3) < weekendDate
        )
      ));
    }
  }, [selectedWeek]);

  return data ? (
    <Chart
      width={"100%"}
      height={"600px"}
      chartType="Timeline"
      loader={<div>Loading Chart...</div>}
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
