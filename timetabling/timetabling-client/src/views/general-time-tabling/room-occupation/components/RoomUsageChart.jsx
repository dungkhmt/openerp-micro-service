import React, { useEffect, useState } from "react";
import { Chart } from "react-google-charts";
import { useRoomOccupations } from "../hooks/useRoomOccupations";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const RoomUsageChart = ({ semester, selectedWeek, startDate }) => {
  const { loading, error, data } = useRoomOccupations(
    semester,
    startDate,
    selectedWeek
  );
  console.log(semester);
  console.log(startDate);
  console.log(data);
  console.log(selectedWeek);
  const [roomData, setRoomData] = useState([
    [
      { type: "string", id: "Room" },
      { type: "string", id: "Name" },
      { type: "date", id: "Start" },
      { type: "date", id: "End" },
    ],
  ]);

  const roomHeader = [
    [
      { type: "string", id: "Room" },
      { type: "string", id: "Name" },
      { type: "date", id: "Start" },
      { type: "date", id: "End" },
    ],
  ];

  useEffect(() => {
    if (selectedWeek === null) return;
    setRoomData(roomHeader.concat(data));
  }, [data]);

  return semester ? (
    (data && data?.length > 0)  ? (
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
      <div className="">Tuần học đang trống</div>
    )
  ) : (
    <div>Chọn kỳ học để hiển thị sơ đồ...</div>
  );
};

export default RoomUsageChart;
