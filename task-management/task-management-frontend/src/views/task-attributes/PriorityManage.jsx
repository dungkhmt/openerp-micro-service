import { useDispatch, useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import toast from "react-hot-toast";
import ManageTable from "../../components/mui/table/ManageTable";
import { createPriority, deletePriority } from "../../store/priority";

const PriorityManage = () => {
  const dispatch = useDispatch();
  const { t } = useTranslation();
  const { priorities, fetchLoading } = useSelector((state) => state.priority);

  const handleCreatePriority = async (data) => {
    // Generate priorityId based on priorityName field
    const priorityId = data.priorityName
      .trim()
      .toUpperCase()
      .replace(/\s+/g, "_")
      .substring(0, 60);
    const dataForm = { priorityId, ...data };
    try {
      await dispatch(createPriority(dataForm)).unwrap();
      toast.success("Đã thêm mới độ ưu tiên thành công!", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi thêm độ ưu tiên!");
    }
  };

  const handleDeletePriority = async (priorityId) => {
    try {
      await dispatch(deletePriority({ priorityId }));
      toast.success("Đã xóa thành công độ ưu tiên", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi xóa độ ưu tiên!");
    }
  };

  // Prevent deletion for default priority IDs
  const defaultPriorityIds = ["URGENT", "HIGH", "LOW", "NORMAL"];
  const canDelete = (row) => !defaultPriorityIds.includes(row.priorityId);

  return (
    <ManageTable
      title="Danh sách độ ưu tiên"
      items={priorities}
      columns={[{ key: "priorityName", label: "Tên độ ưu tiên" }]}
      idKey="priorityId"
      fetchLoading={fetchLoading}
      onCreate={handleCreatePriority}
      onDelete={handleDeletePriority}
      canDelete={canDelete}
      addFields={[
        {
          key: "priorityName",
          label: "Tên độ ưu tiên",
          required: true,
          autoFocus: true,
        },
      ]}
    />
  );
};

export default PriorityManage;
