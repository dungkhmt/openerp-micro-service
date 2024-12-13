import React from "react";
import { Autocomplete, TextField } from "@mui/material";

const RoomSelectBox = ({ data, selectedItem, setSelectedItem }) => {
  return (
    <div>
      <Autocomplete
        disablePortal
        loadingText="Loading..."
        getOptionLabel={(option) => option && option.semester}
        onChange={(e, semester) => {
          setSelectedItem(semester);
        }}
        value={selectedItem}
        options={data}
        sx={{ width: 200 }}
        renderInput={(params) => <TextField {...params} label="Chọn kỳ" />}
      />
    </div>
  );
};

export default RoomSelectBox;
