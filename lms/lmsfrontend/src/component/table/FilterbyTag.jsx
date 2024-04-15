import React, { useState, useCallback, useEffect } from "react";
import Checkbox from '@mui/material/Checkbox';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import { BASE_URL, request } from "api";

const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

const FilterbyTag = (props) => {
  const [definedTags, setdefinedTags] = useState([])

  const getTags = useCallback((path, setData) => {
    request("get", path, (res) => {
      // let sortedTag = res.data 
      // sortedTag.sort((a, b) => {
      //   const nameA = a.name.toUpperCase(); 
      //   const nameB = b.name.toUpperCase(); 
      //   if (nameA < nameB) {
      //     return -1;
      //   }
      //   if (nameA > nameB) {
      //     return 1;
      //   }
      
      //   // names must be equal
      //   return 0;
      // })
      
      // // list to object
      
      // console.log("Tags", sortedTag)

      setData(res.data);
    }).then();
  }, []);

  useEffect(() => {
      getTags("/tags/", (data) => {
      setdefinedTags(data);
    });
  }, [getTags]);

  return (
    <Autocomplete
      multiple
      id="checkboxes-tags-demo"
      options={definedTags}
      disableCloseOnSelect
  
      getOptionLabel={(option) => option.name}
      renderOption={(props, option, { selected }) => (
        <li {...props}>
          <Checkbox
            icon={icon}
            checkedIcon={checkedIcon}
            style={{ marginRight: 8 }}
            checked={selected}
          />
          {option.name}
        </li>
      )}
      style={{ width: 200 }}
      renderInput={(params) => (
        <TextField {...params} label="Tags" placeholder="" />
      )}
      onChange={(event, newValue) => {
        console.log(event, newValue, definedTags)
        props.onFilterChanged(props.columnDef.tableData.id, newValue);
      }}
      limitTags={3}
    />
  );
};


export default FilterbyTag;