import * as React from "react";

function ProgressIndicators() {
  return (
    <div className="flex flex-wrap gap-10 justify-between items-center mt-9 w-full text-base font-medium leading-none text-white max-md:max-w-full">
      <div className="flex gap-1.5 items-center self-stretch my-auto">
        <div className="flex shrink-0 self-stretch my-auto h-3.5 bg-red-600 rounded-full w-[13px]" />
        <div className="self-stretch my-auto">Raw material</div>
      </div>
      <div className="flex gap-1.5 items-center self-stretch my-auto">
        <div className="flex shrink-0 self-stretch my-auto h-3.5 bg-green-600 rounded-full w-[13px]" />
        <div className="self-stretch my-auto">Product deliveries</div>
      </div>
      <div className="flex gap-1.5 items-center self-stretch my-auto">
        <div className="flex shrink-0 self-stretch my-auto h-3.5 bg-amber-400 rounded-full w-[13px]" />
        <div className="self-stretch my-auto">Warehouse load</div>
      </div>
    </div>
  );
}

export default ProgressIndicators;
