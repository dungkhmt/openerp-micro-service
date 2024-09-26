import TerminalIcon from "@mui/icons-material/Terminal";
import { GiTeacher } from "react-icons/gi";
import { RiCodeSSlashLine } from "react-icons/ri";
import { FaUsers } from "react-icons/fa";
import { SiManageiq } from "react-icons/si";
import { ReactComponent as TeachingIcon } from "../assets/icons/mathematics.svg";
import { buildMapPathMenu } from "../utils/MenuUtils";
import { general } from "./menuconfig/general";
import { user } from "./menuconfig/user";
import {
  AirportShuttle,
  ApartmentSharp,
  AssignmentOutlined,
  AttachMoneySharp,
  BlurOn,
  DashboardRounded,
  Description,
  DescriptionOutlined,
  EventNote,
  Fastfood,
  FormatListNumbered,
  HomeSharp,
  Inbox,
  LocalGroceryStore,
  LocalLibrary,
  People,
  PeopleOutline,
  Person,
  StarBorder,
  StoreMallDirectorySharp,
} from "@mui/icons-material";
import { backlog } from "./menuconfig/backlog";

export const MENU_LIST = [];
MENU_LIST.push(general);
MENU_LIST.push(backlog);
MENU_LIST.push(user);

export const menuIconMap = new Map();
menuIconMap.set("Schedule", <EventNote />);
menuIconMap.set(
  "Teaching",
  <img alt="Teaching icon" src={TeachingIcon} height={24} width={24} />
);
menuIconMap.set("DashboardIcon", <DashboardRounded />);
menuIconMap.set("GiTeacher", <GiTeacher size={24} />);
menuIconMap.set("InboxIcon", <Inbox />);
menuIconMap.set("StarBorder", <StarBorder />);
menuIconMap.set("PeopleIcon", <People />);
menuIconMap.set("AirportShuttleIcon", <AirportShuttle />);
menuIconMap.set("PeopleOutlineIcon", <PeopleOutline />);
menuIconMap.set("PersonIcon", <Person />);
menuIconMap.set("FormatListNumberedIcon", <FormatListNumbered />);
menuIconMap.set("DescriptionIcon", <Description />);
menuIconMap.set("DescriptionOutlinedIcon", <DescriptionOutlined />);
menuIconMap.set("ApartmentSharpIcon", <ApartmentSharp />);
menuIconMap.set("AttachMoneySharpIcon", <AttachMoneySharp />);
menuIconMap.set("StoreMallDirectorySharpIcon", <StoreMallDirectorySharp />);
menuIconMap.set("HomeSharpIcon", <HomeSharp />);
menuIconMap.set("FastfoodIcon", <Fastfood />);
menuIconMap.set("LocalGroceryStoreIcon", <LocalGroceryStore />);
menuIconMap.set("BlurOnIcon", <BlurOn />);
menuIconMap.set("GiTeacher", <GiTeacher size={24} />);
menuIconMap.set("LocalLibraryIcon", <LocalLibrary />);
menuIconMap.set("DataManagementIcon", <SiManageiq />);
menuIconMap.set("ProgrammingIcon", <TerminalIcon />);
menuIconMap.set("CodeIcon", <RiCodeSSlashLine />);
menuIconMap.set("UsersIcon", <FaUsers />);
menuIconMap.set("AssignmentOutlinedIcon", <AssignmentOutlined />);

export const mapPathMenu = buildMapPathMenu(MENU_LIST);
