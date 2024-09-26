import CloseIcon from "@mui/icons-material/Close";
import { Autocomplete, Chip, TextField } from "@mui/material";
import { useState } from "react";
export default function CustomTagsInput(props) {
  const { value, onChange, label, error, message, sx, readOnly, options } =
    props;
  const [tag, setTag] = useState("");
  return (
    <Autocomplete
      sx={{ ...sx }}
      multiple
      options={options ? options : []}
      value={value}
      // onInputChange={(event, newInputValue, reason) => {
      //   if (reason === "reset") {
      //     onChange([]);
      //   }
      // }}
      disableClearable={true}
      renderTags={(v, getTagProps) =>
        v.map((option, index) => (
          <Chip
            key={index}
            size="small"
            label={option}
            sx={{ borderRadius: 0 }}
            {...getTagProps({ index })}
            onDelete={() => onChange(value.filter((el) => el !== option))}
            deleteIcon={<CloseIcon />}
          />
        ))
      }
      renderInput={(params) => (
        <TextField
          {...params}
          sx={{ mt: 1, mb: 1 }}
          label={label}
          variant="outlined"
          size="small"
          InputLabelProps={{
            shrink: true,
          }}
          value={tag}
          onChange={(e) => setTag(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && tag) {
              if (!value.includes(tag)) {
                onChange([...value, tag.trim()]);
                setTag("");
              }
            }
            if (e.key === "Backspace" && tag === "") {
              onChange([...value.slice(0, -1)]);
            }
          }}
          error={error}
          helperText={message ? message : " "}
        />
      )}
    />
  );
}
