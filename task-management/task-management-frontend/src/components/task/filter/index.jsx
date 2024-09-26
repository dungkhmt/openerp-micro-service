import { Icon } from "@iconify/react";
import { Button, Menu, Typography } from "@mui/material";
import { Box } from "@mui/system";
import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import { FilterGroup } from "./FilterGroup";
import { FilterWrapper } from "./FilterWrapper";

const TaskFilter = (props) => {
  const {
    anchorEl,
    onClose,
    title = "Bộ lọc",
    initFilter,
    filterList,
    onFilter,
  } = props;
  const [filter, setFilter] = useState(
    initFilter ?? {
      condition: "AND",
      items: [],
    }
  );

  const handleFilter = () => {
    onFilter?.(filter);
    onClose();
  };

  const handleClose = () => {
    setFilter(initFilter);
    onClose();
  };

  const handleAddFilter = () => {
    const newId = filter.items?.length ?? 0;
    setFilter({
      ...filter,
      items: [
        ...filter.items,
        {
          id: newId,
          condition: "AND",
          items: [
            {
              id: 0,
              field: "",
              operator: "",
              value: [],
            },
          ],
        },
      ],
    });
  };

  useEffect(() => {
    setFilter(initFilter);
  }, [initFilter]);

  const handleUpdateItem = (id, newItem) => {
    const newFilters = {
      ...filter,
      items: filter.items.map((filter) =>
        filter.id === id ? newItem : filter
      ),
    };
    setFilter(newFilters);
  };

  const handleDeleteItem = (id) => {
    const newFilters = {
      ...filter,
      // delete item and re-index
      items: filter.items
        .filter((filter) => filter.id !== id)
        .map((filter, index) => ({ ...filter, id: index })),
    };
    setFilter(newFilters);
  };

  return (
    <Menu
      keepMounted
      elevation={0}
      anchorEl={anchorEl}
      id="customized-menu"
      onClose={handleClose}
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
        paper: { sx: { backgroundColor: "transparent" } },
      }}
      sx={{
        mt: 2,
        "& .MuiList-root": {
          padding: 0,
        },
      }}
    >
      <FilterWrapper>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Typography variant="subtitle2" sx={{ p: 2 }}>
            {title}
          </Typography>
          <Box className="action">
            <Button
              variant="text"
              sx={{ color: (theme) => theme.palette.common.black }}
              onClick={() =>
                setFilter({
                  condition: "AND",
                  items: [],
                })
              }
            >
              Xóa tất cả
            </Button>
            <Button variant="contained" onClick={handleFilter}>
              Áp dụng
            </Button>
          </Box>
        </Box>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          {filter.items?.map((f) => (
            <FilterGroup
              key={f.id}
              group={f}
              condition={filter.condition}
              hasMore={filter.items?.length > 1}
              deleteGroup={handleDeleteItem}
              updateGroup={handleUpdateItem}
              updateCondition={(condition) =>
                setFilter({ ...filter, condition })
              }
              filterList={filterList}
            />
          ))}
        </Box>
        <Box>
          <Button
            className="btn_add-filter"
            variant="outlined"
            startIcon={<Icon icon="ic:outline-add" />}
            onClick={handleAddFilter}
          >
            Thêm filter
          </Button>
        </Box>
      </FilterWrapper>
    </Menu>
  );
};

TaskFilter.propTypes = {
  title: PropTypes.string,
  anchorEl: PropTypes.any,
  onClose: PropTypes.func,
  initFilter: PropTypes.object,
  filterList: PropTypes.arrayOf(PropTypes.object),
  onFilter: PropTypes.func,
};

export { TaskFilter };
