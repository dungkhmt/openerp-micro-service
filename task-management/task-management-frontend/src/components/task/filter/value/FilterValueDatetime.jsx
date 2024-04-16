import { Icon } from "@iconify/react";
import { Typography } from "@mui/material";
import { useRef } from "react";
import PropTypes from "prop-types";
import { FilterWrapperBox } from "../FilterWrapperBox";
import dayjs from "dayjs";

const FilterValueDatetime = (props) => {
  const { item, isFirstOfDay, onChange } = props;
  const inputRef = useRef(null);

  const handleOpenDateInput = () => {
    inputRef.current.showPicker();
  };

  const handleDateChange = (e) => {
    const value = e.target.value;
    // convert value to unix timestamp
    let timestamp = dayjs(value);
    if (isFirstOfDay) {
      timestamp = timestamp.startOf("day").unix();
    } else {
      timestamp = timestamp.endOf("day").unix();
    }

    onChange([timestamp]);
  };

  return (
    <FilterWrapperBox
      sx={{
        flex: 4,
        "&:hover": {
          background: (theme) => theme.palette.background.paper,
          cursor: "default",
        },
        "& svg:hover": {
          color: (theme) => theme.palette.primary.main,
          cursor: "pointer",
        },
      }}
    >
      {item?.value?.length <= 0 ? (
        <Typography
          noWrap
          variant="subtitle2"
          component="span"
          onClick={handleOpenDateInput}
          sx={{ cursor: "pointer" }}
        >
          Chọn
        </Typography>
      ) : (
        <Typography
          noWrap
          sx={{
            maxWidth: "200px",
          }}
          variant="subtitle2"
          component="span"
        >
          {dayjs.unix(item.value).format("DD-MM-YYYY")}
        </Typography>
      )}
      <Icon
        icon="formkit:datetime"
        style={{ marginLeft: "4px" }}
        onClick={handleOpenDateInput}
      />
      <input
        type="date"
        style={{ visibility: "hidden", position: "absolute" }}
        ref={inputRef}
        value={dayjs.unix(item.value?.[0]).format("YYYY-MM-DD")}
        onChange={handleDateChange}
      />
    </FilterWrapperBox>
  );
};

FilterValueDatetime.propTypes = {
  item: PropTypes.object,
  isFirstOfDay: PropTypes.bool,
  onChange: PropTypes.func.isRequired,
};

export { FilterValueDatetime };
