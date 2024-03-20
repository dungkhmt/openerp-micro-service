import Box from "@mui/material/Box";
import IconButton from "@mui/material/IconButton";
import TextField from "@mui/material/TextField";
import { GridToolbarExport } from "@mui/x-data-grid";
import PropsTypes from "prop-types";
import Icon from "../icon";

const TableToolbar = (props) => {
  return (
    <Box
      sx={{
        gap: 2,
        display: "flex",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "space-between",
        p: (theme) => theme.spacing(2, 2, 2, 2),
      }}
    >
      <GridToolbarExport printOptions={{ disableToolbarButton: true }} />
      <TextField
        size="small"
        value={props.value}
        onChange={props.onChange}
        placeholder="Search…"
        InputProps={{
          startAdornment: (
            <Box sx={{ mr: 2, display: "flex" }}>
              <Icon icon="mdi:magnify" fontSize={20} />
            </Box>
          ),
          endAdornment: (
            <IconButton
              size="small"
              title="Clear"
              aria-label="Clear"
              onClick={props.clearSearch}
            >
              <Icon icon="mdi:close" fontSize={20} />
            </IconButton>
          ),
        }}
        sx={{
          width: {
            xs: 1,
            sm: "auto",
          },
          "& .MuiInputBase-root > svg": {
            mr: 2,
          },
        }}
      />
    </Box>
  );
};

TableToolbar.propTypes = {
  value: PropsTypes.string,
  onChange: PropsTypes.func,
  clearSearch: PropsTypes.func,
};

export default TableToolbar;
