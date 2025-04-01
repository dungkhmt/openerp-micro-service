import { useState, useEffect } from "react";
import { request } from "api";
import { useRoomOccupations } from "./hooks/useRoomOccupations";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import FilterSelectBox from "./components/FilterSelectBox";
import { Button, TablePagination, Autocomplete, TextField } from "@mui/material";
import { Refresh } from "@mui/icons-material";

const RoomOccupationScreen = ({ selectedSemester, setSelectedSemester }) => {
  const [selectedWeek, setSelectedWeek] = useState(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(25);

  const { data, refresh } = useRoomOccupations(selectedSemester?.semester, selectedWeek);

  useEffect(() => {
    if (selectedSemester && selectedWeek) refresh();
  }, [selectedSemester, selectedWeek, refresh]);

  const handleExportExcel = () => {
    request(
      "post",
      `room-occupation/export?semester=${selectedSemester?.semester}&week=${selectedWeek?.weekIndex}`,
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

  const createSessionCells = (periods) => {
    const cells = Array(42).fill(null);
    if (!periods) return cells;
    
    periods.forEach(({ start, duration, classCode }) => {
      if (start < 1) return;
      cells[start] = { colSpan: duration, classCode };
      for (let i = 1; i < duration; i++) {
        cells[start + i] = { hidden: true };
      }
    });
    return cells;
  };

  const renderCell = (cell, index) => {
    if (cell === null) return (
      <td 
        key={index} 
        className="cell border-x border-slate-300"
        style={{ width: '40px', minWidth: '40px', maxWidth: '40px' }}
      />
    );
    if (cell.hidden) return null;

    const day = Math.floor(index / 6) + 2;
    const dayText = day === 8 ? 'CN' : `Thứ ${day}`;
    const periodStart = (index % 6) + 1;
    const periodEnd = ((index + cell.colSpan - 1) % 6) + 1;
    const title = `${cell.classCode}: ${dayText}/${periodStart}-${periodEnd}`;
    
    const baseWidth = 40;
    const width = baseWidth * cell.colSpan;

    return (
      <td
        key={index}
        title={title}
        colSpan={cell.colSpan}
        style={{
          width: `${width}px`,
          minWidth: `${width}px`,
          maxWidth: `${width}px`
        }}
        className="cell bg-yellow-300 text-center overflow-hidden text-xs border-x border-slate-300"
      >
        {cell.classCode}
      </td>
    );
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  return (
    <div className="flex flex-col gap-2 h-[700px]">
      <div className="flex flex-row justify-between">
        <div className="flex flex-row gap-2">
          <Autocomplete
            disabled={true}
            value={selectedSemester}
            options={[selectedSemester].filter(Boolean)}
            getOptionLabel={(option) => option && option.semester}
            sx={{ width: 200 }}
            renderInput={(params) => <TextField {...params} label="Chọn kỳ" />}
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
      <div className="overflow-auto flex-grow border rounded-lg">
        <table className="border-collapse border border-slate-300">
          <thead className="sticky top-0 bg-white z-10">
            <tr>
              <th className="border border-slate-300 bg-gray-50" 
                  rowSpan="2" 
                  colSpan="2"
                  style={{ minWidth: '150px' }}>
                Phòng học/ Thời gian
              </th>
              {Array.from({ length: 7 }).map((_, index) => (
                <th 
                  key={index} 
                  colSpan="6" 
                  className="cell text-center border border-slate-300 bg-gray-50"
                  style={{ width: '240px', minWidth: '240px' }} // 6 periods × 40px
                >
                  {index + 2 === 8 ? "CN" : `Thứ ${index + 2}`}
                </th>
              ))}
            </tr>
            <tr>
              {Array.from({ length: 42 }).map((_, index) => (
                <th 
                  key={index} 
                  className="cell border border-slate-300 text-center bg-gray-50"
                  style={{ width: '40px', minWidth: '40px', maxWidth: '40px' }}
                >
                  {(index % 6) + 1}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((roomData, index) => (
                <tr key={roomData.room}>
                  <th className="border-x border-slate-300" rowSpan="1">
                    {roomData.room}
                  </th>
                  <th className="border-x border-slate-300">
                    <div className="flex flex-col">
                      <div className="border-b border-slate-300 p-1">S</div>
                      <div className="p-1">C</div>
                    </div>
                  </th>
                  <td colSpan="42" className="p-0 border-b border-slate-300">
                    <div className="flex flex-col">
                      <div className="border-b border-slate-300 flex">
                        {createSessionCells(roomData.morningPeriods).map((cell, index) => renderCell(cell, index))}
                      </div>
                      <div className="flex">
                        {createSessionCells(roomData.afternoonPeriods).map((cell, index) => renderCell(cell, index))}
                      </div>
                    </div>
                  </td>
                </tr>
            ))}
          </tbody>
        </table>
      </div>

      <TablePagination
        component="div"
        count={data.length}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[25, 50, 100]}
        labelRowsPerPage="Số hàng mỗi trang:"
        labelDisplayedRows={({ from, to, count }) =>
          `${from}-${to} trên ${count}`
        }
        className="border-t border-slate-300"
      />
    </div>
  );
};

export default RoomOccupationScreen;
