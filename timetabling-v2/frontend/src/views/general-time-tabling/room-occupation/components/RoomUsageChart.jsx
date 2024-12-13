import React, { useEffect, useState } from "react";
import { Chart } from "react-google-charts";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const RoomUsageChart = ({ semester, selectedWeek, startDate, data }) => {
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
      { type: "string", role: "tooltip" },
      { type: "date", id: "Start" },
      { type: "date", id: "End" },
    ],
  ];

  useEffect(() => {
    if (selectedWeek === null) return;
    const startWeekDate = new Date(startDate).setDate(
      startDate?.getDate() + (selectedWeek - 1) * 7
    );
    const endWeekDate = new Date(startDate).setDate(
      startDate?.getDate() + selectedWeek * 7
    );
    console.log(new Date(startWeekDate), new Date(endWeekDate));
    data?.push([
      "End",
      "End",
      "",
      new Date(endWeekDate),
      new Date(endWeekDate),
    ]);
    data?.unshift([
      "Start",
      "Start",
      "",
      new Date(startWeekDate),
      new Date(startWeekDate),
    ]);
    setRoomData(roomHeader.concat(data));
  }, [data]);

  return semester ? (
    data && data?.length > 0 ? (
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
          tooltip: { isHtml: true, className: "custom-tooltip" }, // Enable HTML tooltips
          legend: "none",
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
