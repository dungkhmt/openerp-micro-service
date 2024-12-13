import { Autocomplete, TextField } from "@mui/material";
import { request } from "api";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

function convertToDate(dateString) {
  // Split the string by '/'
  var parts = dateString.split("/");

  // Extract day, month, and year
  var day = parseInt(parts[0], 10);
  var month = parseInt(parts[1], 10) - 1; // Months are 0-based (0 = January)
  var year = parseInt(parts[2], 10);

  // Construct the Date object
  var date = new Date(year, month, day);
  console.log(date);
  return date;
}

const FilterSelectBox = ({
  selectedWeek,
  setSelectedWeek,
  selectedSemester,
  setStartDate,
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
        setStartDate(convertToDate(res?.data[0]?.startDayOfWeek))
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
