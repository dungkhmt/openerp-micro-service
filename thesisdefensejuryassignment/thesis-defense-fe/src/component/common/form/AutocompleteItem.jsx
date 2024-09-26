import React, {useEffect, useState} from 'react';
import {Autocomplete, FormControl, FormLabel, TextField} from "@mui/material";
import PropTypes from "prop-types";

export default function AutocompleteItem(props) {
  const [options, setOptions] = useState([]);

  useEffect(() => getOptions(props.defaultSearch), []);

  async function getOptions(search) {
    let options = await props.optionsRetriever(search);
    setOptions(options);
  }

  function onChange(selected) {
    if (!selected || selected.length === 0) {
      props.onChange(selected);
    } else {
      let newValue = props.multiple ? selected.map(option => option.value) : selected.value;
      props.onChange(newValue)
    }
  }
  
  return (
    <FormControl style={props.style}>
      <FormLabel>{props.label}</FormLabel>
      <Autocomplete
        options={options}
        clearOnBlur={false}
        multiple={props.multiple}
        isOptionEqualToValue={(option1, option2) => (option1.value = option2.value)}
        renderInput={ (params) => (
          <TextField {...params} placeholder={props.placeholder}/>
        )}
        onChange={(_, selected) => onChange(selected)}
        onInputChange={(_, search, reason) => {
          if (reason === 'input' || reason === "clear") {
            getOptions(search)
          }
        }}
      />
    </FormControl>
  );
}

AutocompleteItem.propTypes = {
  optionsRetriever: PropTypes.func.isRequired,
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string,
  placeholder: PropTypes.string,
  defaultSearch: PropTypes.string,
  multiple: PropTypes.bool
}

Autocomplete.defaultProps = {
  defaultSearch: "",
  multiple: false
}