import { Icon } from "@iconify/react";
import {
  Box,
  ListItemIcon,
  Menu,
  MenuItem,
  TextField,
  Typography,
  InputAdornment,
} from "@mui/material";
import PropTypes from "prop-types";
import { useState } from "react";

const MenuSingleSelect = ({
  anchorEl,
  onClose,
  options,
  onItemClick,
  selectedItem,
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

  const handleItemClick = (item) => {
    onItemClick(item);
    onClose();
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
        {filteredOptions.map((option) => (
          <MenuItem
            key={option.id}
            onClick={() => handleItemClick(option)}
            selected={option.id === selectedItem.id}
            sx={{ borderRadius: "4px" }}
          >
            {option.renderItem ? (
              option.renderItem(option)
            ) : (
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  width: "100%",
                  gap: 1,
                }}
              >
                <Typography sx={{ flex: 1 }}>{option.label}</Typography>
                {option.id === selectedItem.id && (
                  <ListItemIcon>
                    <Icon icon="ic:baseline-check" />
                  </ListItemIcon>
                )}
              </Box>
            )}
          </MenuItem>
        ))}
        {filteredOptions.length === 0 && (
          <MenuItem>
            <Typography>Không tìm thấy</Typography>
          </MenuItem>
        )}
      </Box>
    </Menu>
  );
};

MenuSingleSelect.propTypes = {
  anchorEl: PropTypes.object,
  onClose: PropTypes.func,
  options: PropTypes.array,
  onItemClick: PropTypes.func,
  selectedItem: PropTypes.object,
  searchable: PropTypes.bool,
};

export { MenuSingleSelect };
