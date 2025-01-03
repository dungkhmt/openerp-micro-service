const FieldMap = {
  statusId: "Trạng thái",
  priorityId: "Ưu tiên",
  categoryId: "Danh mục",
  fromDate: "Ngày bắt đầu",
  dueDate: "Thời hạn",
  name: "Tiêu đề",
  description: "Mô tả",
  assigneeId: "Người thực hiện",
  attachmentPaths: "Tệp tin",
  estimatedTime: "Thời gian ước lượng",
  progress: "Tiến độ",
  subtaskId: "Công việc con",
  eventId: "Sự kiện",
};

export const parseLogItemDetail = (logItem) => {
  const { event, field, oldValue, newValue } = logItem;

  switch (event) {
    case "set":
    case "update":
      switch (field) {
        case "description":
          return [
            FieldMap[field],
            event === "set" ? "được thiết lập" : "được cập nhật",
            oldValue,
            "",
            newValue,
          ];
        case "estimatedTime":
          return [
            FieldMap[field],
            event === "set" ? "được thiết lập thành" : "được cập nhật từ",
            oldValue,
            event === "set" ? "" : "sang",
            `${newValue} (giờ)`,
          ];
        case "progress":
          return [
            FieldMap[field],
            event === "set" ? "được thiết lập thành" : "được cập nhật từ",
            `${oldValue}%`,
            event === "set" ? "" : "sang",
            `${newValue}%`,
          ];
        case "assigneeId":
          return [
            FieldMap[field],
            event === "set" ? "được giao cho" : "được cập nhật từ",
            oldValue,
            event === "set" ? "" : "sang",
            newValue,
          ];
        case "eventId":
          return [
            FieldMap[field],
            newValue === ""
              ? "được gỡ bỏ"
              : event === "set"
              ? "được thiết lập thành"
              : "được cập nhật từ",
            oldValue,
            newValue === "" ? "" : event === "set" ? "" : "sang",
            newValue,
          ];
        default:
          return [
            FieldMap[field],
            event === "set" ? "được thiết lập thành" : "được cập nhật từ",
            oldValue,
            event === "set" ? "" : "sang",
            newValue,
          ];
      }
    case "attachment":
      return [FieldMap[field], "được đính kèm", "", "", newValue];
    case "subtask":
      return [FieldMap[field], "được tạo", "", "", newValue];
    default:
      return [FieldMap[field] || field, "", oldValue, "", newValue];
  }
};
