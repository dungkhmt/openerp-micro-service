import * as React from "react";

function MetricCard({ bgColor, icon, title, value, description, change }) {
  return (
    <div
      className={`flex flex-col flex-1 shrink p-5 rounded-2xl basis-0 bg-${bgColor} min-w-[240px]`}
    >
      <div className="flex gap-5 justify-center items-end self-start">
        <div
          className={`flex gap-2.5 justify-center items-center px-2.5 w-11 h-11 ${icon.bgColor} rounded-[35px]`}
        >
          <div className="flex self-stretch my-auto min-h-[26px] w-[26px]" />
        </div>
        <div className="text-xl font-semibold text-white">{title}</div>
      </div>
      <div className="mt-5 text-3xl font-semibold text-white">{value}</div>
      <div className="flex gap-10 justify-between items-center mt-5 w-full">
        <div className="self-stretch my-auto text-base font-medium text-white">
          {description}
        </div>
        <div
          className={`flex gap-2.5 justify-center items-center self-stretch my-auto font-semibold text-${change.colorCode}`}
        >
          <div className="self-stretch my-auto text-xl">{change.value}</div>
          <div
            className={`gap-2.5 self-stretch py-2.5 pr-2.5 pl-2 my-auto text-xs bg-${change.colorCode} bg-opacity-20 rounded-[35px]`}
          >
            {change.percentage}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MetricCard;
