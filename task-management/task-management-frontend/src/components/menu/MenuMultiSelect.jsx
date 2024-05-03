import { Icon } from "@iconify/react";
import {
  Box,
  InputAdornment,
  ListItemIcon,
  Menu,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";
import PropTypes from "prop-types";
import { useState } from "react";

const MenuMultiSelect = ({
  anchorEl,
  onClose,
  options,
  onAddItem,
  onDeleteItem,
  onSelectAll,
  onDeselectAll,
  selectedItems,
  renderItem,
  searchable = true,
}) => {
  const [filteredOptions, setFilteredOptions] = useState(options);

  const handleSearch = (e) => {
    // filter options
    const value = e.target.value.toLowerCase();

    const newOptions = options.filter((option) => {
      return (
        option.label.toLowerCase().includes(value) ||
        option.id.toLowerCase().includes(value)
      );
    });

    setFilteredOptions(newOptions);
  };

  const isSelectAll = selectedItems.length === options.length;

  const handleItemClick = (item, isSelected) => {
    isSelected ? onDeleteItem(item) : onAddItem(item);
  };

  return (
    <Menu
      keepMounted
      elevation={0}
      anchorEl={anchorEl}
      onClose={onClose}
      open={Boolean(anchorEl)}
      anchorOrigin={{
        vertical: "bottom",
        horizontal: "center",
      }}
      transformOrigin={{
        vertical: "top",
        horizontal: "center",
      }}
      slotProps={{
        paper: {
          sx: {
            border: (theme) => `1px solid ${theme.palette.divider}`,
            borderRadius: "8px",
            boxShadow: 4,
          },
        },
      }}
    >
      <Box sx={{ p: 1 }}>
        {searchable && (
          <TextField
            fullWidth
            variant="standard"
            placeholder="Tìm kiếm..."
            size="small"
            onChange={handleSearch}
            autoFocus
            inputProps={{ style: { border: "none" } }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Icon icon="material-symbols:search" />
                </InputAdornment>
              ),
            }}
          />
        )}
        {filteredOptions.map((option) => {
          const isSelected = selectedItems.some(
            (selectedItem) => selectedItem === option.id
          );
          return (
            <MenuItem
              key={option.id}
              onClick={() => handleItemClick(option, isSelected)}
              selected={isSelected}
              sx={{
                borderRadius: "4px",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                width: "100%",
                gap: 1,
              }}
            >
              {renderItem ? (
                renderItem(option, true)
              ) : (
                <Typography sx={{ flex: 1 }}>{option.label}</Typography>
              )}
              {isSelected && (
                <ListItemIcon>
                  <Icon icon="ic:baseline-check" />
                </ListItemIcon>
              )}
            </MenuItem>
          );
        })}
        {filteredOptions.length === 0 ? (
          <MenuItem>
            <Typography>Không tìm thấy</Typography>
          </MenuItem>
        ) : (
          <Typography
            variant="body2"
            onClick={isSelectAll ? onDeselectAll : onSelectAll}
            sx={{
              textTransform: "none",
              color: "primary.main",
              cursor: "pointer",
              textAlign: "center",
              mt: 2,
              "&:hover": {
                fontWeight: 600,
              },
            }}
          >
            {isSelectAll ? "Bỏ chọn tất cả" : "Chọn tất cả"}
          </Typography>
        )}
      </Box>
    </Menu>
  );
};

MenuMultiSelect.propTypes = {
  anchorEl: PropTypes.any,
  onClose: PropTypes.func,
  options: PropTypes.array,
  selectedItems: PropTypes.array,
  searchable: PropTypes.bool,
  onAddItem: PropTypes.func,
  onDeleteItem: PropTypes.func,
  onSelectAll: PropTypes.func,
  onDeselectAll: PropTypes.func,
  renderItem: PropTypes.func,
};

export { MenuMultiSelect };
