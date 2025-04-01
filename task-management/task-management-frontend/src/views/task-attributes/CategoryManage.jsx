import { useDispatch, useSelector } from "react-redux";
import ManageTable from "../../components/mui/table/ManageTable";
import { createCategory, deleteCategory } from "../../store/category";
import { useTranslation } from "react-i18next";
import toast from "react-hot-toast";

const CategoryManage = () => {
  const dispatch = useDispatch();
  const { t } = useTranslation();
  const { categories, fetchLoading } = useSelector((state) => state.category);

  const handleCreateCategory = async (data) => {
    // Generate categoryId based on categoryName
    const categoryId = data.categoryName
      .trim()
      .toUpperCase()
      .replace(/\s+/g, "_")
      .substring(0, 60);
    const dataForm = { categoryId, ...data };
    try {
      await dispatch(createCategory(dataForm)).unwrap();
      toast.success("Đã thêm mới danh mục thành công!", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi thêm danh mục!");
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    try {
      await dispatch(deleteCategory({ categoryId }));
      toast.success("Đã xóa thành công danh mục", true);
    } catch (e) {
      console.error(e);
      toast.error(t(e.message) || "Đã xảy ra lỗi khi xóa danh mục!");
    }
  };

  // Prevent deletion for default category IDs
  const defaultCategoryIds = ["BUG", "IMPROVEMENT", "OTHER", "REQUEST", "TASK"];
  const canDelete = (row) => !defaultCategoryIds.includes(row.categoryId);

  return (
    <ManageTable
      title="Danh sách danh mục"
      items={categories}
      columns={[
        {
          key: "categoryName",
          label: "Tên danh mục",
        },
      ]}
      idKey="categoryId"
      fetchLoading={fetchLoading}
      onCreate={handleCreateCategory}
      onDelete={handleDeleteCategory}
      canDelete={canDelete}
      addFields={[
        {
          key: "categoryName",
          label: "Tên danh mục",
          required: true,
          autoFocus: true,
        },
      ]}
    />
  );
};

export default CategoryManage;
