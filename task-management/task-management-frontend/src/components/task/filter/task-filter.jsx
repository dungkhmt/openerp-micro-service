import { TaskStatus } from "../status";
import { TaskPriority } from "../priority";
import { TaskCategory } from "../category";
import { UserAvatar } from "../../common/avatar/UserAvatar";
import { Box, Typography } from "@mui/material";

export const operators = {
  equal: {
    label: "Là",
    id: ":",
  },
  notEqual: {
    label: "Không là",
    id: "!",
  },
  greaterThan: {
    label: "Lớn hơn",
    id: ">",
  },
  greaterThanOrEqual: {
    label: "Lớn hơn hoặc bằng",
    // one character
    id: "]",
  },
  lessThan: {
    label: "Nhỏ hơn",
    id: "<",
  },
  lessThanOrEqual: {
    label: "Nhỏ hơn hoặc bằng",
    id: "[",
  },
  isNull: {
    label: "Không được thiết lập",
    id: ":NULL",
    isUnary: true,
  },
  isNotNull: {
    label: "Được thiết lập",
    id: "!NULL",
    isUnary: true,
  },
};

const renderUserItem = (value, showText) =>
  value && (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
      <UserAvatar user={value} width={24} height={24} fontSize={"0.7rem"} />
      {showText && <Typography variant="body2">{value.label}</Typography>}
    </Box>
  );

export const buildTaskFilter = ({
  statusOptions,
  priorityOptions,
  categoryOptions,
  memberOptions,
  excludeFields = [],
}) => {
  const fields = [
    {
      label: "Trạng thái",
      id: "statusId",
      type: "multiselect",
      operators: [
        operators.equal,
        operators.notEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      options: statusOptions,
      defaultOperator: operators.equal,
      renderValueItem: (value) =>
        value && (
          <TaskStatus
            status={{
              statusId: value.id,
              description: value.label,
            }}
          />
        ),
    },
    {
      label: "Độ ưu tiên",
      id: "priorityId",
      type: "multiselect",
      operators: [
        operators.equal,
        operators.notEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      options: priorityOptions,
      defaultOperator: operators.equal,
      renderValueItem: (value, showText) =>
        value && (
          <TaskPriority
            priority={{
              priorityId: value.id,
              priorityName: value.label,
            }}
            showText={showText}
          />
        ),
    },
    {
      label: "Danh mục",
      id: "categoryId",
      type: "multiselect",
      operators: [
        operators.equal,
        operators.notEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      options: categoryOptions,
      defaultOperator: operators.equal,
      renderValueItem: (value) =>
        value && (
          <TaskCategory
            category={{
              categoryId: value.id,
              categoryName: value.label,
            }}
          />
        ),
    },
    {
      label: "Người thực hiện",
      id: "assigneeId",
      type: "multiselect",
      operators: [
        operators.equal,
        operators.notEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      options: memberOptions,
      defaultOperator: operators.equal,
      renderValueItem: renderUserItem,
    },
    {
      label: "Người tạo",
      id: "creatorId",
      type: "multiselect",
      operators: [operators.equal, operators.notEqual],
      options: memberOptions,
      defaultOperator: operators.equal,
      renderValueItem: renderUserItem,
    },
    {
      label: "Ngày bắt đầu",
      id: "fromDate",
      type: "datetime",
      operators: [
        operators.greaterThan,
        operators.greaterThanOrEqual,
        operators.lessThan,
        operators.lessThanOrEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      defaultOperator: operators.greaterThan,
    },
    {
      label: "Ngày kết thúc",
      id: "dueDate",
      type: "datetime",
      operators: [
        operators.greaterThan,
        operators.greaterThanOrEqual,
        operators.lessThan,
        operators.lessThanOrEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      defaultOperator: operators.greaterThan,
    },
    {
      label: "Ngày tạo",
      id: "createdStamp",
      type: "datetime",
      operators: [
        operators.greaterThan,
        operators.greaterThanOrEqual,
        operators.lessThan,
        operators.lessThanOrEqual,
        operators.isNull,
        operators.isNotNull,
      ],
      defaultOperator: operators.greaterThan,
    },
    {
      label: "Tiến độ",
      id: "progress",
      type: "number",
      operators: [
        operators.equal,
        operators.notEqual,
        operators.greaterThan,
        operators.greaterThanOrEqual,
        operators.lessThan,
        operators.lessThanOrEqual,
      ],
      defaultOperator: operators.equal,
    },
    {
      label: "Ước lượng",
      id: "estimatedTime",
      type: "number",
      operators: [
        operators.equal,
        operators.notEqual,
        operators.greaterThan,
        operators.greaterThanOrEqual,
        operators.lessThan,
        operators.lessThanOrEqual,
      ],
      defaultOperator: operators.equal,
    },
  ];

  return fields.filter((field) => !excludeFields.includes(field.id));
};
