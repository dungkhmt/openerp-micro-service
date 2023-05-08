import React from 'react';
import {FormControl, FormLabel, MenuItem, Select} from "@mui/material";

export default function SelectItem(props) {

  return (
    <FormControl style={props.style}
                 disabled={props.disabled}>
      <FormLabel>{props.label}</FormLabel>
      <Select
        value={props.value}
        multiple={props.multiple}
        onChange={event => props.onChange(event.target.value)}
      >
        { props.options.map(option => (
          <MenuItem value={option.value} key={option.value}>
            { option.label }
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}