import React, { useState } from "react";
import RoomUsageChart from "./components/RoomUsageChart";
import RoomSelectBox from "./components/RoomSelectBox";
import { useSemesters } from "../general-schedule/hooks/useSemester";
import FilterSelectBox from "./components/FilterSelectBox";

const RoomOccupationScreen = () => {
  const [selectedItem, setSelectedItem] = useState(null);
  const [selectedWeek, setSelectedWeek] = useState(0);
  const { loading, error, semesters } = useSemesters();
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-row gap-2">
        <RoomSelectBox  data={semesters} setSelectedItem={setSelectedItem} />
        <FilterSelectBox selectedWeek={selectedWeek} setSelectedWeek={setSelectedWeek}/>
      </div>
      <RoomUsageChart selectedWeek = {selectedWeek} semester={selectedItem?.semester} />
    </div>
  );
};

export default RoomOccupationScreen;
