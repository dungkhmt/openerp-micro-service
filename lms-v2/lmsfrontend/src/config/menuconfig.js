import AirportShuttleIcon from "@material-ui/icons/AirportShuttle";
import ApartmentSharpIcon from "@material-ui/icons/ApartmentSharp";
import AssignmentOutlinedIcon from "@material-ui/icons/AssignmentOutlined";
import AttachMoneySharpIcon from "@material-ui/icons/AttachMoneySharp";
import BlurOnIcon from "@material-ui/icons/BlurOn";
import DashboardRoundedIcon from "@material-ui/icons/DashboardRounded";
import DescriptionIcon from "@material-ui/icons/Description";
import DescriptionOutlinedIcon from "@material-ui/icons/DescriptionOutlined";
import EventNoteIcon from "@material-ui/icons/EventNote";
import FastfoodIcon from "@material-ui/icons/Fastfood";
import FormatListNumberedIcon from "@material-ui/icons/FormatListNumbered";
import HomeSharpIcon from "@material-ui/icons/HomeSharp";
import LocalGroceryStoreIcon from "@material-ui/icons/LocalGroceryStore";
import LocalLibraryIcon from "@material-ui/icons/LocalLibrary";
import InboxIcon from "@material-ui/icons/MoveToInbox";
import PeopleIcon from "@material-ui/icons/People";
import PeopleOutlineIcon from "@material-ui/icons/PeopleOutline";
import PersonIcon from "@material-ui/icons/Person";
import StarBorder from "@material-ui/icons/StarBorder";
import StoreMallDirectorySharpIcon from "@material-ui/icons/StoreMallDirectorySharp";
import TerminalIcon from '@mui/icons-material/Terminal';
import CodeIcon from '@mui/icons-material/Code';
import React from "react";
import {GiTeacher} from "react-icons/gi";
import {RiCodeSSlashLine} from "react-icons/ri";
import {FaUsers} from "react-icons/fa";
import {SiManageiq} from "react-icons/si";
import TeachingIcon from "../assets/icons/mathematics.svg";
import {buildMapPathMenu} from "../utils/MenuUtils";
import {eduLearningManagement} from "./menuconfig/classmanagement/student";
import {eduTeachingManagement} from "./menuconfig/classmanagement/teacher";
import {DataAdministration} from "./menuconfig/dataadmin";
import {ThesisDefensePlanManagement} from "./menuconfig/Thesis_defense_plan_managenment";
import { ExamMenu } from "./menuconfig/exam";
import {whiteboard} from "./menuconfig/whiteboard";
import {ProgrammingContestMenuStudent, ProgrammingContestMenuTeacher,} from "./menuconfig/ProgramingContest";
import {general} from "./menuconfig/general";
import {teachingassignment} from "./menuconfig/teachingassignment";
import {user} from "./menuconfig/user";

export const MENU_LIST = [];
MENU_LIST.push(general);
MENU_LIST.push(ProgrammingContestMenuTeacher);
MENU_LIST.push(ProgrammingContestMenuStudent);
MENU_LIST.push(eduTeachingManagement);
MENU_LIST.push(eduLearningManagement);

MENU_LIST.push(DataAdministration);
MENU_LIST.push(ThesisDefensePlanManagement);
MENU_LIST.push(whiteboard);
MENU_LIST.push(teachingassignment);
MENU_LIST.push(ExamMenu);

MENU_LIST.push(user);

export const menuIconMap = new Map();
menuIconMap.set(
  "Schedule",
  <EventNoteIcon/>
);
menuIconMap.set(
  "Teaching",
  <img alt="Teaching icon" src={TeachingIcon} height={24} width={24}/>
);
menuIconMap.set("DashboardIcon", <DashboardRoundedIcon/>);
menuIconMap.set("GiTeacher", <GiTeacher size={24}/>);
menuIconMap.set("InboxIcon", <InboxIcon/>);
menuIconMap.set("StarBorder", <StarBorder/>);
menuIconMap.set("PeopleIcon", <PeopleIcon/>);
menuIconMap.set("AirportShuttleIcon", <AirportShuttleIcon/>);
menuIconMap.set("PeopleOutlineIcon", <PeopleOutlineIcon/>);
menuIconMap.set("PersonIcon", <PersonIcon/>);
menuIconMap.set("FormatListNumberedIcon", <FormatListNumberedIcon/>);
menuIconMap.set("DescriptionIcon", <DescriptionIcon/>);
menuIconMap.set("DescriptionOutlinedIcon", <DescriptionOutlinedIcon/>);
menuIconMap.set("ApartmentSharpIcon", <ApartmentSharpIcon/>);
menuIconMap.set("AttachMoneySharpIcon", <AttachMoneySharpIcon/>);
menuIconMap.set("StoreMallDirectorySharpIcon", <StoreMallDirectorySharpIcon/>);
menuIconMap.set("HomeSharpIcon", <HomeSharpIcon/>);
menuIconMap.set("FastfoodIcon", <FastfoodIcon/>);
menuIconMap.set("LocalGroceryStoreIcon", <LocalGroceryStoreIcon/>);
menuIconMap.set("BlurOnIcon", <BlurOnIcon/>);
menuIconMap.set("GiTeacher", <GiTeacher size={24}/>);
menuIconMap.set("LocalLibraryIcon", <LocalLibraryIcon/>);
menuIconMap.set("DataManagementIcon", <SiManageiq/>);
menuIconMap.set("ProgrammingIcon", <TerminalIcon/>)
menuIconMap.set("CodeIcon", <RiCodeSSlashLine/>)
menuIconMap.set("UsersIcon", <FaUsers/>)
menuIconMap.set("AssignmentOutlinedIcon", <AssignmentOutlinedIcon/>);

export const mapPathMenu = buildMapPathMenu(MENU_LIST);
