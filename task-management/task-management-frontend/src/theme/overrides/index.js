import MuiDataGrid from "./dataGrid";
import MuiAvatar from "./avatar";
import MuiTypography from "./typography";
import MuiChip from "./chip";
import MuiTimeline from "./timeline";
import MuiCard from "./card";
import MuiDialog from "./dialog";

const Overrides = () => {
  const dataGrid = MuiDataGrid();
  const avatar = MuiAvatar();
  const typography = MuiTypography;
  const chip = MuiChip();
  const timeline = MuiTimeline();
  const card = MuiCard();
  const dialog = MuiDialog();

  return {
    ...dataGrid,
    ...avatar,
    ...typography,
    ...chip,
    ...timeline,
    ...card,
    ...dialog,
  };
};

export default Overrides;
