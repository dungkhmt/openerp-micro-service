import { ThemeProvider, createTheme } from "@mui/material";
import { grey } from "@mui/material/colors";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";

const dateTimeInputStyles = {
    root: (theme) => ({
      margin: 0,
      height: theme.spacing(8),
      padding: theme.spacing(2),
      "& .MuiOutlinedInput-root": {
        height: 40,
        width: "100%"
      }
    }),
    closeButton: (theme) => ({
      width: 40,
      height: 40,
      position: "absolute",
      top: theme.spacing(1.5),
      right: theme.spacing(2),
      color: "rgba(0, 0, 0, 0.5)",
      background: grey[300],
      "&:hover": {
        background: grey[400],
      },
    }),
  };
const DateTimeInput = (props) => {
    const {onChange, sx} = props;
    return (
        
        <LocalizationProvider dateAdapter={AdapterDayjs}
        sx={(theme) => ({
            ...dateTimeInputStyles.root(theme),
            ...(sx ? sx(theme) : {}),
          })}
        >
            <DemoContainer components={['DateTimePicker']}>
                <DateTimePicker label=""
                    onChange={onChange} 
                    />
            </DemoContainer>
        </LocalizationProvider>
    );
};

export default DateTimeInput;