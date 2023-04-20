import { Box, Button, IconButton, Stack, Tooltip } from "@mui/material";
import { useState } from "react";
import { useDebounce, useMeasure } from "react-use";
import SearchBox from "../searchbox/SearchBox";
const CustomToolBar = ({ actions }) => {
  const [ref, { width }] = useMeasure();
  const [keyword, setKeyword] = useState("");
  const [,] = useDebounce(
    () => {
      console.log(keyword);
    },
    1000,
    [keyword]
  );

  return (
    <Box ref={ref} sx={{ paddingY: 2 }}>
      <Stack direction={"row"} spacing={2}>
        <SearchBox value={keyword} setValue={setKeyword} />
        <Stack
          direction="row"
          spacing={width < 900 ? 0 : 2}
          alignItems="center"
        >
          {actions?.map((action, index) =>
            width < 900 ? (
              <IconButton
                size="small"
                key={index}
                disabled={action.disabled}
                color={"secondary"}
                onClick={action.type !== "type" ? action.callback : undefined}
              >
                <Tooltip title={action.describe}>
                  <>
                    {action.type === "file" ? (
                      <input
                        hidden
                        multiple
                        type="file"
                        accept="*"
                        onChange={(e) => action.callback(e)}
                      />
                    ) : null}
                    {action.icon}
                  </>
                </Tooltip>
              </IconButton>
            ) : (
              <Button
                key={index}
                component="label"
                disabled={action.disabled}
                startIcon={action.icon}
                variant="contained"
                color={"secondary"}
                onClick={action.type !== "type" ? action.callback : undefined}
                sx={{
                  fontSize: 12,
                  textTransform: "uppercase",
                  fontWeight: 600,
                  boxShadow: 0,
                }}
              >
                <Tooltip title={action.describe}>
                  <>
                    {action.type === "file" ? (
                      <input
                        hidden
                        multiple
                        type="file"
                        accept="*"
                        onChange={(e) => action.callback(e)}
                      />
                    ) : null}
                    {action.title ? action.title : null}
                  </>
                </Tooltip>
              </Button>
            )
          )}
        </Stack>
      </Stack>
    </Box>
  );
};
export default CustomToolBar;
