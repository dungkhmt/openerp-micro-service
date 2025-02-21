import { useState, useEffect } from "react";
import { request } from "api";
import { useRoomOccupations } from "./hooks/useRoomOccupations";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import FilterSelectBox from "./components/FilterSelectBox";
import { Button } from "@mui/material";
import { Refresh } from "@mui/icons-material";

const RoomOccupationScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedWeek, setSelectedWeek] = useState(null);

  const { data, refresh } = useRoomOccupations(selectedSemester?.semester, selectedWeek?.weekIndex);

  useEffect(() => {
    if (selectedSemester && selectedWeek) refresh();
  }, [selectedSemester, selectedWeek, refresh]);

  const handleExportExcel = () => {
    request(
      "post",
      `room-occupation/export?semester=${selectedSemester?.semester}&week=${selectedWeek}`,
      (res) => {
        const blob = new Blob([res.data], { type: res.headers["content-type"] });
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "Room_Conflict_List.xlsx";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      },
      (error) => console.error("Error exporting Excel:", error),
      null,
      { responseType: "arraybuffer" }
    ).then();
  };

  const createGridCells = (periods) => {
    const gridCells = Array(42).fill(null);
    periods.forEach(({ start, duration, classCode }) => {
      if (start < 1) return;
      gridCells[start] = { colSpan: duration, classCode };
      for (let i = 1; i < duration; i++) {
        gridCells[start + i] = { hidden: true };
      }
    });
    return gridCells;
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-row justify-between">
        <div className="flex flex-row gap-2">
          <GeneralSemesterAutoComplete
            setSelectedSemester={setSelectedSemester}
            selectedSemester={selectedSemester}
          />
          <FilterSelectBox
            selectedSemester={selectedSemester}
            selectedWeek={selectedWeek}
            setSelectedWeek={setSelectedWeek}
          />
        </div>
        <div className="flex flex-row gap-2">
          <Button disabled={!selectedSemester} variant="contained" onClick={handleExportExcel}>
            Xuất File Excel
          </Button>
          <Button disabled={!selectedSemester || selectedWeek == null} variant="contained" onClick={refresh}>
            <Refresh />
          </Button>
        </div>
      </div>
      <table className="room-occupation-table border-[1px] border-slate-600 overflow-auto">
        <thead>
          <tr>
            <th className="border-[1px] border-slate-600" rowSpan="2">
              Phòng học
            </th>
            {Array.from({ length: 7 }).map((_, index) => (
              <th
                key={index}
                colSpan="6"
                className="cell text-center border-[1px] border-slate-600"
              >
                {index + 2 === 8 ? "CN" : index + 2}
              </th>
            ))}
          </tr>
          <tr>
            {Array.from({ length: 7 }).flatMap((_, day) =>
              Array.from({ length: 6 }).map((_, periodIndex) => (
                <th
                  key={`${day}-${periodIndex}`}
                  className="cell border-[1px] border-slate-600 text-center"
                >
                  {periodIndex + 1}
                </th>
              ))
            )}
          </tr>
        </thead>
        <tbody>
          {data.map((roomData) => {
            const gridCells = createGridCells(roomData.periods);
            return (
              <tr key={roomData.room}>
                <th className="border-[1px] border-slate-600">{roomData.room}</th>
                {gridCells.map((cell, index) => {
                  if (cell === null)
                    return (
                      <td
                        key={index}
                        className="cell border-[1px] border-slate-600"
                      ></td>
                    );
                  if (cell.hidden) return null;
                  return (
                    <td
                      key={index}
                      colSpan={cell.colSpan}
                      title={cell.classCode}
                      className="cell bg-yellow-400 text-xs overflow-hidden border-[1px] border-slate-600"
                    >
                      {cell.classCode}
                    </td>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default RoomOccupationScreen;
