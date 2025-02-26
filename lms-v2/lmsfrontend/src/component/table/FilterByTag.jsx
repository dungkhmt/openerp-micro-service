import React, {useCallback, useEffect, useState} from "react";
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import {request} from "api";
import {PopperComponent} from "../education/programmingcontestFE/AddMember2Contest";
import {autocompleteClasses} from "@mui/material";
import {useTranslation} from "react-i18next";

const FilterByTag = (props) => {
  const {t} = useTranslation("education/programmingcontest/problem");
  const [definedTags, setDefinedTags] = useState([])

  const getTags = useCallback((path, setData) => {
    request("get", path, (res) => {
      setData(res.data);
    });
  }, []);

  useEffect(() => {
    getTags("/tags", (data) => {
      setDefinedTags(data);
    });
  }, [getTags]);

  return (
    <Autocomplete
      id="checkboxes-tags-demo"
      multiple
      size="small"
      PopperComponent={PopperComponent}
      getOptionLabel={(option) => option.name}
      // filterOptions={(x) => x} // disable filtering on client
      options={props.tags || definedTags}
      noOptionsText="No matches found"
      isOptionEqualToValue={(option, value) => option.tagId === value.tagId}
      onChange={(event, newValue) => {
        props.onSelect(newValue);
      }}
      renderInput={(params) => (
        <TextField {...params}
                   label={t("tag")}
                   inputProps={{
                     ...params.inputProps,
                     autoComplete: "new-password", // disable autocomplete and autofill
                   }}
        />
      )}
      value={props.value}
      disableCloseOnSelect
      renderOption={(props, option, {selected}) => {
        const {key, ...optionProps} = props;
        return <li key={key} {...optionProps}>
          {option.name}
        </li>
      }
      }
      ListboxProps={{
        sx: {
          [`& .${autocompleteClasses.option}`]: {
            margin: '2px 0px',
            padding: '6px 8px !important'
          },
          [`& .${autocompleteClasses.option}[aria-selected='true'], & .${autocompleteClasses.option}[aria-selected='true']:hover`]: {
            color: "#ffffff",
            backgroundColor: "#1976d2", // updated backgroundColor
            "&.Mui-focused": {background: "#1976d2"},
          },
        },
      }}
      limitTags={props.limitTags || 1}
    />
  );
};

export default FilterByTag;