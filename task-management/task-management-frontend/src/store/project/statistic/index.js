import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { StatisticService } from "../../../services/api/statistic.service";
import dayjs from "dayjs";

export const fetchStatisticData = createAsyncThunk(
  "project/fetchStatisticData",
  async ({ projectId, startDate, endDate }, { dispatch }) => {
    // get all the statistic data
    dispatch(fetchCreatedTaskStatistic({ projectId, startDate, endDate }));
    dispatch(fetchCompletedTaskStatistic({ projectId, startDate, endDate }));
    dispatch(fetchInprogressTaskStatistic({ projectId, startDate, endDate }));
    dispatch(fetchWorkloadByStatus({ projectId, startDate, endDate }));
  }
);

export const fetchCreatedTaskStatistic = createAsyncThunk(
  "project/fetchCreatedTaskStatistic",
  async ({ projectId, startDate, endDate, includeTasks = false }) => {
    // FIXME: fix the hard code id of the status here
    const paginationTasks = await StatisticService.getTaskStatisticByStatus(
      projectId,
      "all",
      startDate,
      endDate,
      includeTasks
    );
    return paginationTasks;
  }
);

export const fetchCompletedTaskStatistic = createAsyncThunk(
  "project/fetchCompletedTaskStatistic",
  async ({ projectId, startDate, endDate, includeTasks = false }) => {
    // FIXME: fix the hard code id of the status here
    const paginationTasks = await StatisticService.getTaskStatisticByStatus(
      projectId,
      "TASK_RESOLVED",
      startDate,
      endDate,
      includeTasks
    );
    return paginationTasks;
  }
);

export const fetchInprogressTaskStatistic = createAsyncThunk(
  "project/fetchInprogressTaskStatistic",
  async ({ projectId, startDate, endDate, includeTasks = false }) => {
    // FIXME: fix the hard code id of the status here
    const paginationTasks = await StatisticService.getTaskStatisticByStatus(
      projectId,
      "TASK_INPROGRESS",
      startDate,
      endDate,
      includeTasks
    );
    return paginationTasks;
  }
);

export const fetchWorkloadByStatus = createAsyncThunk(
  "project/fetchWorkloadByStatus",
  async ({ projectId, startDate, endDate }) => {
    const workload = await StatisticService.getWorkloadByStatus(
      projectId,
      startDate,
      endDate
    );
    return workload;
  }
);

const initialState = {
  created: {
    count: 0,
    percentage: 0,
    tasks: [],
    loading: false,
  },
  inprogress: {
    count: 0,
    percentage: 0,
    tasks: [],
    loading: false,
  },
  completed: {
    count: 0,
    percentage: 0,
    tasks: [],
    loading: false,
  },
  workloadByStatus: {
    load: [],
    loading: false,
  },
  period: {
    startDate: dayjs().subtract(1, "week").startOf("day").toDate(),
    endDate: dayjs().endOf("day").toDate(),
  },
};

export const statisticSlice = createSlice({
  name: "statistic",
  initialState,
  reducers: {
    setStartDate: (state, action) => {
      state.period.startDate = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCreatedTaskStatistic.pending, (state) => {
        state.created.loading = true;
      })
      .addCase(fetchCreatedTaskStatistic.fulfilled, (state, action) => {
        state.created.count = action.payload.count ?? 0;
        state.created.percentage = action.payload.percentage ?? 0;
        state.created.tasks = action.payload.tasks ?? [];
        state.created.loading = false;
      })
      .addCase(fetchCreatedTaskStatistic.rejected, (state) => {
        state.created.loading = false;
      })
      .addCase(fetchCompletedTaskStatistic.pending, (state) => {
        state.completed.loading = true;
      })
      .addCase(fetchCompletedTaskStatistic.fulfilled, (state, action) => {
        state.completed.count = action.payload.count ?? 0;
        state.completed.percentage = action.payload.percentage ?? 0;
        state.completed.tasks = action.payload.tasks ?? [];
        state.completed.loading = false;
      })
      .addCase(fetchCompletedTaskStatistic.rejected, (state) => {
        state.completed.loading = false;
      })
      .addCase(fetchInprogressTaskStatistic.pending, (state) => {
        state.inprogress.loading = true;
      })
      .addCase(fetchInprogressTaskStatistic.fulfilled, (state, action) => {
        state.inprogress.count = action.payload.count ?? 0;
        state.inprogress.percentage = action.payload.percentage ?? 0;
        state.inprogress.tasks = action.payload.tasks ?? [];
        state.inprogress.loading = false;
      })
      .addCase(fetchInprogressTaskStatistic.rejected, (state) => {
        state.inprogress.loading = false;
      })
      .addCase(fetchWorkloadByStatus.pending, (state) => {
        state.workloadByStatus.loading = true;
      })
      .addCase(fetchWorkloadByStatus.fulfilled, (state, action) => {
        state.workloadByStatus.load = action.payload;
        state.workloadByStatus.loading = false;
      })
      .addCase(fetchWorkloadByStatus.rejected, (state) => {
        state.workloadByStatus.loading = false;
      });
  },
});

export const { setStartDate } = statisticSlice.actions;

export default statisticSlice.reducer;
