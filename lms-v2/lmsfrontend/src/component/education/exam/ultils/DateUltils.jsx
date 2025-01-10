import {addZeroBefore} from "../../../../utils/dateutils";

export function formatDateTime(value){
  if(value != null && value !== ''){
    let date = new Date(value);
    return (
      addZeroBefore(date.getDate(), 2) +
      "/" +
      addZeroBefore(date.getMonth() + 1, 2) +
      "/" +
      date.getFullYear() +
      " " +
      addZeroBefore(date.getHours(), 2) +
      ":" +
      addZeroBefore(date.getMinutes(), 2) +
      ":" +
      addZeroBefore(date.getSeconds(), 2)
    );
  }
  return null
}

export function formatDate(value){
  if(value != null && value !== ''){
    let date = new Date(value);
    return (
      addZeroBefore(date.getDate(), 2) +
      "/" +
      addZeroBefore(date.getMonth() + 1, 2) +
      "/" +
      date.getFullYear()
    );
  }
  return null
}

export function formatDateApi(value) {
  if(value != null && value !== ''){
    let date = new Date(value);
    return (
      date.getFullYear() +
      "-" +
      addZeroBefore(date.getMonth() + 1, 2) +
      "-" +
      addZeroBefore(date.getDate(), 2)
    );
  }
  return null
}

export function formatDateTimeApi(value){
  if(value != null && value !== ''){
    let date = new Date(value);
    return (
      date.getFullYear() +
      "-" +
      addZeroBefore(date.getMonth() + 1, 2) +
      "-" +
      addZeroBefore(date.getDate(), 2)+
      " " +
      addZeroBefore(date.getHours(), 2) +
      ":" +
      addZeroBefore(date.getMinutes(), 2) +
      ":" +
      addZeroBefore(date.getSeconds(), 2)
    );
  }
  return null
}
