const formatTime = (n) => (Number(n) < 10 ? "0" + Number(n) : "" + Number(n));

export default function displayTime(time) {
  if (time) {
    return (
      <>
        {formatTime(time.getDate())}/{formatTime(time.getMonth() + 1)}/
        {time.getFullYear()}&nbsp;&nbsp;
        {formatTime(time.getHours())}
        <b>:</b>
        {formatTime(time.getMinutes())}
        <b>:</b>
        {formatTime(time.getSeconds())}
      </>
    );
  }
}
