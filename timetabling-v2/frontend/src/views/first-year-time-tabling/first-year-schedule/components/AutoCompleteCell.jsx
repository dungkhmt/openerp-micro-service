import { Autocomplete, TextField } from "@mui/material";

const AutocompleteCell = ({
  row,
  onSaveTimeSlot,
  id,
  field,
  value,
  options,
  width,
}) => {
  const handleAutoCompleteChange = (event, newValue) => {
    const newRequest = {
      roomReservationId: row?.roomReservationId,
      startTime: row?.startTime,
      endTime: row?.endTime,
      room: row?.room,
      weekday: row?.weekday,
    };
    newRequest[field] = Number(newValue?.label);
    
    onSaveTimeSlot(newRequest);
  };
  
  return (
    <Autocomplete
      disablePortal
      value={value !== undefined ? { label: value?.toString() || "" } : null}
      isOptionEqualToValue={(option, value) =>
        option.label.toString() === value.label
      }
      onChange={handleAutoCompleteChange}
      options={options}
      sx={{ width: width }}
      getOptionLabel={(option) => option.label || ""}
      renderInput={(params) => <TextField {...params} />}
    />
  );
};

export default AutocompleteCell;
