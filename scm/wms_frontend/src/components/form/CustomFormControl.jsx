import { Stack } from "@mui/material";
import { Controller } from "react-hook-form";
import { CustomDatePicker } from "../datepicker/CustomDatePicker";
import CustomInput from "../input/CustomInput";
import CustomTagsInput from "../input/CustomTagsInput";
import CustomSelect from "../select/CustomSelect";
import { CustomSlider } from "../slider/CustomSlider";

/**
 * @typedef FieldControls
 * @property {"input" | "select" | "date" | "slider" | "tagsinput"} component
 * @property {string} name
 * @property {string} type
 * @property {string} label
 * @property {Array.{label: string, value: string | number}} options
 * @property {boolean=} loading
 * @property {boolean=} disabled
 * @property {any=} minDate
 * @property {any=} renderOption
 * @property {boolean=} readOnly
 * @property {any} field_type
 * @typedef Prop
 * @property {FieldControls[]} fields
 * @property {Function} handleSearch
 * @property {any} control
 * @property {any[]} errors
 * @param {Prop} props
 */
const CustomFormControl = (props) => {
  const { fields, handleSearch, control, errors } = props;
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
          case "select":
            return (
              <Controller
                control={control}
                name={el.name}
                key={id}
                render={({ field: { onChange, value } }) => (
                  <CustomSelect
                    readOnly={el?.readOnly}
                    options={el.options ? el.options : []}
                    onSearch={(value) =>
                      handleSearch
                        ? handleSearch({ [el.name]: value })
                        : undefined
                    }
                    loading={el.loading}
                    value={value}
                    onChange={onChange}
                    label={el.label}
                    error={!!errors[el.name]}
                    message={errors[el.name]?.message}
                    renderOption={el.renderOption}
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
                    onChange={onChange}
                    label={el.label}
                    minDate={el.minDate}
                  />
                )}
              />
            );

          case "tagsinput":
            return (
              <Controller
                name={el.name}
                control={control}
                key={id}
                render={({ field: { onChange, value } }) => (
                  <CustomTagsInput
                    onChange={onChange}
                    value={value}
                    label={el.label}
                    error={!!errors[el.name]}
                    message={errors[el.name]?.message}
                  />
                )}
              />
            );

          case "slider":
            return (
              <Controller
                control={control}
                name={el.name}
                key={id}
                render={({ field: { onChange, value } }) => (
                  <CustomSlider value={value} onChange={onChange} />
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
export default CustomFormControl;
