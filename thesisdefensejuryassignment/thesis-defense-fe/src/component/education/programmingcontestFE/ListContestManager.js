import withScreenSecurity from "component/withScreenSecurity";
import { ListContestAll } from "./ListContestAll";
import { ListContestManagerByRegistration } from "./ListContestManagerByRegistration";

function ListContestManager() {
  return (
    <div>
      <ListContestManagerByRegistration />
      <ListContestAll />
    </div>
  );
}

const screenName = "SCR_MANAGER_CONTEST_LIST";
export default withScreenSecurity(ListContestManager, screenName, true);
