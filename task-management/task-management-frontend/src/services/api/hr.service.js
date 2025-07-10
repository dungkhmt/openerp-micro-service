import privateClient from "@/services/client/private.client";

const hrEndpoints = {
  staffsMe: "staffs/me",
  shifts: "shifts",
  staffsDetails: "staffs/details",
  departments: "departments",
  jobPositions: "jobs",
  absences: "absences",
  monthAttendance: "checkinout",
  payrolls: "payrolls",
  payrollDetails: (id) => `payrolls/${id}/details`,
  checkpointPeriods: "checkpoints/periods",
  checkpointPeriodDetail:(periodId) => `checkpoints/periods/${periodId}`,
  checkpointDetails: (periodId, userId) => `checkpoints/${periodId}/${userId}`,
  configs: "configs",
  holidays: "holidays",
};

const hrService = {
  getAllStaffDetails: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.staffsDetails, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getAllDepartments: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.departments, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getAllJobPositions: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.jobPositions, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getMyStaffDetails: async () => {
    try {
      const response = await privateClient.get(hrEndpoints.staffsMe);
      return { response };
    } catch (err) { return { err }; }
  },

  getShiftList: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.shifts, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getAbsenceList: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.absences, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getMonthAttendance: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.monthAttendance, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getPayrollList: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.payrolls, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getPayrollDetails: async (payrollId, params) => {
    try {
      const response = await privateClient.get(hrEndpoints.payrollDetails(payrollId), { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getCheckpointPeriods: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.checkpointPeriods, { params });
      return { response };
    } catch (err) { return { err }; }
  },

  getCheckpointDetails: async (periodId, userId) => {
    try {
      const response = await privateClient.get(hrEndpoints.checkpointDetails(periodId, userId));
      return { response };
    } catch (err) { return { err }; }
  },

  getCheckpointPeriodById: async (periodId) => {
    try {
      const response = await privateClient.get(hrEndpoints.checkpointPeriodDetail(periodId));
      return { response };
    } catch (err) {
      return { err };
    }
  },

  // Lấy config của công ty (giờ làm/nghỉ trưa...)
  getCompanyConfigs: async () => {
    try {
      const response = await privateClient.get(hrEndpoints.configs, {
        params: { configGroup: "COMPANY_CONFIGS" }
      });
      return { response };
    } catch (err) { return { err }; }
  },

  // Lấy ngày nghỉ lễ
  getHolidays: async (params) => {
    try {
      const response = await privateClient.get(hrEndpoints.holidays, { params });
      return { response };
    } catch (err) { return { err }; }
  }
};

export default hrService;
