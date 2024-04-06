import React from "react";
import { Chart } from "react-google-charts";
import { useRoomOccupations } from "../hooks/useRoomOccupations";

const RoomUsageChart = ({ semester, selectedWeek }) => {
  const { loading, error, data } = useRoomOccupations(semester);
  console.log(data);

  let roomData = [
    [
      { type: "string", id: "Room" },
      { type: "string", id: "Name" },
      { type: "number", id: "Start" },
      { type: "number", id: "End" },
    ],
  ];

  if (data?.length > 1) {
    roomData = [
      [
        { type: "string", id: "Room" },
        { type: "string", id: "Name" },
        { type: "number", id: "Start" },
        { type: "number", id: "End" },
      ],
    ];
    console.log(
      data?.filter(
        (room, index) => room?.at(2) > 0 && room?.at(3) < (selectedWeek + 1) * 7 *12
      )
    );
    roomData = roomData.concat(
      data?.filter(
        (room, index) => room?.at(2) > 0 && room?.at(3) < (selectedWeek + 1) * 7 *12
      )
    );
  }

  return data ? (
    <Chart
      width={"100%"}
      height={"600px"}
      chartType="Timeline"
      loader={<div>Loading Chart...</div>}
      data={roomData}
      options={{
        timeline: {
          colorByRowLabel: true
        },
        allowHtml: true,
      }}
    />
  ) : (
    <div>Chọn kỳ học để hiển thị sơ đồ...</div>
  );
};

export default RoomUsageChart;
