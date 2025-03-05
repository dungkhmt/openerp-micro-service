import { useDispatch } from "react-redux";
import { useSelector } from "react-redux";
import toast from "react-hot-toast";
import { useTranslation } from "react-i18next";
import ManageTable from "../../components/mui/table/ManageTable";
import { addSkill, deleteSkill, updateSkill } from "../../store/skill";


const SkillManage = () => {
  const dispatch = useDispatch();
  const { t } = useTranslation();
  const { skills, fetchLoading } = useSelector((state) => state.skill);

  const handleCreateSkill = async (data) => {
    const skillId = data.name
      .trim()
      .toUpperCase()
      .replace(/\s+/g, "_")
      .substring(0, 60);
    const dataForm = { skillId, ...data };
    try {
      await dispatch(addSkill(dataForm)).unwrap();
      toast.success("Đã thêm mới kỹ năng thành công!", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi thêm kỹ năng!");
    }
  };

  const handleDeleteSkill = async (skillId) => {
    try {
      await dispatch(deleteSkill({ skillId }));
      toast.success("Đã xóa thành công kỹ năng", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi xóa kỹ năng!");
    }
  };

  const handleEditSkill = async (skillId, updatedData) => {
    const dataForm = { skillId, data: updatedData };
    try {
      await dispatch(updateSkill(dataForm)).unwrap();
      toast.success("Đã cập nhật kỹ năng thành công!", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi cập nhật kỹ năng!");
    }
  };

  return (
    <ManageTable
      title="Danh sách kỹ năng"
      items={skills}
      columns={[
        { key: "name", label: "Tên kỹ năng" },
        { key: "description", label: "Mô tả" },
      ]}
      idKey="skillId"
      fetchLoading={fetchLoading}
      onCreate={handleCreateSkill}
      onDelete={handleDeleteSkill}
      canDelete={() => true}
      onEdit={handleEditSkill}
      addFields={[
        { key: "name", label: "Tên kỹ năng", required: true, autoFocus: true },
        { key: "description", label: "Mô tả", multiline: true, rows: 4 },
      ]}
      editFields={[
        { key: "name", label: "Tên kỹ năng", readOnly: true },
        {
          key: "description",
          label: "Mô tả",
          multiline: true,
          rows: 4,
          autoFocus: true,
        },
      ]}
      canEdit={() => true}
    />
  );
};

export default SkillManage;
