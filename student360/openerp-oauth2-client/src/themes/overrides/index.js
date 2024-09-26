import AppBar from "./AppBar";
import Autocomplete from "./Autocomplete";
import Backdrop from "./Backdrop";
import BaseLine from "./BaseLine";
import Button from "./Button";
import Card from "./Card";
import CheckBox from "./Checkbox";
import Chip from "./Chip";
import Dialog from "./Dialog";
import Drawer from "./Drawer";
import Input from "./Input";
import Menu from "./Menu";
import PaginationItem from "./PaginationItem";
import Popover from "./Popover";
import Tab from "./Tab";
import Tooltip from "./Tooltip";
import Typography from "./Typography";

export default function ComponentsOverrides(theme) {
  return Object.assign(
    AppBar(theme),
    Autocomplete(theme),
    Backdrop(theme),
    BaseLine(theme),
    Button(theme),
    Card(theme),
    Dialog(theme),
    Drawer(theme),
    // Input(theme),
    Tooltip(theme),
    Typography(theme),
    PaginationItem(theme),
    Popover(theme),
    Tab(theme),
    CheckBox(theme),
    Chip(theme),
    Menu(theme)
  );
}
