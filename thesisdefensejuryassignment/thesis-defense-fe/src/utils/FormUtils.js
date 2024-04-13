// import InputLabel from "@material-ui/core/InputLabel";
// import {Select} from "@material-ui/core";
// import MenuItem from "@material-ui/core/MenuItem";
// import React from "react";
// import TextField from "@material-ui/core/TextField";
// import {NumberFormatCustom} from "./NumberFormatTextField";
//
// export const notNegativeIntFilterOnChange = (newValue, setValue) => {
//   if (parseInt(newValue.replace("/[.,]/g", "")) >= 0) {
//     setValue(newValue);
//   }
// };
//
// export const notNegativeFilterOnChange = (newValue, setValue) => {
//   if (parseFloat(newValue.replace("/[.,]/g", "")) >= 0) {
//     setValue(newValue);
//   }
// };
//
// export function selectValueCallback(
//   label,
//   list,
//   menuItemValueCallback,
//   selected,
//   setSelected
// ) {
//   return (
//     <div>
//       <InputLabel>{label}</InputLabel>
//       <Select
//         value={selected}
//         onChange={(event) => setSelected(event.target.value)}
//       >
//         {list.map((e) => (
//           <MenuItem value={e}>{menuItemValueCallback(e)}</MenuItem>
//         ))}
//       </Select>
//       <p />
//     </div>
//   );
// }
//
// export function selectValueByIdName(
//   label,
//   list,
//   id,
//   name,
//   selected,
//   setSelected
// ) {
//   return (
//     <div>
//       <InputLabel>{label}</InputLabel>
//       <Select
//         value={selected || ""}
//         onChange={(event) => setSelected(event.target.value)}
//       >
//         {list.map((e) => (
//           <MenuItem value={e}>
//             {!id || !name ? e : e[name] + " (" + e[id] + ")"}
//           </MenuItem>
//         ))}
//       </Select>
//       <p />
//     </div>
//   );
// }
//
// export function textField(id, label, type, value, onChange, readOnly) {
//   return (
//     <div>
//       <TextField
//         id={id}
//         label={label}
//         type={type}
//         fullWidth={true}
//         value={value}
//         onChange={(event) => onChange(event.target.value)}
//         InputProps={{ readOnly: readOnly }}
//       />
//       <p />
//     </div>
//   );
// }
//
// export function textFieldNumberFormat(id, label, value, onChange) {
//   return (
//     <div>
//       <TextField
//         id={id}
//         label={label}
//         fullWidth={true}
//         value={value}
//         onChange={(event) => onChange(event.target.value)}
//         InputProps={{ inputComponent: NumberFormatCustom }}
//       />
//       <p />
//     </div>
//   );
// }
