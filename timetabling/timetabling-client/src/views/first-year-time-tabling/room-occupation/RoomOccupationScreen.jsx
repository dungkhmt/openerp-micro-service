import React, { useState } from "react";
import RoomUsageChart from "./components/RoomUsageChart";
import FilterSelectBox from "./components/FilterSelectBox";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import { Button } from "@mui/material";
import { request } from "api";

const RoomOccupationScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedWeek, setSelectedWeek] = useState(1);
  const [startDate, setStartDate] = useState(null);
  const handleExportExcel = () => {
    request(
      "post",
      `room-occupation/export?semester=${selectedSemester?.semester}&week=${selectedWeek}`,
      (res) => {
        const blob = new Blob([res.data], {
          type: res.headers["content-type"],
        });
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "Room_Conflict_List.xlsx";

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      },
      (error) => {
        console.error("Error exporting Excel:", error);
      },
      null,
      { responseType: "arraybuffer" }
    ).then();
  };

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-row gap-2">
        <FirstYearSemesterAutoComplete
          setSelectedSemester={setSelectedSemester}
          selectedSemester={selectedSemester}
        />
        <FilterSelectBox
          selectedSemester={selectedSemester}
          selectedWeek={selectedWeek}
          setSelectedWeek={setSelectedWeek}
          setStartDate={setStartDate}
        />
        <Button
          disabled={selectedSemester === null}
          variant="contained"
          onClick={handleExportExcel}
        >
          Xuất File Excel
        </Button>
      </div>
      <RoomUsageChart
        startDate={startDate}
        selectedWeek={selectedWeek}
        semester={selectedSemester?.semester}
      />
    </div>
  );
};

export default RoomOccupationScreen;
