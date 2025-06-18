import { Helmet } from "react-helmet";
import UserManagementPage from "../../views/user-management/UserManagementPage";

const UserManagement = () => {
  return (
    <>
      <Helmet>
        <title>Danh sách nhân viên | Task management</title>
      </Helmet>
      <UserManagementPage />
    </>
  );
};

export default UserManagement;
