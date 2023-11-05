import PropTypes from "prop-types";
import ReactHtmlTableToExcel from "react-html-table-to-excel";

const ExportExcelButton = (props) => {
  const tasks = props.tasks;
  const projectName = props.projectName;

  return (
    <>
      <table id="list-task" hidden>
        <thead>
          <tr>
            <th>Id</th>
            <th>Tên nhiệm vụ</th>
            <th>Mô tả</th>
            <th>Danh mục</th>
            <th>Trạng thái</th>
            <th>Mức ưu tiên</th>
            <th>Người tạo nhiệm vụ</th>
            <th>Gán nhiệm vụ cho</th>
            <th>Thời hạn</th>
            <th>Thời gian còn lại</th>
            <th>Thời gian tạo</th>
            <th>Thời gian cập nhật</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map((item) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.name}</td>
              <td>{item.description}</td>
              <td>{item.taskCategory.categoryName}</td>
              <td>
                {item.statusItem != null
                  ? item.statusItem.description
                  : "Không xác định"}
              </td>
              <td>{item.taskPriority.priorityName}</td>
              <td>{item.createdByUserLoginId}</td>
              <td>{item.assignee}</td>
              <td>{item.dueDate}</td>
              <td>{item.timeRemaining}</td>
              <td>{item.createdStamp}</td>
              <td>{item.lastUpdatedStamp}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <ReactHtmlTableToExcel
        className="btn btn-info"
        table="list-task"
        filename={`Danh_sach_nhiem_vu_du_an_${projectName}`}
        sheet="Sheet"
        buttonText="Xuất excel"
      />
    </>
  );
};

ExportExcelButton.propTypes = {
  tasks: PropTypes.array.isRequired,
  projectName: PropTypes.string.isRequired,
};

export default ExportExcelButton;
