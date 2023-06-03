import AirportShuttleIcon from "@mui/icons-material/AirportShuttle";
import ApartmentSharpIcon from "@mui/icons-material/ApartmentSharp";
import AssignmentOutlinedIcon from "@mui/icons-material/AssignmentOutlined";
import AttachMoneySharpIcon from "@mui/icons-material/AttachMoneySharp";
import BlurOnIcon from "@mui/icons-material/BlurOn";
import CategoryIcon from "@mui/icons-material/Category";
import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import DescriptionIcon from "@mui/icons-material/Description";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import EventNoteIcon from "@mui/icons-material/EventNote";
import FastfoodIcon from "@mui/icons-material/Fastfood";
import FormatListNumberedIcon from "@mui/icons-material/FormatListNumbered";
import HomeSharpIcon from "@mui/icons-material/HomeSharp";
import LocalGroceryStoreIcon from "@mui/icons-material/LocalGroceryStore";
import LocalLibraryIcon from "@mui/icons-material/LocalLibrary";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import InboxIcon from "@mui/icons-material/MoveToInbox";
import PeopleIcon from "@mui/icons-material/People";
import PeopleOutlineIcon from "@mui/icons-material/PeopleOutline";
import PersonIcon from "@mui/icons-material/Person";
import StarBorder from "@mui/icons-material/StarBorder";
import StoreIcon from "@mui/icons-material/Store";
import StoreMallDirectorySharpIcon from "@mui/icons-material/StoreMallDirectorySharp";
import StorefrontIcon from "@mui/icons-material/Storefront";
import WarehouseIcon from "@mui/icons-material/Warehouse";
import TeachingIcon from "assets/icons/mathematics.svg";
import { CiEdit } from "react-icons/ci";
import { GiTeacher } from "react-icons/gi";
import { buildMapPathMenu } from "utils/MenuUtils";
import { category } from "./menuconfig/category";
import { delivery } from "./menuconfig/delivery";
import { general } from "./menuconfig/general";
import { sellin } from "./menuconfig/sellin";
import { sellout } from "./menuconfig/sellout";
import { user } from "./menuconfig/user";
import { warehouse } from "./menuconfig/warehouse";
export const MENUS = [];

MENUS.push(general);
MENUS.push(warehouse);
MENUS.push(sellin);
MENUS.push(sellout);
MENUS.push(delivery);
MENUS.push(category);
MENUS.push(user);

export const menuIconMap = new Map();

menuIconMap.set(
  "Schedule",
  <EventNoteIcon />
  //   <img alt="Task Schedule icon" src={TaskScheduleIcon} height={24} width={24} />
);
menuIconMap.set(
  "Teaching",
  <img alt="Teaching icon" src={TeachingIcon} height={24} width={24} />
);
menuIconMap.set("DashboardIcon", <DashboardRoundedIcon />);
menuIconMap.set("GiTeacher", <GiTeacher size={24} />);
menuIconMap.set("InboxIcon", <InboxIcon />);
menuIconMap.set("StarBorder", <StarBorder />);
menuIconMap.set("PeopleIcon", <PeopleIcon />);
menuIconMap.set("AirportShuttleIcon", <AirportShuttleIcon />);
menuIconMap.set("PeopleOutlineIcon", <PeopleOutlineIcon />);
menuIconMap.set("PersonIcon", <PersonIcon />);
menuIconMap.set("FormatListNumberedIcon", <FormatListNumberedIcon />);
menuIconMap.set("DescriptionIcon", <DescriptionIcon />);
menuIconMap.set("DescriptionOutlinedIcon", <DescriptionOutlinedIcon />);
menuIconMap.set("ApartmentSharpIcon", <ApartmentSharpIcon />);
menuIconMap.set("AttachMoneySharpIcon", <AttachMoneySharpIcon />);
menuIconMap.set("StoreMallDirectorySharpIcon", <StoreMallDirectorySharpIcon />);
menuIconMap.set("HomeSharpIcon", <HomeSharpIcon />);
menuIconMap.set("FastfoodIcon", <FastfoodIcon />);
menuIconMap.set("LocalGroceryStoreIcon", <LocalGroceryStoreIcon />);
menuIconMap.set("BlurOnIcon", <BlurOnIcon />);
menuIconMap.set("LocalLibraryIcon", <LocalLibraryIcon />);
menuIconMap.set("AssignmentOutlinedIcon", <AssignmentOutlinedIcon />);
menuIconMap.set("ManageAccountsIcon", <ManageAccountsIcon />);
menuIconMap.set("CiEdit", <CiEdit />);
// WMS
menuIconMap.set("CategoryIcon", <CategoryIcon />);
menuIconMap.set("WarehouseIcon", <WarehouseIcon />);
menuIconMap.set("Sellin", <StorefrontIcon />);
menuIconMap.set("Sellout", <StoreIcon />);
menuIconMap.set("Delivery", <LocalShippingIcon />);
export const mapPathMenu = buildMapPathMenu(MENUS);
