import * as React from "react";
import { request } from "api";
import { toast } from "react-toastify";
import { Autocomplete, Box, CircularProgress, TextField } from "@mui/material";

export default function ClassroomAutoCompleteCell({
  row,
  onSaveTimeSlot,
  groupName,
  maxQuantity,
  value,
}) {
  const [open, setOpen] = React.useState(false);
  const [options, setOptions] = React.useState([]);
  const [fetchLoading, setFetchLoading] = React.useState(false);
  const [currentValue, setCurrentValue] = React.useState(
    value ? { classroom: value, quantityMax: "" } : null
  );

  React.useEffect(() => {
    setCurrentValue(value ? { classroom: value, quantityMax: "" } : null);
  }, [value]);

  const handleChange = (event, newValue) => {
    setCurrentValue(newValue);
    const newRequest = {
      roomReservationId: row?.roomReservationId,
      startTime: row?.startTime,
      endTime: row?.endTime,
      room: newValue?.classroom,
      weekday: row?.weekday,
    };
    onSaveTimeSlot(newRequest);
  };

  React.useEffect(() => {
    if (!open) {
      setOptions([]);
      return;
    }

    setFetchLoading(true);
    request(
      "post",
      "/classroom/",
      (res) => {
        const newOptions = currentValue && !res.data.find(opt => opt.classroom === currentValue.classroom)
          ? [currentValue, ...res.data]
          : res.data;
        setOptions(newOptions);
        setFetchLoading(false);
      },
      (err) => {
        setFetchLoading(false);
        toast.error(err.response.data);
      },
      {
        groupName: groupName || null,
        maxAmount: maxQuantity,
      }
    );
  }, [open, groupName, maxQuantity, currentValue?.classroom]);

  return (
    <Autocomplete
      id="classroom-autocell"
      sx={{ width: 400 }}
      open={open}
      onOpen={() => setOpen(true)}
      onClose={() => setOpen(false)}
      value={currentValue}
      options={options}
      getOptionLabel={(option) => option?.classroom || ""}
      isOptionEqualToValue={(option, value) => 
        option?.classroom === value?.classroom
      }
      loading={fetchLoading}
      onChange={handleChange}
      renderOption={(props, option) => (
        <Box component="li" {...props}>
          {option.classroom} 
          {option.quantityMax ? ` (SL: ${option.quantityMax})` : ''}
        </Box>
      )}
      renderInput={(params) => (
        <TextField
          sx={{ width: 200 }}
          {...params}
          InputProps={{
            ...params.InputProps,
            endAdornment: (
              <React.Fragment>
                {fetchLoading ? <CircularProgress color="inherit" size={20} /> : null}
                {params.InputProps.endAdornment}
              </React.Fragment>
            ),
          }}
        />
      )}
    />
  );
}
