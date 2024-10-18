
//import {KeyboardDatePicker, MuiPickersUtilsProvider,} from "@material-ui/pickers";

import {format} from "date-fns";
//import moment from "moment";

export function toFormattedDateTime(rawTime) {
  let date = new Date(rawTime);
  return (
    date.getFullYear() +
    "-" +
    addZeroBefore(date.getMonth() + 1, 2) +
    "-" +
    addZeroBefore(date.getDate(), 2) +
    " " +
    addZeroBefore(date.getHours(), 2) +
    ":" +
    addZeroBefore(date.getMinutes(), 2) +
    ":" +
    addZeroBefore(date.getSeconds(), 2)
  );
}

export function toFormattedDate(rawTime) {
  let date = new Date(rawTime);
  return (
    date.getFullYear() +
    "-" +
    addZeroBefore(date.getMonth() + 1, 2) +
    "-" +
    addZeroBefore(date.getDate(), 2)
  );
}

export function toFormattedTime(numberSeconds) {
  if (numberSeconds < 0) {
    return "-" + toFormattedTime(-numberSeconds);
  }
  let hour = parseInt(numberSeconds / 3600);
  let minute = parseInt((numberSeconds / 60) % 60);
  let second = parseInt(numberSeconds % 60);
  return (
    addZeroBefore(hour, 2) +
    ":" +
    addZeroBefore(minute, 2) +
    ":" +
    addZeroBefore(second, 2)
  );
}

export function addZeroBefore(number, k) {
  return ("0" + number).slice(-k);
}



export function getDateFromNowPlus(year, month, day) {
  let date = new Date();
  return new Date(
    date.getFullYear() + year,
    date.getMonth() + month,
    date.getDay() + day
  );
}

export function dateFnFormat(date, formatString) {
  return format(date, formatString);
}





