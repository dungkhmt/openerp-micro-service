import { DatePicker, LocalizationProvider } from "@mui/lab";
import { TextField } from "@mui/material";
import moment from "moment";

export const CustomDatePicker = (props) => {
  const { value, onChange, label, error, message, minDate, view } = props;
  return (
    <LocalizationProvider
      adapterLocale={moment.locale("en")}
      // dateAdapter={AdapterDateFns}
    >
      <DatePicker
        views={view ? view : null}
        label={label}
        value={value}
        onChange={onChange}
        dayOfWeekFormatter={(day) => {
          return `${day}`;
        }}
        minDate={minDate}
        renderInput={(dates) => (
          <TextField
            sx={{ mb: 2 }}
            variant="outlined"
            error={error}
            size="small"
            helperText={message ? message : ""}
            {...dates}
            InputLabelProps={{
              shrink: true,
            }}
          />
        )}
      />
    </LocalizationProvider>
  );
};
