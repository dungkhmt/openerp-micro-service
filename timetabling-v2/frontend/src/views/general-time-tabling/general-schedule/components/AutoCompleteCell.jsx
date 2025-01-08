import { Autocomplete, TextField } from "@mui/material";

const AutocompleteCell = ({
  row,
  saveRequests,
  setSaveRequests,
  roomReservationId,
  id,
  field,
  value,
  api,
  options,
  width,
  setClasses,
  setLoading,
  semester,
}) => {

  const handleAutoCompleteChangeV2 = (event, newValue) => {
    const newRequest = {
      roomReservationId: row?.roomReservationId,
      startTime: row?.startTime,
      endTime: row?.endTime,
      room: row?.room,
      weekday: row?.weekday,
    };
    newRequest[field] = Number(newValue?.label);
    const updatedRequests = [...saveRequests]; // Create a copy of saveRequests
    
    // Check if a request with the same roomReservationId already exists
    const existingIndex = updatedRequests.findIndex(request => request.roomReservationId === newRequest.roomReservationId);
  
    if (existingIndex !== -1) {
      // If it exists, update the corresponding field
      updatedRequests[existingIndex][field] = Number(newValue?.label);
    } else {
      // If it doesn't exist, push the newRequest to the array
      updatedRequests.push(newRequest);
    }
  
    // Set the updated saveRequests state
    setSaveRequests(updatedRequests);
    setClasses(prevClasses => {
      const existingIndex = prevClasses.findIndex(request => request.roomReservationId === newRequest.roomReservationId);
      prevClasses[existingIndex][field] = newValue?.label;
      return prevClasses;
    })
    console.log(row)
    console.log(saveRequests)

  };
  
  return (
    <Autocomplete
      disablePortal
      value={value !== undefined ? { label: value?.toString() || "" } : null}
      isOptionEqualToValue={(option, value) =>
        option.label.toString() === value.label
      }
      onChange={handleAutoCompleteChangeV2}
      options={options}
      sx={{ width: width }}
      getOptionLabel={(option) => option.label || ""}
      renderInput={(params) => <TextField variant='standard' {...params} />}
    />
  );
};

export default AutocompleteCell;
