import React, { useState } from "react";
import RoomUsageChart from "./components/RoomUsageChart";
import FilterSelectBox from "./components/FilterSelectBox";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { Button } from "@mui/material";
import { request } from "api";
import { Refresh } from "@mui/icons-material";
import { useRoomOccupations } from "./hooks/useRoomOccupations";

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

  


  const { loading, error, data , refresh } = useRoomOccupations(
    selectedSemester?.semester,
    startDate,
    selectedWeek
  );

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-row justify-between">
        <div className="flex flex-row  gap-2 ">
          <GeneralSemesterAutoComplete
            setSelectedSemester={setSelectedSemester}
            selectedSemester={selectedSemester}
          />
          <FilterSelectBox
            selectedSemester={selectedSemester}
            selectedWeek={selectedWeek}
            setSelectedWeek={setSelectedWeek}
            setStartDate={setStartDate}
          />
        </div>
        <div className="flex flex-row gap-2 ">
          <Button
            disabled={selectedSemester === null}
            variant="contained"
            onClick={handleExportExcel}
          >
            Xuất File Excel
          </Button>
          <Button
            disabled={selectedSemester === null || selectedWeek == null}
            variant="contained"
            onClick={refresh}
          >
            <Refresh/>
          </Button>
        </div>
      </div>
      <RoomUsageChart
        startDate={startDate}
        selectedWeek={selectedWeek}
        semester={selectedSemester?.semester}
        data={data}
      />
    </div>
  );
};

export default RoomOccupationScreen;
