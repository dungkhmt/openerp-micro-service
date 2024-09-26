import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Input,
  InputAdornment,
  Menu,
  TextField,
  Typography,
  styled,
} from "@mui/material";
import dayjs from "dayjs";
import { useState, useEffect, useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import { MenuSingleSelect } from "../../../components/menu/MenuSingleSelect";
import { useDebounce } from "../../../hooks/useDebounce";
import {
  setFilters,
  setRange as setRangeStore,
  setSearch,
  setView,
} from "../../../store/project/gantt-chart";
import { Filter } from "../tasks/Filter";
import { viewModeOptions } from "./helper";

const StyledButton = styled(Button)(({ theme }) => ({
  marginLeft: theme.spacing(2),
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: "15px",
  padding: theme.spacing(0, 1.5),
  fontSize: "12px",
  color: "primary.main",
  textTransform: "none",
  position: "relative",

  "&:hover": {
    backgroundColor: theme.palette.action.hover,
    border: `1px solid ${theme.palette.divider}`,
  },
}));

const GanttControl = () => {
  const dispatch = useDispatch();
  const {
    view,
    filters,
    search,
    range: rangeStore,
  } = useSelector((state) => state.gantt);
  const { members } = useSelector((state) => state.project);

  const [viewAnchorEl, setViewAnchorEl] = useState(null);
  const [rangeAnchorEl, setRangeAnchorEl] = useState(null);
  const [range, setRange] = useState(rangeStore);
  const [searchText, setSearchText] = useState(search);
  const searchDebounce = useDebounce(searchText, 500);

  const handleFilterChange = (filters) => {
    dispatch(setFilters(filters));
  };

  const handleCloseViewMenu = () => {
    setViewAnchorEl(null);
  };

  const handleSearchChange = useCallback(() => {
    dispatch(setSearch(searchDebounce));
  }, [searchDebounce, dispatch]);

  useEffect(() => {
    if (search !== searchDebounce) handleSearchChange();
  }, [handleSearchChange]);

  const viewMode = viewModeOptions.find((v) => v.id === view);

  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
      }}
    >
      <Box>
        <Filter
          text="Gantt Filters"
          excludeFields={["fromDate", "dueDate", "createdStamp"]}
          onFilter={handleFilterChange}
          filters={filters}
          members={members.map((m) => m.member)}
        />
        <StyledButton onClick={(e) => setViewAnchorEl(e.currentTarget)}>
          Mode: {viewMode.label}
        </StyledButton>
        <MenuSingleSelect
          anchorEl={viewAnchorEl}
          onClose={handleCloseViewMenu}
          options={viewModeOptions}
          onItemClick={(option) => {
            dispatch(setView(option.id));
          }}
          selectedItem={{ id: view }}
          searchable={false}
        />
        <StyledButton onClick={(e) => setRangeAnchorEl(e.currentTarget)}>
          {`${rangeStore.duration} tháng từ ${dayjs(
            rangeStore.startDate
          ).format("MM/YYYY")}`}
        </StyledButton>
        <Menu
          anchorEl={rangeAnchorEl}
          open={Boolean(rangeAnchorEl)}
          onClose={() => {
            setRangeAnchorEl(null);
            setRange(rangeStore);
          }}
        >
          <Box sx={{ p: 3, display: "flex", gap: 1, alignItems: "center" }}>
            <Box
              sx={{
                border: (theme) => `1px solid ${theme.palette.divider}`,
                borderRadius: "5px",
                display: "flex",
                alignItems: "center",
                gap: 2,
                p: 2,
              }}
            >
              <Input
                value={range.duration}
                onChange={(e) =>
                  setRange({
                    ...range,
                    duration: parseInt(e.target.value),
                  })
                }
                inputProps={{
                  min: 1,
                  max: 12,
                }}
                type="number"
                sx={{ width: "40px" }}
              />
              <Typography>tháng từ</Typography>
              <Input
                type="month"
                inputProps={{
                  min: "2019-01",
                }}
                value={dayjs(range.startDate).format("YYYY-MM")}
                onChange={(e) =>
                  setRange({
                    ...range,
                    startDate: dayjs(e.target.value)
                      .startOf("month")
                      .format("YYYY-MM-DD"),
                  })
                }
              />
            </Box>
            <Button
              variant="contained"
              sx={{ blockSize: "30px" }}
              disabled={!range.duration || !range.startDate}
              onClick={() => {
                dispatch(setRangeStore(range));
                setRangeAnchorEl(null);
              }}
            >
              Apply
            </Button>
          </Box>
        </Menu>
      </Box>

      <Box>
        <TextField
          placeholder="Tìm kiếm..."
          sx={{
            "& .MuiInputBase-root": {
              height: "36px",
              fontSize: "14px",
            },
          }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Icon icon="material-symbols:search" />
              </InputAdornment>
            ),
          }}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />
      </Box>
    </Box>
  );
};

export { GanttControl };
