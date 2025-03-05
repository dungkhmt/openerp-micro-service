import { useDispatch, useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import toast from "react-hot-toast";
import ManageTable from "../../components/mui/table/ManageTable";
import { createStatus, deleteStatus } from "../../store/status";

const StatusManage = () => {
  const dispatch = useDispatch();
  const { t } = useTranslation();
  const { statuses, fetchLoading } = useSelector((state) => state.status);

  const handleCreateStatus = async (data) => {
    // Generate statusId based on statusCode field
    const statusId = data.statusCode
      .trim()
      .toUpperCase()
      .replace(/\s+/g, "_")
      .substring(0, 60);
    const dataForm = {
      statusId,
      ...data,
      statusCode: data.statusCode.trim().toUpperCase(),
    };
    try {
      await dispatch(createStatus(dataForm)).unwrap();
      toast.success("Đã thêm mới trạng thái thành công!", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi thêm trạng thái!");
    }
  };

  const handleDeleteStatus = async (statusId) => {
    try {
      await dispatch(deleteStatus({ statusId }));
      toast.success("Đã xóa thành công trạng thái", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi xóa trạng thái!");
    }
  };

  const defaultStatusCodes = [
    "ACTIVE",
    "INACTIVE",
    "CLOSED",
    "INPROGRESS",
    "OPEN",
    "RESOLVED",
  ];
  const canDelete = (row) => {
    return !defaultStatusCodes.includes(row.statusCode);
  };

  return (
    <ManageTable
      title="Danh sách trạng thái"
      items={statuses}
      columns={[
        { key: "statusCode", label: "Mã trạng thái" },
        { key: "description", label: "Mô tả" },
      ]}
      idKey="statusId"
      fetchLoading={fetchLoading}
      onCreate={handleCreateStatus}
      onDelete={handleDeleteStatus}
      canDelete={canDelete}
      addFields={[
        {
          key: "statusCode",
          label: "Mã trạng thái",
          required: true,
          autoFocus: true,
        },
        {
          key: "description",
          label: "Mô tả",
          multiline: true,
          rows: 3,
          required: true,
        },
      ]}
    />
  );
};

export default StatusManage;
