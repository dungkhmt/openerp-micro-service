import * as React from 'react';
import TextField from '@mui/material/TextField';
import {AdapterDateFns} from '@mui/x-date-pickers/AdapterDateFns';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {DateTimePicker} from '@mui/x-date-pickers/DateTimePicker';

export default function DateTimePickerBasic({ label, onChange, value }) {
    return (
        <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DateTimePicker
                renderInput={(props) => <TextField {...props} />}
                label={label}
                value={value}
                onChange={onChange}
                sx={{ backgroundColor: "#fff" }}
            />
        </LocalizationProvider>
    );
}
