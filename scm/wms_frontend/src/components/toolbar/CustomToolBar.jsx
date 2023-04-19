import { Box, IconButton, Stack, Tooltip } from "@mui/material";
import { useState } from "react";
import { useDebounce } from "react-use";
import SearchBox from "../searchbox/SearchBox";

const CustomToolBar = () => {
  const [keyword, setKeyword] = useState("");
  const [,] = useDebounce(
    () => {
      console.log(keyword);
    },
    1000,
    [keyword]
  );
  return (
    <Box>
      <Stack direction={"row"} spacing={2}>
        <SearchBox value={keyword} setValue={setKeyword} />
        <IconButton>
          <Tooltip>
            <></>
          </Tooltip>
        </IconButton>
      </Stack>
    </Box>
  );
};
export default CustomToolBar;
