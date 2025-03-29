import { Icon } from "@iconify/react";
import { Box, Button, styled } from "@mui/material";
import PropTypes from "prop-types";
import { useCallback, useMemo, useState } from "react";
import { useSelector } from "react-redux";
import { TaskFilter } from "../../../components/task/filter";
import { buildTaskFilter } from "../../../components/task/filter/task-filter";

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

const Filter = (props) => {
  const {
    text,
    showIcon = true,
    onFilter,
    excludeFields,
    sx,
    filters,
    members = [],
    statusType = "task",
  } = props;
  const { status, priority, category } = useSelector((state) => state);
  const [anchorEl, setAnchorEl] = useState(null);

  const getStatusOptions = useCallback(() => {
    const statuses =
      statusType === "task" ? status.taskStatuses : status.meetingStatuses;
    return statuses.map((item) => ({
      id: item.statusId,
      label: item.description,
    }));
  }, [status.meetingStatuses, status.taskStatuses, statusType]);

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
    return members.map((member) => ({
      label:
        member.firstName || member.lastName
          ? `${member.firstName} ${member.lastName}`
          : member.email ?? member.id,
      id: member.id,
      firstName: member.firstName,
      lastName: member.lastName,
      email: member.email,
    }));
  }, [members]);

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
        excludeFields,
      }),
    [
      getStatusOptions,
      getPriorityOptions,
      getCategoryOptions,
      getMemberOptions,
      excludeFields,
    ]
  );

  const handleFilter = (filter) => {
    onFilter?.(filter);
  };

  return (
    <>
      <FilterButton
        variant="outlined"
        aria-haspopup="true"
        onClick={handleClick}
        aria-controls="customized-menu"
        startIcon={showIcon && <Icon icon="fluent:filter-16-regular" />}
        sx={{
          ...sx,
          ...(filters.items.length > 0 && {
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
        {filters.items.length > 0 && (
          <>
            <span>{filters.items.length}</span>
            &nbsp;
          </>
        )}
        {text && <span>{text}</span>}
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
        initFilter={filters}
        onFilter={handleFilter}
      />
    </>
  );
};

Filter.propTypes = {
  text: PropTypes.string,
  showIcon: PropTypes.bool,
  onFilter: PropTypes.func,
  excludeFields: PropTypes.array,
  sx: PropTypes.object,
  filters: PropTypes.object,
  members: PropTypes.array,
  statusType: PropTypes.string,
};

export { Filter };
