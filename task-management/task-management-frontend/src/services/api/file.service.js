import privateClient from "../client/private.client";

const endPoints = {
  upload: `/content/create`,
  download: (fileId) => `/content/get/${fileId}`,
};

export const FileService = {
  async uploadFile(file) {
    if (!file) return;

    const body = {
      id: `myFile_${new Date().getTime().toString()}`,
    };
    const formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", file);

    const response = await privateClient.post(endPoints.upload, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    const data = response.data;
    return data;
  },
  async downloadFile(fileId) {
    if (!fileId) return;
    const response = await privateClient.get(endPoints.download(fileId), {
      responseType: "blob",
    });
    return response.data;
  },
};
