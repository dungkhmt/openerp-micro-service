import { request } from "api";

const CACHE_KEY = 'general-classes-cache';
const CACHE_DURATION = 5 * 60 * 1000;

const invalidateCache = (semester, groupName = "") => {
  const cacheKey = `${CACHE_KEY}-${semester}-${groupName}`;
  localStorage.removeItem(cacheKey);
};

const getCacheKey = (semester, groupName = "") => {
  return `${CACHE_KEY}-${semester}${groupName ? `-${groupName}` : ''}`;
};

export const generalScheduleRepository = {
  getClasses: async (semester, groupName = "", forceRefresh = false) => {
    try {
      if (forceRefresh) {
        console.log('Force refreshing data');
        const response = await request("get", `/general-classes/?semester=${semester}&groupName=${groupName || ""}`);
        return response.data;
      }

      const cacheKey = getCacheKey(semester, groupName);
      const cachedData = localStorage.getItem(cacheKey);
      
      // Check valid cache
      if (cachedData && !forceRefresh) {
        const { data, timestamp } = JSON.parse(cachedData);
        const isValid = Date.now() - timestamp < CACHE_DURATION;
        console.log('Cache status:', { isValid, age: Date.now() - timestamp });
        
        if (isValid) {
          console.log('Returning cached data');
          return data;
        }
      }

      console.log('Fetching fresh data');
      const response = await request("get", `/general-classes/?semester=${semester}&groupName=${groupName || ""}`);
      
      const transformedData = response.data.map(item => ({
        ...item,
        generalClassId: item.generalClassId != null ? String(item.generalClassId) : ''
      }));

      // Save to cache
      localStorage.setItem(cacheKey, JSON.stringify({
        data: transformedData,
        timestamp: Date.now()
      }));

      return transformedData;
    } catch (error) {
      console.error('Cache/fetch error:', error);
      throw error;
    }
  },

  getClassesNoSchedule: async (semester, groupName = "") => {
    try {
      const response = await request(
        "get",
        `/general-classes/?semester=${semester}&groupName=${groupName || ""}`
      );
      
      // Transform data - remove timeSlots
      const transformedData = response.data.map(classObj => {
        const { timeSlots, ...rest } = classObj;
        return rest;
      });

      return transformedData;
    } catch (error) {
      console.error('Fetch no schedule error:', error);
      throw error;
    }
  },

  resetSchedule: async (semester, ids) => {
    return await request(
      "post",
      `/general-classes/reset-schedule?semester=${semester}`,
      null,
      null,
      { ids }
    );
  },

  autoScheduleTime: async (semester, groupName, timeLimit) => {
    return await request(
      "post",
      `/general-classes/auto-schedule-time?semester=${semester}&groupName=${groupName}&timeLimit=${timeLimit}`
    );
  },

  autoScheduleRoom: async (semester, groupName, timeLimit) => {
    return await request(
      "post",
      `/general-classes/auto-schedule-room?semester=${semester}&groupName=${groupName}&timeLimit=${timeLimit}`
    );
  },

  autoScheduleSelected: async (classIds, timeLimit, semester) => {
    return await request(
      "post",
      "/general-classes/auto-schedule-timeslot-room",
      null,
      null,
      {
        classIds,
        timeLimit,
        semester,
      }
    );
  },

  exportExcel: async (semester) => {
    try {
      const response = await request(
        "post",
        `general-classes/export-excel?semester=${semester}`,
        null,
        null,
        null,
        { responseType: "arraybuffer" }
      );

      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = `Danh_sach_lop_TKB_${semester}.xlsx`;

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      return response;
    } catch (error) {
      console.error('Export Excel error:', error);
      throw error;
    }
  },

  updateTimeSlot: async (semester, saveRequest, errorCallback) => {
    try {
      const response = await request(
        "post",
        `/general-classes/update-class-schedule-v2?semester=${semester}`,
        null,
        null,
        { saveRequests: [saveRequest] },
        {},
        null,
        errorCallback
      );
      
      invalidateCache(semester);
      return response;
    } catch (error) {
      console.log('Update error:', error);
      
      if (typeof errorCallback === 'function' && error?.response?.status === 410) {
        errorCallback(error);
        return error; 
      }
      
      throw error;
    }
  },

  addTimeSlot: async (semester, params = {}) => {
    const { generalClassId, parentId, duration } = params;
    if (!generalClassId) {
      throw new Error('generalClassId is required');
    }
    const cleanId = generalClassId.toString().split("-")[0];
    const response = await request(
      "post",
      `/general-classes/${cleanId}/room-reservations/`,
      null,
      null,
      {parentId, duration } 
    );
    invalidateCache(semester);
    return response;
  },

  removeTimeSlot: async (semester, params = {}) => {
    const { generalClassId, roomReservationId } = params;
    if (!generalClassId || !roomReservationId) {
      throw new Error('generalClassId and roomReservationId are required');
    }
    const cleanId = generalClassId.toString().split("-")[0];
    const response = await request(
      "delete",
      `/general-classes/${cleanId}/room-reservations/${roomReservationId}`
    );
    invalidateCache(semester);
    return response;
  },

  deleteClasses: async (semester) => {
    const response = await request(
      "delete",
      `/general-classes/?semester=${semester}`
    );
    invalidateCache(semester);
    return response;
  },

  deleteBySemester: async (semester) => {
    const response = await request(
      "delete",
      `/general-classes/delete-by-semester?semester=${semester}`
    );
    invalidateCache(semester);
    return response;
  },

  deleteByIds: async (ids) => {
    const response = await request(
      "delete",
      `/general-classes/delete-by-ids`,
      null,
      null,
      ids
    );
    return response;
  },

  uploadFile: async (semester, file) => {
    const formData = new FormData();
    formData.append("file", file);
    
    const response = await request(
      "post",
      `/excel/upload-general?semester=${semester}`,
      null,
      null,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        }
      }
    );
    invalidateCache(semester);
    return response;
  },

  getClassGroups: async (classId) => {
    if (!classId) {
      throw new Error('classId is required');
    }
    const response = await request(
      "get",
      `/general-classes/get-class-groups?classId=${classId}`
    );
    return response.data;
  },

  updateClassGroup: async (classId, groupId) => {
    const response = await request(
      "post",
      `/general-classes/update-class-group?classId=${classId}&groupId=${groupId}`
    );
    return response.data;
  },

  deleteClassGroup: async (classId, groupId) => {
    const response = await request(
      "delete",
      `/general-classes/delete-class-group?classId=${classId}&groupId=${groupId}`
    );
    return response.data;
  },

  updateClassesGroup: async (semester, params) => {
    const { ids, groupName } = params;
    try {
      const response = await request(
        "post",
        "/general-classes/update-classes-group",
        null,
        null,
        { ids, groupName }
      );
      invalidateCache(semester);
      return response;
    } catch (error) {
      console.error('Update classes group error:', error);
      throw error;
    }
  },
};
