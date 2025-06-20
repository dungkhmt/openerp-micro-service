import { useState } from "react";
import { Button, Menu, MenuItem, Typography, Divider } from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";

const SortButton = ({
  sortFields,
  onSort,
  defaultSortField,
  defaultSortDirection,
}) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedSortField, setSelectedSortField] = useState(
    defaultSortField || sortFields[0]?.key
  );
  const [selectedSortDirection, setSelectedSortDirection] = useState(
    defaultSortDirection || "asc"
  );

  const selectedFieldLabel =
    sortFields.find((field) => field.key === selectedSortField)?.label || "Sắp xếp";

  const handleOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleSort = (sortField, sortDirection) => {
    setSelectedSortField(sortField);
    setSelectedSortDirection(sortDirection);
    onSort(sortField, sortDirection);
    handleClose();
  };

  return (
    <>
      <Button
        variant="outlined"
        startIcon={
          <Icon
            icon={
              selectedSortDirection === "asc"
                ? "gravity-ui:bars-descending-align-left-arrow-up"
                : "gravity-ui:bars-descending-align-left-arrow-down"
            }
            width={18}
            height={18}
          />
        }
        onClick={handleOpen}
        sx={{
          textTransform: "none",
          color: "text.primary",
          borderColor: "grey.300",
          "&:hover": { borderColor: "grey.500" },
        }}
      >
        {selectedFieldLabel}
      </Button>

      {/* Sort Fields */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        PaperProps={{
          sx: {
            backgroundColor: "grey.50",
            color: "grey.700",
            minWidth: 200,
            px: 2,
            borderRadius: 2,
          },
        }}
      >
        {sortFields.map((sortField) => (
          <MenuItem
            key={sortField.key}
            onClick={() => handleSort(sortField.key, selectedSortDirection)}
            sx={{
              "&:hover": { backgroundColor: "grey.300" },
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              px: 2,
            }}
          >
            <Typography>{sortField.label}</Typography>
            {selectedSortField === sortField.key && (
              <Icon icon="mdi:check" width={20} height={20} />
            )}
          </MenuItem>
        ))}

        <Divider sx={{ my: 0.5, mx: 1 }} />

        {/* Sort Directions */}
        <MenuItem
          onClick={() => handleSort(selectedSortField, "asc")}
          sx={{
            "&:hover": { backgroundColor: "grey.300" },
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            px: 2,
          }}
        >
          <Typography>Tăng dần</Typography>
          {selectedSortDirection === "asc" && (
            <Icon icon="mdi:check" width={20} height={20} />
          )}
        </MenuItem>
        <MenuItem
          onClick={() => handleSort(selectedSortField, "desc")}
          sx={{
            "&:hover": { backgroundColor: "grey.300" },
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            px: 2,
          }}
        >
          <Typography>Giảm dần</Typography>
          {selectedSortDirection === "desc" && (
            <Icon icon="mdi:check" width={20} height={20} />
          )}
        </MenuItem>
      </Menu>
    </>
  );
};

SortButton.propTypes = {
  sortFields: PropTypes.arrayOf(PropTypes.object).isRequired,
  onSort: PropTypes.func.isRequired,
  defaultSortField: PropTypes.string,
  defaultSortDirection: PropTypes.oneOf(["asc", "desc"]),
};

SortButton.defaultProps = {
  defaultSortField: null,
  defaultSortDirection: "asc",
};

export default SortButton;
