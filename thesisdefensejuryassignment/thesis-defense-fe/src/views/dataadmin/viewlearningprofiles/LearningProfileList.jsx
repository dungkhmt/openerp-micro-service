import withScreenSecurity from "component/withScreenSecurity";
import LearningStatisticResults from "./LearningStatisticResults";
import StudentList from "./StudentList";

function LearningProfileList(props) {
  return (
    <>
      <div style={{ marginBottom: "20px" }}>
        <StudentList />
      </div>
      <div>
        <LearningStatisticResults />
      </div>
    </>
  );
}

const screenName = "SCR_ADMIN_USER_LEARNING_PROFILE_LIST";
export default withScreenSecurity(LearningProfileList, screenName, true);
