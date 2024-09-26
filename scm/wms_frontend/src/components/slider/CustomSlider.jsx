import { Box, Slider } from "@mui/material";

const marks = [
  {
    value: 0,
    label: "0°%",
  },
  {
    value: 20,
    label: "20°%",
  },
  {
    value: 37,
    label: "37°%",
  },
  {
    value: 100,
    label: "100°%",
  },
];

function valuetext(value) {
  return `${value}°%`;
}

export const CustomSlider = ({ value, onChange }) => {
  return (
    <Box sx={{ width: 300 }}>
      <Slider
        onChange={onChange}
        aria-label="Always visible"
        value={value}
        getAriaValueText={valuetext}
        marks={marks}
      />
    </Box>
  );
};
