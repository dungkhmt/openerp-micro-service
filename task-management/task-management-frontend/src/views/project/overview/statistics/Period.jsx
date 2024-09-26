import { Box, Divider, MenuItem, Select, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setStartDate } from "../../../../store/project/statistic";

const Period = () => {
  const { period } = useSelector((state) => state.statistic);
  const dispatch = useDispatch();

  const options = [
    {
      id: 1,
      label: "1 tuần",
    },
    {
      id: 2,
      label: "2 tuần",
    },
    {
      id: 3,
      label: "3 tuần",
    },
    {
      id: 4,
      label: "4 tuần",
    },
  ];

  const currentPeriod = useMemo(
    () => dayjs(period.endDate).diff(dayjs(period.startDate), "week"),
    [period]
  );

  const handlePeriodChange = (value) => {
    dispatch(
      setStartDate(dayjs(period.endDate).subtract(value, "week").toDate())
    );
  };

  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
      <Box>
        <Typography variant="subtitle2" component="span">
          {dayjs(period.startDate).toDate().toLocaleDateString("vi-VN")}
        </Typography>
        <span> - </span>
        <Typography variant="subtitle2" component="span">
          {dayjs(period.endDate).toDate().toLocaleDateString("vi-VN")}
        </Typography>
      </Box>
      <Divider sx={{ flex: 1 }} />
      <Select
        size="small"
        value={currentPeriod}
        onChange={(e) => handlePeriodChange(e.target.value)}
        inputProps={{
          style: {
            fontSize: 14,
            fontWeight: 500,
            height: 24,
          },
        }}
        sx={{
          borderRadius: "6px",
          backgroundColor: (theme) => theme.palette.action.hover,
          "& .MuiSelect-select": {
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: `4px 8px !important`,
          },
          "& svg": {
            display: "none",
          },
          "&::before": {
            display: "none",
          },
          "&::after": {
            display: "none",
          },
          "&:hover": {
            backgroundColor: (theme) => theme.palette.action.focus,
          },
        }}
        variant="standard"
      >
        {options.map((item) => (
          <MenuItem key={item.id} value={item.id}>
            {item.label}
          </MenuItem>
        ))}
      </Select>
    </Box>
  );
};

export { Period };
