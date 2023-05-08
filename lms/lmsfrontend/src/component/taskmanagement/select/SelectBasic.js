// import {FormControl, InputLabel, MenuItem, Select} from "@mui/material";
//
// const SelectBasic = ({
//     label,
//     ref,
//     onChange,
//     error,
//     data,
//     value,
//     valueItem,
//     nameItem
// }) => {
//     return (
//         <FormControl fullWidth>
//             <InputLabel id="demo-simple-select-label" sx={{ fontSize: "16px" }} >{label}</InputLabel>
//             <Select
//                 labelId="demo-simple-select-label"
//                 id="demo-simple-select"
//                 label={label}
//                 ref={ref}
//                 defaultValue=""
//                 value={value}
//                 onChange={onChange}
//                 error={error}
//             >
//                 {data.map((item) => (
//                     <MenuItem key={item[valueItem]} value={item[valueItem]}>{item[nameItem]}</MenuItem>
//                 ))}
//             </Select>
//         </FormControl>
//     );
// }
//
// export default SelectBasic;