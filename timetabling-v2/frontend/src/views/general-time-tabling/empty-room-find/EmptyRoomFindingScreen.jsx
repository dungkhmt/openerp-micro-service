import { useEffect, useState } from "react";
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import { toast } from "react-toastify";
import { request } from "api";
import { useEmptyRoomTableColumns } from "./useEmptyRoomTableColumn";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { Autocomplete, Button, TextField } from "@mui/material";
import { Search } from "@mui/icons-material";

const EmptyRoomFindingScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [roomRequest, setRequest] = useState({
    startTime: 0,
    endTime: 0,
    weekDay: 0,
    week: 0,
    crew: null,
  });

  const [classrooms, setClassrooms] = useState([]);
  const [weeks, setWeeks] = useState([]);

  useEffect(() => {
    if (selectedSemester === null) return;
    request(
      "get",
      `/academic-weeks/?semester=${selectedSemester?.semester}`,
      (res) => {
        console.log(res.data);
        setWeeks(res.data);
        toast.success("Truy vấn tuần học thành công với " + res.data?.length);
      },
      (error) => {
        toast.error("Có lỗi khi truy vấn tuần học!");
        console.log(error);
      }
    );
  }, [selectedSemester]);

  const handleChangeRequestField = (_, value, field) => {
    setRequest((prevReq) => ({
      ...prevReq,
      [field]: value,
    }));
  };

  const handleGetEmptyRooms = () => {
    for (const key of Object.keys(roomRequest)) {
      console.log(roomRequest[key]);
      if (!roomRequest[key]) {
        toast.warn("Cần phải chọn đẩy đủ các trường thông tin!");
        return;
      }
    }
    request(
      "post",
      `/room-occupation/empty-room?semester=${selectedSemester?.semester}`,
      (res) => {
        setClassrooms(res.data);
        toast.success("Tìm phòng thành công");
      },
      (err) => {
        if (err.response.state === 410) {
          toast.error("Không tìm thấy  phòng!");
        } else {
          toast.error("Tìm phòng thất bại!");
        }
      },
      { ...roomRequest }
    );
  };

  return (
    <div className="">
      <div className="flex flex-row justify-between my-4">
        {/* Autocomplete  */}
        <div className="flex flex-col gap-2">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <div className="flex flex-row gap-2">
            <Autocomplete
              sx={{ width: 140 }}
              options={[2, 3, 4, 5, 6, 7, 8]}
              getOptionLabel={(option) =>
                option !== 8 ? `Thứ ${option}` : `Chủ nhật`
              }
              renderInput={(params) => (
                <TextField label={"Chọn thứ"} {...params} />
              )}
              onChange={(e, value) =>
                handleChangeRequestField(e, value, "weekDay")
              }
            />
            <Autocomplete
              sx={{ width: 120 }}
              options={weeks}
              getOptionLabel={(option) => option.weekIndex}
              renderInput={(params) => (
                <TextField label={"Chọn tuần"} {...params} />
              )}
              onChange={(e, value) =>
                handleChangeRequestField(e, value.weekIndex, "week")
              }
            />
          </div>
          <div className="flex flex-row gap-2">
            <Autocomplete
              sx={{ width: 120 }}
              options={["S", "C"]}
              renderInput={(params) => (
                <TextField label={"Chọn kíp"} {...params} />
              )}
              onChange={(e, value) =>
                handleChangeRequestField(e, value, "crew")
              }
            />
            <Autocomplete
              sx={{ width: 140 }}
              options={[1, 2, 3, 4, 5, 6]}
              renderInput={(params) => (
                <TextField label={"Chọn tiết BĐ"} {...params} />
              )}
              onChange={(e, value) =>
                handleChangeRequestField(e, value, "startTime")
              }
            />
            <Autocomplete
              sx={{ width: 140 }}
              options={[1, 2, 3, 4, 5, 6]}
              type="number"
              renderInput={(params) => (
                <TextField label={"Chọn tiết KT"} {...params} />
              )}
              onChange={(e, value) =>
                handleChangeRequestField(e, value, "endTime")
              }
            />
          </div>
        </div>
        {/* Search button */}
        <div className="">
          <Button variant="contained" onClick={handleGetEmptyRooms}>
            <Search />
          </Button>
        </div>
      </div>
      <DataGrid
        sx={{ height: 550 }}
        columns={useEmptyRoomTableColumns()}
        rows={classrooms}
        initialState={{
          filter: {
            filterModel: {
              items: [],
              quickFilterValues: [""],
            },
          },
        }}
        slots={{ toolbar: GridToolbar }}
        slotProps={{
          toolbar: {
            printOptions: { disableToolbarButton: true },
            csvOptions: { disableToolbarButton: true },
            showQuickFilter: true,
          },
        }}
        disableColumnSelector
        disableDensitySelector
      />
    </div>
  );
};

export default EmptyRoomFindingScreen;
