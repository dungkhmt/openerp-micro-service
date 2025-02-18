import * as React from "react";

function Icon({ children }) {
  return (
    <div className="flex gap-2.5 items-start p-2.5">
      <div className="flex min-h-[27px] w-[26px]" />
      {children}
    </div>
  );
}

export default Icon;
