import AirportShuttleIcon from "@mui/icons-material/AirportShuttle";
import ApartmentSharpIcon from "@mui/icons-material/ApartmentSharp";
import AssessmentIcon from '@mui/icons-material/Assessment';
import AssignmentOutlinedIcon from "@mui/icons-material/AssignmentOutlined";
import AttachMoneySharpIcon from "@mui/icons-material/AttachMoneySharp";
import BlurOnIcon from "@mui/icons-material/BlurOn";
import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import DescriptionIcon from "@mui/icons-material/Description";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import EventNoteIcon from "@mui/icons-material/EventNote";
import FastfoodIcon from "@mui/icons-material/Fastfood";
import FormatListNumberedIcon from "@mui/icons-material/FormatListNumbered";
import HomeIcon from '@mui/icons-material/Home';
import HomeSharpIcon from "@mui/icons-material/HomeSharp";
import LibraryBooksIcon from '@mui/icons-material/LibraryBooks';
import LocalGroceryStoreIcon from "@mui/icons-material/LocalGroceryStore";
import LocalLibraryIcon from "@mui/icons-material/LocalLibrary";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import MonitorIcon from '@mui/icons-material/Monitor';
import InboxIcon from "@mui/icons-material/MoveToInbox";
import PeopleIcon from "@mui/icons-material/People";
import PeopleOutlineIcon from "@mui/icons-material/PeopleOutline";
import PersonIcon from "@mui/icons-material/Person";
import SettingsIcon from '@mui/icons-material/Settings';
import StarBorder from "@mui/icons-material/StarBorder";
import StoreMallDirectorySharpIcon from "@mui/icons-material/StoreMallDirectorySharp";
import TeachingIcon from "assets/icons/mathematics.svg";
import InfoIcon from "@mui/icons-material/Info";
import BusinessIcon from "@mui/icons-material/Business";
import AnalyticsIcon from "@mui/icons-material/Analytics";
import { CiEdit } from "react-icons/ci";
import { GiTeacher } from "react-icons/gi";
import { buildMapPathMenu } from "utils/MenuUtils";
import { teacher } from "./menuconfig/teacher";
import { user } from "./menuconfig/user";
// import {schedule} from "./menuconfig/schedule"
import { scheduleInformation } from "./menuconfig/scheduleInformation";
import {wms_purchase} from "./menuconfig/wms_purchase";
import {wms_sales} from "./menuconfig/wms_sales";
import {wms_logistics} from "./menuconfig/wms_logistics";


// import {schedulePerformance} from "./menuconfig/schedulePerformance"
import { courseTimeTabling } from "./menuconfig/courseTimeTabling";
import { generalTimeTabling } from "./menuconfig/generalTimeTabling";
import { ExamTimeTabling } from "./menuconfig/examTimeTabling";

import { firstYearTimeTabling } from "./menuconfig/firstYearTimeTabling";
import { computerLabTimeTabling } from "./menuconfig/computerLabTimeTabling";
import { taRecruitment } from "./menuconfig/taRecruitment";
import {
  ThesisDefensePlanManagement,
  ThesisDefensePlanStudent,
} from "./menuconfig/Thesis_defense_plan_managenment";
import { Schedule } from "@mui/icons-material/";
import { AssetManagementAsset } from "./menuconfig/assetManagement";
import {TrainingFrogCourse} from "./menuconfig/trainingprogcourse";


export const MENUS = [];

// MENUS.push(general);
// MENUS.push(user);
// MENUS.push(teacher);
//MENUS.push(schedule);
MENUS.push(scheduleInformation);


// MENUS.push(schedulePerformance);
//MENUS.push(courseTimeTabling);
MENUS.push(generalTimeTabling);
MENUS.push(ExamTimeTabling);
MENUS.push(wms_purchase);
MENUS.push(wms_sales);
MENUS.push(wms_logistics);




//MENUS.push(firstYearTimeTabling);
MENUS.push(computerLabTimeTabling);
MENUS.push(taRecruitment);
//MENUS.push(ThesisDefensePlanManagement);
//MENUS.push(ThesisDefensePlanStudent);
//MENUS.push(AssetManagementAsset);
//MENUS.push(TrainingFrogCourse);


export const menuIconMap = new Map();

menuIconMap.set("ScheduleIcon", <EventNoteIcon />);
menuIconMap.set("AnalyticsIcon", <AnalyticsIcon />);
menuIconMap.set(
  "Teaching",
  <img alt="Teaching icon" src={TeachingIcon} height={24} width={24} />
);
menuIconMap.set("BusinessIcon", <BusinessIcon />);
menuIconMap.set("InfoIcon", <InfoIcon />);
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
menuIconMap.set("Schedule", <Schedule />);
menuIconMap.set("LibraryBookIcon", <LibraryBooksIcon/>);
menuIconMap.set("MonitorIcon", <MonitorIcon/>);
menuIconMap.set("HomeIcon", <HomeIcon/>);
menuIconMap.set("SettingsIcon", <SettingsIcon/>);
menuIconMap.set("AssessmentIcon", <AssessmentIcon/>);
export const mapPathMenu = buildMapPathMenu(MENUS);
