import TuneIcon from "@mui/icons-material/Tune";
import {
  Box,
  Button,
  IconButton,
  Popover,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { useDebounce, useMeasure } from "react-use";
import CustomFilter from "../filter/CustomFilter";
import SearchBox from "../searchbox/SearchBox";
const CustomToolBar = ({
  actions,
  containSearch = true,
  containFilter = true,
  fields,
  resolver = null,
  defaultValues = {},
  onSubmit = () => {},
  onSearch = () => {},
}) => {
  const methods = useForm({
    mode: "onChange",
    defaultValues: defaultValues,
    resolver: resolver,
  });
  const {
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = methods;

  const [ref, { width }] = useMeasure();
  const [keyword, setKeyword] = useState("");
  const [,] = useDebounce(
    () => {
      onSearch(keyword);
    },
    1000,
    [keyword]
  );

  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;

  return (
    <Box ref={ref} sx={{ paddingY: 2 }}>
      <Stack direction={"row-reverse"} spacing={2}>
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
                color={action?.color ? action?.color : "secondary"}
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
                color={action?.color ? action?.color : "secondary"}
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
                    <Typography
                      sx={{
                        fontSize: 14,
                        fontWeight: "bold",
                        color: "white",
                      }}
                    >
                      {action.title ? action.title : null}
                    </Typography>
                  </>
                </Tooltip>
              </Button>
            )
          )}
        </Stack>
      </Stack>
      <Stack
        direction={"row"}
        justifyContent={"space-between"}
        alignItems={"center"}
        sx={{ marginTop: 2 }}
      >
        {containSearch && (
          <Box>
            <SearchBox value={keyword} setValue={setKeyword} />
          </Box>
        )}
        {containFilter && (
          <Box>
            {width < 900 ? (
              <IconButton
                size="small"
                color="primary"
                aria-describedby={id}
                variant="contained"
                onClick={handleClick}
              >
                <TuneIcon />
              </IconButton>
            ) : (
              <Button
                component="label"
                startIcon={<TuneIcon />}
                variant="contained"
                color={"primary"}
                onClick={handleClick}
                sx={{
                  fontSize: 12,
                  textTransform: "uppercase",
                  fontWeight: 600,
                  boxShadow: 0,
                  color: "white",
                }}
              >
                <Tooltip title={"Filter"}>
                  <>
                    <Typography
                      sx={{
                        fontSize: 14,
                        fontWeight: "bold",
                        color: "white",
                      }}
                    >
                      {"Filter"}
                    </Typography>
                  </>
                </Tooltip>
              </Button>
            )}
            <Popover
              id={id}
              open={open}
              anchorEl={anchorEl}
              onClose={handleClose}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "left",
              }}
            >
              <Box sx={{ width: 300, padding: 2 }}>
                <CustomFilter
                  fields={fields ? fields : []}
                  control={control}
                  errors={errors}
                />
                <Stack direction="row" justifyContent={"flex-end"}>
                  <Button
                    onClick={handleSubmit(onSubmit)}
                    variant="contained"
                    style={{ color: "white" }}
                  >
                    Submit
                  </Button>
                </Stack>
              </Box>
            </Popover>
          </Box>
        )}
      </Stack>
    </Box>
  );
};
export default CustomToolBar;
