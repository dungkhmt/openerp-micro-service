import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';

export default function BasicSelect({items, label, value, onChange, disabled}) {
  return (
    <Box sx={{ minWidth: 120 }}>
      <FormControl fullWidth size="small">
        <InputLabel id="demo-simple-select-label">{label}</InputLabel>
        <Select
          disabled={disabled||false}
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={value||''}
          label="Age"
          onChange={onChange}
        >
          {items?.map((item, index) => {
            const {id, ...rest} = item;
            return (
                <MenuItem key={index} value={id}>{rest[Object.keys(rest)[0]]}</MenuItem>   
            )
            })}
        </Select>
      </FormControl>
    </Box>
  );
}
