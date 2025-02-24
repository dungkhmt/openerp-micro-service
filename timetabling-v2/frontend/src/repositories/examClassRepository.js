import { request } from 'api';

export const examClassService = {
  getAllExamClass: async (examPlanId) => {
    return await request('get', '/exam-class?examPlanId=' + examPlanId);
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

  importExcel: async (formData, examPlanId) => {
    return await request(
      'POST',
      `/exam-class/upload?examPlanId=${examPlanId}`,
      null,
      null,
      formData,
      {
        'Content-Type': 'multipart/form-data',
      }
    );
  },

  downloadSample: async () => {
    return await request(
      'get',
      '/exam-class/download-template',
      null,
      null,
      null,
      {
        responseType: 'arraybuffer',
        headers: {
          'Content-Type': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' // Add this for Excel files
        }
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
