import { Helmet } from "react-helmet";
import { UserManagementView } from "../../views/user-management/UserManagementView";

const UserManagement = () => {
  return (
    <>
      <Helmet>
        <title>Quản lý nhân viên | Task management</title>
      </Helmet>
      <UserManagementView />
    </>
  );
};

export default UserManagement;
