import { request } from 'api';

export const classOpenedService = {
  getAllClassOpeneds: async () => {
    return await request('get', '/class-opened/get-all');
  },

  clearAll: async () => {
    return await request('post', '/classroom/clear-all');
  },

  importExcel: async (formData, semester) => {
    return await request(
      'POST',
      `/excel/upload-class-opened?semester=${semester}`,
      null,
      null,
      formData,
      {
        'Content-Type': 'multipart/form-data',
      }
    );
  },

  exportConflicts: async (conflictData) => {
    return await request(
      'post',
      '/excel/export/class-opened-conflict',
      null,
      null,
      conflictData,
      { responseType: 'arraybuffer' }
    );
  }
};
