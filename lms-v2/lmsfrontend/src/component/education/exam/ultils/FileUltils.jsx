import {config} from "../../../../config/config";

export const prefixFile = config.url.API_URL + '/service/files/'
export function getFilePathFromString(value){
  return prefixFile + value
}

export function getFilenameFromString(value){
  const files = value.split('/')
  return files[files.length - 1]
}
