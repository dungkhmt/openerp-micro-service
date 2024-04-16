import { Icon } from "@iconify/react";
import { Box, Button, styled } from "@mui/material";
import { useMemo, useState, useCallback } from "react";
import { useSelector, useDispatch } from "react-redux";
import { TaskFilter } from "../../../components/task/filter";
import { buildTaskFilter } from "../../../components/task/filter/task-filter";
import {
  clearCache,
  resetPagination,
  setFilters,
} from "../../../store/project/tasks";

const FilterButton = styled(Button)(({ theme }) => ({
  blockSize: "26px",
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: "15px",
  padding: theme.spacing(0, 1.5),
  fontSize: "12px",
  color: theme.palette.text.secondary,
  textTransform: "none",
  position: "relative",

  "& .MuiButton-startIcon": {
    marginRight: "2px",
  },

  "& svg": {
    fontSize: "16px !important",
    marginInlineStart: "4px",
  },

  "&:hover": {
    backgroundColor: theme.palette.action.hover,
    border: `1px solid ${theme.palette.divider}`,
  },
}));

const Filter = () => {
  const { status, priority, category, project, tasks } = useSelector(
    (state) => state
  );
  const dispatch = useDispatch();
  const [anchorEl, setAnchorEl] = useState(null);

  const getStatusOptions = useCallback(() => {
    return status.statuses.map((item) => ({
      id: item.statusId,
      label: item.description,
    }));
  }, [status.statuses]);

  const getPriorityOptions = useCallback(() => {
    return priority.priorities.map((item) => ({
      id: item.priorityId,
      label: item.priorityName,
    }));
  }, [priority.priorities]);

  const getCategoryOptions = useCallback(() => {
    return category.categories.map((item) => ({
      id: item.categoryId,
      label: item.categoryName,
    }));
  }, [category.categories]);

  const getMemberOptions = useCallback(() => {
    return project.members.map(({ member }) => ({
      label:
        member.firstName || member.lastName
          ? `${member.firstName} ${member.lastName}`
          : member.email ?? member.id,
      id: member.id,
      firstName: member.firstName,
      lastName: member.lastName,
      email: member.email,
    }));
  }, [project.members]);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const filterList = useMemo(
    () =>
      buildTaskFilter({
        statusOptions: getStatusOptions(),
        priorityOptions: getPriorityOptions(),
        categoryOptions: getCategoryOptions(),
        memberOptions: getMemberOptions(),
      }),
    [getStatusOptions, getPriorityOptions, getCategoryOptions, getMemberOptions]
  );

  const handleFilter = (filter) => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setFilters(filter));
  };

  return (
    <>
      <FilterButton
        variant="outlined"
        aria-haspopup="true"
        onClick={handleClick}
        aria-controls="customized-menu"
        startIcon={<Icon icon="fluent:filter-16-regular" />}
        sx={{
          ...(tasks.filters.items.length > 0 && {
            color: "primary.main",
            borderColor: "primary.main",

            "&:hover .clear-filter": {
              display: "inherit",
            },

            "&:hover": {
              pr: 5,
            },
          }),
        }}
      >
        {tasks.filters.items.length > 0 && (
          <>
            <span>{tasks.filters.items.length}</span>
            &nbsp;
          </>
        )}
        <span>Filters</span>
        <Box
          title="Clear filter"
          className="clear-filter"
          sx={{
            display: "none",
            position: "absolute",
            right: "0",
            top: "0",
            bottom: "0",
            alignItems: "center",
            padding: "0 2px",
            cursor: "pointer",
            color: (theme) => theme.palette.grey[500],

            "&:hover": {
              color: (theme) => theme.palette.grey[700],
            },
          }}
          onClick={(e) => {
            e.stopPropagation();
            handleFilter({
              condition: "AND",
              items: [],
            });
          }}
        >
          <Icon icon="uis:times-circle" />
        </Box>
      </FilterButton>
      <TaskFilter
        anchorEl={anchorEl}
        onClose={handleClose}
        filterList={filterList}
        initFilter={tasks.filters}
        onFilter={handleFilter}
      />
    </>
  );
};

export { Filter };
