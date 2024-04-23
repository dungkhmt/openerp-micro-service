import PropTypes from "prop-types";
import { FilterWrapperBox } from "../FilterWrapperBox";

const FilterValueText = (props) => {
  const { filter, item, onValueChange } = props;

  return (
    <FilterWrapperBox
      sx={{
        flex: 4,
        p: 0,
        "& input": {
          width: "100%",
          height: "100%",
          borderRadius: "4px",
          border: "none",
          padding: (theme) => theme.spacing(1, 2),

          "&:focus": {
            outline: "none",
            boxShadow: (theme) => `0 0 0 2px ${theme.palette.divider}`,
          },
        },
      }}
    >
      <input
        type={filter.type}
        {...(filter.type === "number" && {
          inputMode: "numeric",
          min: 0,
          max: 100,
          onKeyDown: (e) => {
            if (e.key === "-") {
              e.preventDefault();
            }
          },
        })}
        value={item?.value?.[0] ?? ""}
        onChange={(e) => onValueChange([e.target.value])}
      />
    </FilterWrapperBox>
  );
};

FilterValueText.propTypes = {
  filter: PropTypes.object.isRequired,
  item: PropTypes.object,
  onValueChange: PropTypes.func.isRequired,
};

export { FilterValueText };
