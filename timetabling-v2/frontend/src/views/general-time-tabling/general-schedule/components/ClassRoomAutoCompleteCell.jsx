import * as React from "react";
import { request } from "api";
import { toast } from "react-toastify";
import { Autocomplete, Box, CircularProgress, TextField } from "@mui/material";

export default function ClassroomAutoCompleteCell({
  row,
  saveRequests,
  setSaveRequests,
  groupName,
  maxQuantity,
  roomReservationId,
  id,
  field,
  semester,
  setLoading,
  setClasses,
  value,
}) {
  const [open, setOpen] = React.useState(false);
  const [options, setOptions] = React.useState([]);
  const [fetchLoading, setFetchLoading] = React.useState(false);

  const handleAutocompleteChangeV2 = (event, newValue) => {
    const newRequest = {
      roomReservationId: row?.roomReservationId,
      startTime: row?.startTime,
      endTime: row?.endTime,
      room: row?.room,
      weekday: row?.weekday,
    };
    newRequest[field] = newValue?.classroom;
    const updatedRequests = [...saveRequests]; // Create a copy of saveRequests
    
    // Check if a request with the same roomReservationId already exists
    const existingIndex = updatedRequests.findIndex(request => request.roomReservationId === newRequest.roomReservationId);
  
    if (existingIndex !== -1) {
      // If it exists, update the corresponding field
      updatedRequests[existingIndex][field] = newValue?.classroom;
    } else {
      // If it doesn't exist, push the newRequest to the array
      updatedRequests.push(newRequest);
    }
  
    // Set the updated saveRequests state
    setSaveRequests(updatedRequests);
    setClasses(prevClasses => {
      const existingIndex = prevClasses.findIndex(request => request.roomReservationId === newRequest.roomReservationId);
      prevClasses[existingIndex][field] = newValue?.classroom;
      return prevClasses;
    })
    console.log(row)
    console.log(saveRequests)
  };

  React.useEffect(() => {
    console.log(groupName, maxQuantity)
    if (!open) {
      setOptions([]);
    } else {
      setFetchLoading(true);
      request(
        "post",
        "/classroom/",
        (res) => {
          setFetchLoading(false);
          setOptions(res.data);
        },
        (err) => {
          setFetchLoading(false);
          console.log(err);
          toast.error(err.response.data);
        },
        {
          groupName: groupName !== "" ? groupName : null,
          maxAmount: maxQuantity,
        }
      );
    }
  }, [open]);

  return (
    <Autocomplete
      id="classroom-autocell"
      sx={{ width: 400 }}
      open={open}
      onOpen={() => {
        setOpen(true);
      }}
      onClose={() => {
        setOpen(false);
      }}
      getOptionLabel={(option) => option.classroom}
      value={
        value !== undefined ? { classroom: value?.toString() || "" } : null
      }
      isOptionEqualToValue={(option, value) => {
        return option.classroom.toString() === value.classroom;
      }}
      renderOption={(props, option) => (
        <Box
          component="li"
          sx={{ "& > img": { mr: 2, flexShrink: 0 } }}
          {...props}
        >
          {option.classroom} (SL: {option.quantityMax})
        </Box>
      )}
      options={options}
      loading={fetchLoading}
      onChange={handleAutocompleteChangeV2}
      renderInput={(params) => (
        <TextField
        variant='standard'
          sx={{ width: 200 }}
          {...params}
          InputProps={{
            ...params.InputProps,
            endAdornment: (
              <React.Fragment>
                {fetchLoading ? (
                  <CircularProgress color="inherit" size={20} />
                ) : null}
                {params.InputProps.endAdornment}
              </React.Fragment>
            ),
          }}
        />
      )}
    />
  );
}
