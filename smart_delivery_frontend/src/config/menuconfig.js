import AirportShuttleIcon from "@mui/icons-material/AirportShuttle";
import ApartmentSharpIcon from "@mui/icons-material/ApartmentSharp";
import AssignmentOutlinedIcon from "@mui/icons-material/AssignmentOutlined";
import AttachMoneySharpIcon from "@mui/icons-material/AttachMoneySharp";
import BlurOnIcon from "@mui/icons-material/BlurOn";
import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import DescriptionIcon from "@mui/icons-material/Description";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import EventNoteIcon from "@mui/icons-material/EventNote";
import FastfoodIcon from "@mui/icons-material/Fastfood";
import FormatListNumberedIcon from "@mui/icons-material/FormatListNumbered";
import HomeSharpIcon from "@mui/icons-material/HomeSharp";
import LocalGroceryStoreIcon from "@mui/icons-material/LocalGroceryStore";
import LocalLibraryIcon from "@mui/icons-material/LocalLibrary";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import InboxIcon from "@mui/icons-material/MoveToInbox";
import PeopleIcon from "@mui/icons-material/People";
import PeopleOutlineIcon from "@mui/icons-material/PeopleOutline";
import PersonIcon from "@mui/icons-material/Person";
import StarBorder from "@mui/icons-material/StarBorder";
import StoreMallDirectorySharpIcon from "@mui/icons-material/StoreMallDirectorySharp";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import InventoryIcon from "@mui/icons-material/Inventory";
import ShowChartIcon from "@mui/icons-material/ShowChart";
import BarChartIcon from "@mui/icons-material/BarChart";
import WarehouseIcon from "@mui/icons-material/Warehouse";
import GroupIcon from "@mui/icons-material/Group";
import TeachingIcon from "assets/icons/mathematics.svg";
import { CiEdit } from "react-icons/ci";
import { GiTeacher } from "react-icons/gi";
import { buildMapPathMenu } from "utils/MenuUtils";
import { general } from "./menuconfig/general";
import { user } from "./menuconfig/user";
import {demo} from "./menuconfig/demo";
import {hub} from "./menuconfig/hub";
import {order} from "./menuconfig/order";
import {humanResources} from "./menuconfig/humanResources";
import {statistics} from "./menuconfig/statistics";
import {confirmOrder} from "./menuconfig/confirmOrder";
import {middleMile} from "./menuconfig/middleMile";
import {config} from "./menuconfig/config";

export const MENUS = [];

MENUS.push(general);
MENUS.push(hub);
MENUS.push(order);
MENUS.push(humanResources);
MENUS.push(confirmOrder);
MENUS.push(middleMile);
MENUS.push(statistics);
MENUS.push(config);


export const menuIconMap = new Map();

menuIconMap.set(
  "Schedule",
  <EventNoteIcon />
  //   <img alt="Task Schedule icon" src={TaskScheduleIcon} height={24} width={24} />
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
menuIconMap.set("LocalGxroceryStoreIcon", <LocalGroceryStoreIcon />);
menuIconMap.set("BlurOnIcon", <BlurOnIcon />);
menuIconMap.set("LocalLibraryIcon", <LocalLibraryIcon />);
menuIconMap.set("AssignmentOutlinedIcon", <AssignmentOutlinedIcon />);
menuIconMap.set("ManageAccountsIcon", <ManageAccountsIcon />);
menuIconMap.set("CiEdit", <CiEdit />);
menuIconMap.set("ShoppingCartIcon", <ShoppingCartIcon />);
menuIconMap.set("ReceiptIcon", <ReceiptLongIcon />);
menuIconMap.set("InventoryIcon", <InventoryIcon />);
menuIconMap.set("ShowChartIcon", <ShowChartIcon />);
menuIconMap.set("BarChartIcon", <BarChartIcon />);
menuIconMap.set("WarehouseIcon", <WarehouseIcon />);
menuIconMap.set("GroupIcon", <GroupIcon />);

export const mapPathMenu = buildMapPathMenu(MENUS);
