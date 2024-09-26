import { Autocomplete, TextField } from "@mui/material";
import { request } from "api";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";


const FilterSelectBox = ({
  selectedWeek,
  setSelectedWeek,
  selectedSemester,
}) => {
  const [weeks, setWeeks] = useState([]);

  useEffect(() => {
    if (selectedSemester === null || selectedWeek === null) return;
    request(
      "get",
      `/academic-weeks/?semester=${selectedSemester?.semester}`,
      (res) => {
        console.log(res.data);
        setWeeks(res.data);
        setSelectedWeek(res?.data[0]?.weekIndex);
        toast.success("Truy vấn tuần học thành công với " + res.data?.length);
      },
      (error) => {
        toast.error("Có lỗi khi truy vấn tuần học!");
        console.log(error);
      }
    );
  }, [selectedSemester]);

  return (
    <div>
      <Autocomplete
        disabled={selectedSemester === null}
        defaultValue={selectedWeek?.weekIndex}
        loadingText="Loading..."
        getOptionLabel={(option) => "Tuần " + option?.weekIndex?.toString()}
        onChange={(e, week) => {
          console.log(week);
          if (!week) return;
          setSelectedWeek(week?.weekIndex);
        }}
        value={selectedWeek?.weekIndex}
        options={weeks}
        sx={{ width: 200 }}
        renderInput={(params) => (
          <TextField {...params} label="Lọc theo tuần" />
        )}
      />
    </div>
  );
};

export default FilterSelectBox;
