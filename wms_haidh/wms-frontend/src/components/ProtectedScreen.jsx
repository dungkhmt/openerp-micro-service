import PrimaryButton from "./button/PrimaryButton";
import withScreenSecurity from "./common/withScreenSecurity.jsx";

function ProtectedScreen({ screenAuthorization }) {
  return (
    <div>
      {screenAuthorization.has("SCR_PROTECTED.BTN_1.VIEW") && (
        <PrimaryButton
          sx={{ mr: 2 }}
          disabled={!screenAuthorization.has("SCR_PROTECTED.BTN_1.CLICK")}
        >
          Only user with role ADMIN can click me
        </PrimaryButton>
      )}
    </div>
  );
}

const SCR_ID = "SCR_PROTECTED";
export default withScreenSecurity(ProtectedScreen, SCR_ID, true);
