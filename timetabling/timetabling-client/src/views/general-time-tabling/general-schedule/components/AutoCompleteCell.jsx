import { Autocomplete, TextField } from "@mui/material";
import { request } from "api";
import { toast } from "react-toastify";

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
  const handleAutocompleteChangeV1 = (event, newValue) => {
    // Call the api that update the general class with id = ?
    const generalClassId = String(id).split("-")?.[0];
    setLoading(true);
    switch (String(field)) {
      case "startTime":
      case "endTime":
      case "room":
      case "weekday":
        const scheduleIndex = String(id).split("-")?.[1];
        request(
          "post",
          `/general-classes/update-class-schedule?semester=${semester?.semester}`,
          (res) => {
            setLoading(false);
            const newClass = res.data;
            const newTimeSlot = res.data.timeSlots.filter(
              (timeSlot) => timeSlot.id === roomReservationId
            )?.[0];
            delete newClass.timeSlots;

            const a = {
              ...newClass,
              ...newTimeSlot,
              roomReservationId: newTimeSlot.id,
              id: `${newClass.id}-${scheduleIndex}`,
            };

            setClasses((prevClasses) => {
              return prevClasses.map((prevClass) => {
                if (prevClass.roomReservationId === roomReservationId) {
                  return a;
                } else {
                  return prevClass;
                }
              });
            });
          },
          (error) => {
            toast.error(error.response.data);
            console.error(error);
            setLoading(false);
          },
          {
            field,
            value: newValue?.label,
            scheduleIndex,
            generalClassId,
            roomReservationId,
          }
        );
        break;
      default:
        request(
          "post",
          `/general-class/${generalClassId}/update-class-schedule?semester=${semester?.semester}`,
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
      renderInput={(params) => <TextField {...params} />}
    />
  );
};

export default AutocompleteCell;
