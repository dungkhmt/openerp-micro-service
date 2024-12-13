import { request } from 'api';

export const classroomService = {
  getAllClassrooms: async () => {
    const response = await request('get', '/classroom/get-all');
    return response;
  },

  createClassroom: async (data) => {
    const response = await request('post', '/classroom/create', null, null, data);
    return response;
  },

  updateClassroom: async (data) => {
    const response = await request('post', '/classroom/update', null, null, data);
    return response;
  },

  deleteClassroom: async (id) => {
    const response = await request('delete', `/classroom/delete?id=${id}`);
    return response;
  },

  getAllBuildings: async () => {
    const response = await request('get', '/classroom/get-all-building');
    return response;
  },

  postClassroom: async (data) => {
    const response = await request('post', '/classroom/', null, null, data);
    return response;
  },

  importExcel: async (formData) => {
    const response = await request(
      'POST',
      '/excel/upload-classroom',
      null,
      null,
      formData,
      {
        "Content-Type": "multipart/form-data",
      }
    );
    return response;
  },

  clearAll: async () => {
    return await request('post', '/classroom/clear-all');
  }
};
