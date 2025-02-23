import { request } from 'api';

export const examClassService = {
  getAllExamClass: async () => {
    return await request('get', '/exam-class/all');
  },

  clearAll: async () => {
    return await request('post', '/exam-class/clear-all');
  },

  importExcel: async (formData, semester) => {
    return await request(
      'POST',
      `/exam-class/upload`,
      null,
      null,
      formData,
      {
        'Content-Type': 'multipart/form-data',
      }
    );
  },

  exportConflicts: async (conflictData) => {
    console.log(conflictData);
    return await request(
      'post',
      '/exam-class/export-conflict-class',
      null,
      null,
      conflictData,
      {
        responseType: 'arraybuffer',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' // Add this for Excel files
        }
      }
    );
  }
};
