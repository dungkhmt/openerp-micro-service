import { Autocomplete, TextField } from "@mui/material";
import { useState } from "react";

const AutocompleteCell = ({ id, field, value, api, options, width }) => {
  const handleAutocompleteChange = (event, newValue) => {
    console.log(id, field, value);
    // Call the api that update the general class with id = ?
    // The api will parse the id to real-id with the schedule_index and update the 'field' with 'newValue'
    console.log(newValue);
  };

  return (
    <Autocomplete
      disablePortal
      value={value !== undefined ? { label: value.toString() } : null}
      isOptionEqualToValue={(option, value) => option.label.toString() === value.label}
      onChange={handleAutocompleteChange}
      options={options}
      sx={{ width: width }}
      getOptionLabel={(option) => option.label || ""}
      renderInput={(params) => <TextField {...params} />}
    />
  );
};

export default AutocompleteCell;
