import { twMerge } from "tailwind-merge";
import clsx from "clsx";
export function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}
export function cn(...inputs) {
  return twMerge(clsx(inputs));
}