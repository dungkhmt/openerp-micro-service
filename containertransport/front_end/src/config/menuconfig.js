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
import FireTruckRoundedIcon from '@mui/icons-material/FireTruckRounded';
import AddIcon from '@mui/icons-material/Add';
import ControlPointIcon from '@mui/icons-material/ControlPoint';
import ListAltIcon from '@mui/icons-material/ListAlt';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import RemoveRedEyeIcon from '@mui/icons-material/RemoveRedEye';
import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh';
import SearchIcon from '@mui/icons-material/Search';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import TeachingIcon from "assets/icons/mathematics.svg";
import ContainerIcon from "assets/icons/container.svg";
import TrailerIcon from "assets/icons/trailer.svg";
import FacilityIcon from "assets/icons/facility.svg";
import ShipmentIcon from "assets/icons/shipmentIcon.svg";
import { CiEdit } from "react-icons/ci";
import { GiTeacher } from "react-icons/gi";
import { buildMapPathMenu } from "utils/MenuUtils";
import { general } from "./menuconfig/general";
import { student } from "./menuconfig/student";
import { teacher } from "./menuconfig/teacher";
import { user } from "./menuconfig/user";
import { truck } from "./menuconfig/truck";
import { trailer } from "./menuconfig/trailer";
import { container } from "./menuconfig/container";
import { facility } from "./menuconfig/facility";
import { shipment } from "./menuconfig/shipment";
import { order } from "./menuconfig/order";
import { trip } from "./menuconfig/trip";

export const MENUS = [];

MENUS.push(general);
MENUS.push(user);
// MENUS.push(teacher);
MENUS.push(order);
MENUS.push(truck);
MENUS.push(trailer);
MENUS.push(container);
MENUS.push(facility);
MENUS.push(shipment);
MENUS.push(trip)


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
menuIconMap.set(
  "Container",
  <img alt="Container icon" src={ContainerIcon} height={24} width={24} />
);
menuIconMap.set(
  "Trailer",
  <img alt="Trailer icon" src={TrailerIcon} height={24} width={30} />
);
menuIconMap.set(
  "Facility",
  <img alt="Facility icon" src={FacilityIcon} height={24} width={30} />
);
menuIconMap.set(
  "Shipment",
  <img alt="Shipment icon" src={ShipmentIcon} height={24} width={30} />
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
menuIconMap.set("Truck", <FireTruckRoundedIcon />);
menuIconMap.set("AddIcon", <AddIcon />);
menuIconMap.set("ControlPointIcon", <ControlPointIcon />);
menuIconMap.set("OrderIcon", <ListAltIcon />);
menuIconMap.set("ArrowBackIosIcon", <ArrowBackIosIcon />);
menuIconMap.set("DeleteForeverIcon", <DeleteForeverIcon />);
menuIconMap.set("RemoveRedEyeIcon", <RemoveRedEyeIcon />);
menuIconMap.set("AutoFixHighIcon", <AutoFixHighIcon />);
menuIconMap.set("SearchIcon", <SearchIcon />);
menuIconMap.set("ArrowDropDownIcon", <ArrowDropDownIcon />);

export const mapPathMenu = buildMapPathMenu(MENUS);

export const typeOrderMap = new Map();
typeOrderMap.set("IF", "Inbound Full");
typeOrderMap.set("OF", "Outbound Full");
typeOrderMap.set("IE", "Inbound Empty");
typeOrderMap.set("OE", "Outbound Empty");

export const roles = new Map();
roles.set("Customer", "TMS_CUSTOMER");
roles.set("Driver", "TMS_DRIVER");

export const facilityType = new Map();
facilityType.set("Container", "Container");
facilityType.set("Truck", "Truck");
facilityType.set("Trailer", "Trailer");

export const facilityStatus = new Map();
facilityStatus.set("AVAILABLE", "AVAILABLE");
facilityStatus.set("UNAVAILABLE", "UNAVAILABLE");

export const tripItemType = new Map();
tripItemType.set("Order", "Order");
tripItemType.set("Truck", "Truck");
tripItemType.set("Trailer", "Trailer");

export const colorStatus = new Map();
colorStatus.set("Container", "success");
colorStatus.set("Truck", "secondary");
colorStatus.set("Trailer", "primary");
colorStatus.set("WAITING_SCHEDULER", "primary");
colorStatus.set("AVAILABLE", "success");
colorStatus.set("SCHEDULED", "secondary");
colorStatus.set("EXECUTING", "secondary");
colorStatus.set("DONE", "success");