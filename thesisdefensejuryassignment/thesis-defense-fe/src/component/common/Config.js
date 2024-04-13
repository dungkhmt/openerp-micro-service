export const ChartColor = [
  "#f44336",
  "#009688",
  "#3f51b5",
  "#ffeb3b",
  "#e91e63",
  "#2196f3",
  "#4caf50",
  "#ffc107",
  "#9c27b0",
  "#03a9f4",
  "#4caf50",
  "#ff9800",
  "#673ab7",
  "#00bcd4",
  "#cddc39",
  "#ff5722",
];

export const TABLE_STRIPED_ROW_COLOR = ["#f6f6f6", "#ffffff"];

export const COLOR = {
  red: "#F44336",
  orange: "#FF9800",
  blue: "#2196F3",
  green: "#4CAF50",
};

// task status
export const TASK_STATUS = {
  LIST: [
    {
      statusId: "TASK_OPEN",
      statusCode: "OPEN",
      description: "Mở",
      color: COLOR.red,
    },
    {
      statusId: "TASK_INPROGRESS",
      statusCode: "INPROGRESS",
      description: "Đang xử lý",
      color: COLOR.orange,
    },
    {
      statusId: "TASK_RESOLVED",
      statusCode: "RESOLVED",
      description: "Đã xử lý",
      color: COLOR.green,
    },
    {
      statusId: "TASK_CLOSED",
      statusCode: "CLOSED",
      description: "Đóng",
      color: COLOR.blue,
    },
  ],
  DEFAULT_ID_ASSIGNED: "TASK_INPROGRESS",
  DEFAULT_ID_NOT_ASSIGN: "TASK_OPEN",
};

// task priority
export const TASK_PRIORITY = {
  LIST: [
    {
      priorityId: "HIGH",
      priorityName: "Cao",
      icon: "arrow_upward",
      color: COLOR.red,
    },
    {
      priorityId: "NORMAL",
      priorityName: "Trung bình",
      icon: "arrow_forward",
      color: COLOR.blue,
    },
    {
      priorityId: "LOW",
      priorityName: "Thấp",
      icon: "arrow_downward",
      color: COLOR.green,
    },
  ],
  DEFAULT_PRIORITY: "NORMAL",
};

// task category
export const TASK_CATEGORY = {
  LIST: [
    {
      categoryId: "BUG",
      categoryName: "Lỗi",
      color: COLOR.red,
    },
    {
      categoryId: "OTHER",
      categoryName: "Khác",
      color: COLOR.blue,
    },
    {
      categoryId: "REQUEST",
      categoryName: "Yêu cầu",
      color: COLOR.orange,
    },
    {
      categoryId: "TASK",
      categoryName: "Nhiệm vụ",
      color: COLOR.green,
    },
  ],
  DEFAULT_CATEGORY: "TASK",
};
