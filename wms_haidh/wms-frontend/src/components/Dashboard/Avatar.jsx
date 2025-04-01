import * as React from "react";

function Avatar({ src, alt, name, id }) {
  return (
    <div className="flex gap-2.5 items-start self-stretch my-auto">
      <img
        loading="lazy"
        src={src}
        alt={alt}
        className="object-contain shrink-0 w-11 rounded-full aspect-square"
      />
      <div className="flex flex-col justify-center">
        <div className="text-lg font-medium text-white">{name}</div>
        <div className="mt-1 text-sm text-white text-opacity-80">{id}</div>
      </div>
    </div>
  );
}

export default Avatar;
