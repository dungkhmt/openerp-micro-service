import { Autocomplete, TextField } from "@mui/material";
import { useGroups } from "../hooks/useGroups";

const FirstYearGroupAutoComplete = ({ selectedGroup, setSelectedGroup }) => {
  const { groups } = useGroups();
  
  return (
    <Autocomplete
      disablePortal
      loadingText="Loading..."
      getOptionLabel={(option) => option && option.groupName}
      onChange={(e, group) => {
        console.log(group);
        setSelectedGroup(group);
      }}
      value={selectedGroup}
      options={groups}
      sx={{ width: 200 }}
      renderInput={(params) => <TextField {...params} label="Chọn nhóm" />}
      PopperProps={{
        popperOptions: {
          modifiers: [
            {
              name: "preventOverflow",
              enabled: true,
              options: {
                altAxis: true,
              },
            },
          ],
        },
      }}
    />
  );
};

export default FirstYearGroupAutoComplete;
