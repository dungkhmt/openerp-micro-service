import { Autocomplete, TextField } from "@mui/material";
import React from "react";

const FilterSelectBox = ({ selectedWeek, setSelectedWeek }) => {
  return (
    <div>
      <Autocomplete
        disablePortal
        loadingText="Loading..."
        getOptionLabel={(i) => {
            console.log(i);
          return `Tuần ${i + 1}`;
        }}
        onChange={(e, index) => {
          setSelectedWeek(index + 1);
        }}
        value={selectedWeek}
        options={Array.from({ length: 16 })}
        sx={{ width: 200 }}
        renderInput={(params) => (
          <TextField {...params} label="Lọc theo tuần" />
        )}
      />
    </div>
  );
};

export default FilterSelectBox;
