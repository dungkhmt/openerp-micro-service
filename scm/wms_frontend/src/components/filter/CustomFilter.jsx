import { grey } from "@mui/material/colors";
import { styled } from "@mui/styles";
import { AppColors } from "../../shared/AppColors";

const {
  Box,
  Stack,
  Switch,
  FormControlLabel,
  Checkbox,
} = require("@mui/material");
const { Controller } = require("react-hook-form");
const { default: CustomInput } = require("../input/CustomInput");
const { default: CustomSelect } = require("../select/CustomSelect");
const { CustomDatePicker } = require("../datepicker/CustomDatePicker");
const { CheckBox } = require("@mui/icons-material");

const IOSSwitch = styled((props) => (
  <Switch focusVisibleClassName=".Mui-focusVisible" disableRipple {...props} />
))(({ theme }) => ({
  width: 100,
  height: 26,
  padding: 0,
  "& .MuiSwitch-switchBase": {
    padding: 0,
    margin: 2,
    transitionDuration: "300ms",
    "&.Mui-checked": {
      transform: "translateX(74px)",
      color: "#fff",
      "& + .MuiSwitch-track": {
        // backgroundColor: theme.palette.mode === "dark" ? "#2ECA45" : "#65C466",
        backgroundColor: AppColors.green,
        opacity: 1,
        border: 0,
      },
      "&.Mui-disabled + .MuiSwitch-track": {
        opacity: 0.5,
      },
    },
    "&.Mui-focusVisible .MuiSwitch-thumb": {
      color: "#33cf4d",
      border: "6px solid #fff",
    },
    "&.Mui-disabled .MuiSwitch-thumb": {
      color:
        // theme.palette.mode === "light"
        grey[100],
      // : theme.palette.grey[600],
    },
    "&.Mui-disabled + .MuiSwitch-track": {
      // opacity: theme.palette.mode === "light" ? 0.7 : 0.3,
      opacity: 0.7,
    },
  },
  "& .MuiSwitch-thumb": {
    boxSizing: "border-box",
    width: 22,
    height: 22,
  },
  "& .MuiSwitch-track": {
    borderRadius: 26 / 2,
    // backgroundColor: theme.palette.mode === "light" ? "#E9E9EA" : "#39393D",
    backgroundColor: "#E9E9EA",
    opacity: 1,
    // transition: theme.transitions.create(["background-color"], {
    //   duration: 500,
    // }),
  },
}));
const CustomFilter = (props) => {
  const { fields, control, errors } = props;
  return (
    <Stack>
      {fields.map((el, id) => {
        switch (el.component) {
          case "input":
            return (
              <Controller
                key={id}
                control={control}
                name={el.name}
                render={({ field: { onChange, value } }) => (
                  <CustomInput
                    required={el?.required}
                    value={value}
                    type={el.type}
                    label={el.label}
                    readOnly={el?.readOnly}
                    error={!!errors[el.name]}
                    message={errors[el.name]?.message}
                    onChange={onChange}
                  />
                )}
              />
            );
          case "select":
            return (
              <Controller
                key={id}
                control={control}
                name={el.name}
                render={({ field: { onChange, value } }) => (
                  <CustomSelect
                    readOnly={el?.readOnly}
                    options={el.options ? el.options : []}
                    loading={el.loading}
                    value={value}
                    label={el.label}
                    error={!!errors[el.name]}
                    message={errors[el.name]?.message}
                    renderOption={el.renderOption}
                    fullWidth={el.fullWidth}
                    onChange={onChange}
                  />
                )}
              />
            );

          case "date":
            return (
              <Controller
                name={el.name}
                control={control}
                key={id}
                render={({ field: { onChange, value } }) => (
                  <CustomDatePicker
                    value={value}
                    error={!!errors[el.name]}
                    message={errors[el.name]?.message}
                    label={el.label}
                    minDate={el.minDate}
                    onChange={onChange}
                  />
                )}
              />
            );

          case "checkbox":
            return (
              <Controller
                name={el.name}
                control={control}
                key={id}
                render={({ field: { onChange, value } }) => (
                  <Checkbox {...el.label} defaultChecked color="default" />
                )}
              />
            );

          case "switch":
            return (
              <Controller
                control={control}
                name={el.name}
                key={id}
                render={({ field: { onChange, value } }) => (
                  <FormControlLabel
                    control={
                      <IOSSwitch
                        sx={{ m: 1, marginBottom: 2, marginTop: 0 }}
                        defaultChecked
                        check={el.check}
                        required={el.required}
                        onChange={onChange}
                      />
                    }
                    label={value}
                  />
                )}
              />
            );
          default:
            return (
              <Controller
                key={id}
                control={control}
                name={el.name}
                render={({ field: { onChange, value } }) => (
                  <CustomInput
                    readOnly={el?.readOnly}
                    error={!!errors[el.name]}
                    message={errors[el.name]?.message}
                    value={value}
                    type={el.type}
                    onChange={onChange}
                    label={el.label}
                  />
                )}
              />
            );
        }
      })}
    </Stack>
  );
};
export default CustomFilter;
