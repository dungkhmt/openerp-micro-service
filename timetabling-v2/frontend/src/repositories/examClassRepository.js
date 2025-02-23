import { request } from 'api';

export const examClassService = {
  getAllExamClass: async () => {
    return await request('get', '/exam-class/all');
  },

  deleteExamClasses: async (examClassIds) => {
    return await request('post', '/exam-class/delete-classes', null, null, examClassIds, {
      headers: {
        'Content-Type': 'application/json',
      }
    });
  },

  createExamClass: async (examClass) => {
    return await request('post', '/exam-class/create', null, null, examClass, {
      headers: {
        'Content-Type': 'application/json',
      }
    });
  },

  updateExamClass: async (examClass) => {
    return await request('post', '/exam-class/update', null, null, examClass, {
      headers: {
        'Content-Type': 'application/json',
      }
    });
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

  exportExcel: async (classExamIds) => {
    return await request(
      'post',
      '/exam-class/export',
      null,
      null,
      classExamIds,
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
