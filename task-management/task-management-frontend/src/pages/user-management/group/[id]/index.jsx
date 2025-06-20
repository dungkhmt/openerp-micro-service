import { Helmet } from "react-helmet";
import GroupDetailsPage from "../../../../views/user-management/group/GroupDetailsPage";

const GroupDetails = () => {
  return (
    <>
      <Helmet>
        <title>Chi tiết nhóm | Task management</title>
      </Helmet>
      <GroupDetailsPage />
    </>
  );
};

export default GroupDetails;
