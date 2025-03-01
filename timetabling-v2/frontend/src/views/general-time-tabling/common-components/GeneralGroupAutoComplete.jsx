import { Autocomplete, TextField } from "@mui/material";
import { useGroupData } from "services/useGroupData";

const GeneralGroupAutoComplete = ({ selectedGroup, setSelectedGroup }) => {
  const { isLoading, error, allGroups } = useGroupData();

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
      options={allGroups}
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

export default GeneralGroupAutoComplete;
