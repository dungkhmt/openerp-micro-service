import { Autocomplete, TextField } from "@mui/material";
import { request } from "api";
import { useState } from "react";
import { toast } from "react-toastify";

const AutocompleteCell = ({
  id,
  field,
  value,
  api,
  options,
  width,
  setClasses,
  setLoading
}) => {
  const handleAutocompleteChange = (event, newValue) => {
    
    // Call the api that update the general class with id = ?
    const generalClassId = String(id).split("-")?.[0];
    setLoading(true);
    switch (String(field)) {
      case "startTime":
      case "room":
      case "weekday":
        const scheduleIndex = String(id).split("-")?.[1];
        request(
          "post",
          `/general-classes/update-class-schedule`,
          (res) => {
            setLoading(false);
            setClasses((prevClasses) => {
              return prevClasses.map((gc) => {
                console.log(gc);
                if (id == gc.id) {
                  return { ...gc, [field]: newValue?.label };
                } else {
                  return gc;
                }
              });
            });
          },
          (error) => {
            toast.error(error.response.data);
            console.error(error);
            setLoading(false);
          },
          { field, value: newValue?.label, scheduleIndex, generalClassId }
        );
        break;
      default:
        request(
          "post",
          `/general-class/${generalClassId}/update-class-schedule`,
          (res) => {
            console.log(res.data);
            setClasses((prevClasses) => {
              prevClasses.map((gc) => ({
                ...gc,
                [field]: newValue,
              }));
            });
          },
          (error) => console.error(error),
          { field, value: newValue }
        );
        break;
    }
    // The api will parse the id to real-id with the schedule_index and update the 'field' with 'newValue'
    console.log(newValue);
  };

  return (
    <Autocomplete
      disablePortal
      value={value !== undefined ? { label: value.toString() } : null}
      isOptionEqualToValue={(option, value) =>
        option.label.toString() === value.label
      }
      onChange={handleAutocompleteChange}
      options={options}
      sx={{ width: width }}
      getOptionLabel={(option) => option.label || ""}
      renderInput={(params) => <TextField {...params} />}
    />
  );
};

export default AutocompleteCell;
