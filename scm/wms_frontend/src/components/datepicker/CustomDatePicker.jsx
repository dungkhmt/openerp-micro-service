import { TextField } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterMoment } from "@mui/x-date-pickers/AdapterMoment";
import moment from "moment";
moment.updateLocale("en", {
  weekdays: ["CN", "T2", "T3", "T4", "T5", "T6", "T7"],
  weekdaysShort: ["CN", "T2", "T3", "T4", "T5", "T6", "T7"],
  months: [
    "Tháng 1",
    "Tháng 2",
    "Tháng 3",
    "Tháng 4",
    "Tháng 5",
    "Tháng 6",
    "Tháng 7",
    "Tháng 8",
    "Tháng 9",
    "Tháng 10",
    "Tháng 11",
    "Tháng 12",
  ],
});

export const CustomDatePicker = (props) => {
  const { value, onChange, label, error, message, minDate, view } = props;
  return (
    <LocalizationProvider
      adapterLocale={moment.locale("en")}
      dateAdapter={AdapterMoment}
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
