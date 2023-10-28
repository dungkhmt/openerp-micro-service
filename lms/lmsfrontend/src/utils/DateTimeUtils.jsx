const formatTime = (n) => (Number(n) < 10 ? "0" + Number(n) : "" + Number(n));

export default function displayTime(input) {
  if (input) {
    let time;

    if (typeof input === "string") {
      time = new Date(input);
    } else if (input instanceof Date) {
      time = input;
    } else {
      return <> </>;
    }

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

  return <> </>;
}
