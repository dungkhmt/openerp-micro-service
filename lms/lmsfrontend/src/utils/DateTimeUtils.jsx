import React from "react";

const formatTime = (n) => (Number(n) < 10 ? "0" + Number(n) : "" + Number(n));

export default function displayTime(time) {
  if (time) {
    return (
      <>
        {time.getFullYear()}-{formatTime(time.getMonth() + 1)}-
        {formatTime(time.getDate())}
        &nbsp;&nbsp;
        {formatTime(time.getHours())}
        <b>:</b>
        {formatTime(time.getMinutes())}
        <b>:</b>
        {formatTime(time.getSeconds())}
      </>
    );
  }
}
