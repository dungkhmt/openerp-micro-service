import { Helmet } from "react-helmet";
import UserGroupsPage from "../../../views/user-management/group/UserGroupsPage";


const UserGroups = () => {
  return (
    <>
      <Helmet>
        <title>Danh sách nhóm | Task management</title>
      </Helmet>
      <UserGroupsPage />
    </>
  );
};

export default UserGroups;
