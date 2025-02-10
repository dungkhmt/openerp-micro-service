import * as React from "react";
import Icon from "./Icon";

function UserGreeting({ name, date }) {
  return (
    <div className="flex gap-10 justify-center items-center self-stretch my-auto text-white min-w-[240px]">
      <div className="self-stretch my-auto text-2xl font-semibold">
        Hello, {name}
      </div>
      <div className="flex gap-2.5 justify-center items-end self-stretch py-2.5 pr-5 pl-4 my-auto text-lg font-medium rounded-lg bg-white bg-opacity-10">
        <div className="flex shrink-0 h-[27px] w-[26px]" />
        <div>{date}</div>
      </div>
    </div>
  );
}

export default UserGreeting;
