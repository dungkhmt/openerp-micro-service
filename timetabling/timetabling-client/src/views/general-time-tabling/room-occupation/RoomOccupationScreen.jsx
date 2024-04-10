import React, { useState } from "react";
import RoomUsageChart from "./components/RoomUsageChart";
import FilterSelectBox from "./components/FilterSelectBox";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";

const RoomOccupationScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedWeek, setSelectedWeek] = useState(0);
  const [startDate, setStartDate] = useState(null);
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-row gap-2">
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
      <RoomUsageChart
        startDate={startDate}
        selectedWeek={selectedWeek}
        semester={selectedSemester?.semester}
      />
    </div>
  );
};

export default RoomOccupationScreen;
