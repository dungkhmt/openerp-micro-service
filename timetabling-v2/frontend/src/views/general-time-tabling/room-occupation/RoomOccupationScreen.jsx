import { useState } from "react";
import { request } from "api";
import { useRoomOccupations } from "./hooks/useRoomOccupations";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import FilterSelectBox from "./components/FilterSelectBox";
import { Button } from "@mui/material";
import { Refresh } from "@mui/icons-material";

const RoomOccupationScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedWeek, setSelectedWeek] = useState(1);

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

  const { data, refresh } = useRoomOccupations(
    selectedSemester?.semester,
    selectedWeek
  );

  console.log(data);

  // return (
  //   <div className="flex flex-col gap-4">
  //     <RoomUsageChart
  //       startDate={startDate}
  //       selectedWeek={selectedWeek}
  //       semester={selectedSemester?.semester}
  //       data={data}
  //     />
  //   </div>
  // );

  return (
    <div className="flex flex-col gap-2">
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
            <Refresh />
          </Button>
        </div>
      </div>
      <table className=" room-occupation-table border-[1px] border-slate-600 overflow-auto">
        {/* Header */}
        <thead>
          <th
            className=" border-[1px] border-slate-600 w-full"
            rowSpan="2"
            colSpan={2}
          >
            Phòng học/ Thời gian
          </th>

          {Array.from({ length: 7 }).map((v, index) => (
            <td
              key={index}
              colSpan="6"
              className="cell text-center border-[1px] border-slate-600"
            >
              {index + 2 === 8 ? "CN" : index + 2}
            </td>
          ))}
          <tr>
            {Array.from({ length: 6 * 7 }).map((v, index) => (
              <td className="cell border-[1px] border-slate-600" key={index}>
                {(index % 6) + 1}
              </td>
            ))}
          </tr>
        </thead>
        {/* Content */}
        <tbody>
          {data.map((period) => {
            const morningCells = Array(42).fill(null);
            const afternoonCells = Array(42).fill(null);

            period.periods.forEach(({ start, duration, classCode, crew }) => {
              if (crew === "S") {
                morningCells[start] = { colSpan: duration, classCode };
                for (let i = 1; i < duration; i++) {
                  morningCells[start + i] = { hidden: true };
                }
              }
            });

            period.periods.forEach(({ start, duration, classCode, crew }) => {
              if (crew === "C") {
                afternoonCells[start] = { colSpan: duration, classCode };
                for (let i = 1; i < duration; i++) {
                  afternoonCells[start + i] = { hidden: true };
                }
              }
            });

            return (
              <tr key={period.room}>
                <th
                  className=" border-[1px] border-slate-600"
                  rowSpan={1}
                  colSpan={1}
                >
                  {period.room}
                </th>
                <th className=" border-[1px] border-slate-600" colSpan={1}>
                  <tr className="flex">
                    <td className="cell w-full border-b-[1px] border-slate-600">
                      S
                    </td>
                  </tr>
                  <tr className="flex">
                    <td className="cell w-full">C</td>
                  </tr>
                </th>
                <th
                  className=" border-[1px] border-slate-600"
                  rowSpan={1}
                  colSpan={42}
                >
                  <tr className="border-b-[1px] border-slate-600">
                    {morningCells.map((cell, index) => {
                      if (cell === null)
                        return (
                          <td
                            className="cell w-full border-x-[0.5px] border-slate-600"
                            key={index}
                          ></td>
                        );
                      if (cell.hidden) return null;
                      switch (cell.colSpan) {
                        case 2:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[52px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 3:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[78px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 4:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[104px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 5:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[130px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 6:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[156px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                      }
                    })}
                  </tr>
                  <tr className="">
                    {afternoonCells.map((cell, index) => {
                      if (cell === null)
                        return (
                          <td
                            className="cell w-full border-x-[0.5px] border-slate-600"
                            key={index}
                          ></td>
                        );
                      if (cell.hidden) return null;
                      switch (cell.colSpan) {
                        case 2:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[52px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 3:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[78px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 4:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[104px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 5:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[130px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                        case 6:
                          return (
                            <td
                              title={`${cell.classCode}: ${
                                Math.floor(index / 6) + 2 !== 8
                                  ? `Thứ ${Math.floor(index / 6) + 2}`
                                  : `CN`
                              }/${(index % 6) + 1}-${
                                ((index + cell.colSpan - 1) % 6) + 1
                              }`}
                              key={index}
                              className={`cell max-w-[156px] bg-yellow-400 overflow-hidden text-xs border-x-[0.5px] border-slate-600 `}
                              colSpan={cell.colSpan}
                            >
                              {cell.classCode}
                            </td>
                          );
                      }
                    })}
                  </tr>
                </th>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default RoomOccupationScreen;
