import * as React from "react";
import Icon from "./Icon";
import ProgressIndicators from "./ProgressIndicators";
function WarehouseWorkload({ title, iconClasses, imgSrc, alt,hasAnnotation }) {
  return (
    <div className="flex overflow-hidden flex-col justify-center py-7 pr-8 pl-8 mx-auto w-full rounded-2xl bg-slate-600 max-md:px-5 max-md:mt-7 max-md:max-w-full">
      <div className="flex flex-wrap gap-10 justify-between items-end w-full max-md:max-w-full">
        <div className="text-2xl font-semibold text-white">{title}</div>
        <div className="flex gap-3.5 items-start">
          {iconClasses.map((iconClass, index) => (
            <Icon key={index} />
          ))}
        </div>
      </div>
      <img
        loading="lazy"
        src={imgSrc}
        alt={alt}
        className="object-contain self-center mt-9 max-w-full aspect-[1.83] w-[582px]"
      />
      {hasAnnotation && <ProgressIndicators />}
    </div>
  );
}

export default WarehouseWorkload;
