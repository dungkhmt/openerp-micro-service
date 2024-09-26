import {request} from "../../../../api";

export default async function getFileByStorageId(storageId) {
  let attachment;
  const convertResponseToBlob = (res) => {
    attachment = res.data;
  }
  let errorHandlers = {
    onError: (error) => console.log("error", error)
  }
  await request("GET", `/content/get/${storageId}`, convertResponseToBlob, errorHandlers,null, { responseType: 'blob'});
  return attachment;
}