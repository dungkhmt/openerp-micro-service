/* eslint-disable react/prop-types */
import { useEffect, useCallback, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import List from "@mui/material/List";
import MuiDialog from "@mui/material/Dialog";
import ListItem from "@mui/material/ListItem";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import useMediaQuery from "@mui/material/useMediaQuery";
import { styled, useTheme } from "@mui/material/styles";
import ListItemButton from "@mui/material/ListItemButton";
import InputAdornment from "@mui/material/InputAdornment";
import MuiAutocomplete from "@mui/material/Autocomplete";

import { searchData } from "./searchData";
import { Icon } from "@iconify/react";

const defaultSuggestionsData = [
  {
    category: "Phổ biến",
    suggestions: [
      {
        icon: "octicon:project-roadmap-16",
        suggestion: "Danh sách dự án",
        link: "/projects",
      },
      {
        icon: "mdi:format-list-numbered",
        suggestion: "Danh sách công việc được giao",
        link: "/tasks/assign-me",
      },
      {
        icon: "mdi:format-list-bulleted-square",
        suggestion: "Danh sách công việc của tôi",
        link: "/tasks/created-by-me",
      },
      {
        icon: "material-symbols:dashboard-outline",
        suggestion: "Dashboard",
        link: "/dashboard",
      },
    ],
  },
  {
    category: "Quản lý dự án",
    suggestions: [
      {
        icon: "octicon:project-roadmap-16",
        suggestion: "Danh sách dự án",
        link: "/projects",
      },
      {
        icon: "mdi:plus-circle-outline",
        suggestion: "Thêm mới dự án",
        link: "/projects/new",
      },
    ],
  },
  {
    category: "Quản lý công việc",
    suggestions: [
      {
        icon: "mdi:format-list-numbered",
        suggestion: "Danh sách công việc được giao",
        link: "/tasks/assign-me",
      },
      {
        icon: "mdi:format-list-bulleted-square",
        suggestion: "Danh sách công việc đã tạo",
        link: "/tasks/created-by-me",
      },
    ],
  },
];

const categoryTitle = {
  projects: "Quản lý dự án",
  tasks: "Quản lý công việc",
  dashboard: "Dashboard",
};

const Autocomplete = styled(MuiAutocomplete)(({ theme }) => ({
  "& fieldset": {
    border: 0,
  },
  "& + .MuiAutocomplete-popper": {
    "& .MuiAutocomplete-listbox": {
      paddingTop: 0,
      height: "100%",
      maxHeight: "inherit",
      "& .MuiListSubheader-root": {
        top: 0,
        fontWeight: 400,
        lineHeight: "15px",
        fontSize: "0.75rem",
        letterSpacing: "1px",
        color: theme.palette.text.disabled,
      },
    },
    "& .MuiAutocomplete-paper": {
      border: 0,
      height: "100%",
      borderRadius: 0,
      boxShadow: "none",
    },
    "& .MuiListItem-root.suggestion": {
      padding: 0,
      "& .MuiListItemSecondaryAction-root": {
        display: "flex",
      },
      "&.Mui-focused.Mui-focusVisible, &:hover": {
        backgroundColor: theme.palette.action.hover,
      },
      "& .MuiListItemButton-root: hover": {
        backgroundColor: "transparent",
      },
      "&:not(:hover)": {
        "& .MuiListItemSecondaryAction-root": {
          display: "none",
        },
        "&.Mui-focused, &.Mui-focused.Mui-focusVisible:not(:hover)": {
          "& .MuiListItemSecondaryAction-root": {
            display: "flex",
          },
        },
        [theme.breakpoints.down("sm")]: {
          "&.Mui-focused:not(.Mui-focusVisible) .MuiListItemSecondaryAction-root":
            {
              display: "none",
            },
        },
      },
    },
    "& .MuiAutocomplete-noOptions": {
      display: "grid",
      minHeight: "100%",
      alignItems: "center",
      flexDirection: "column",
      justifyContent: "center",
      padding: theme.spacing(10),
    },
  },
}));

// ** Styled Dialog component
const Dialog = styled(MuiDialog)({
  "& .MuiBackdrop-root": {
    backdropFilter: "blur(4px)",
  },
  "& .MuiDialog-paper": {
    overflow: "hidden",
    "&:not(.MuiDialog-paperFullScreen)": {
      height: "100%",
      maxHeight: 550,
    },
  },
});

const NoResult = ({ value, setOpenDialog }) => {
  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        flexDirection: "column",
        justifyContent: "center",
      }}
    >
      <Box sx={{ mb: 2.5, color: "text.primary" }}>
        <Icon icon="mdi:file-remove-outline" fontSize="5rem" />
      </Box>
      <Typography variant="h6" sx={{ mb: 11.5, wordWrap: "break-word" }}>
        Không tìm thấy kết quả cho{" "}
        <Typography
          variant="h6"
          component="span"
          sx={{ wordWrap: "break-word" }}
        >
          {`"${value}"`}
        </Typography>
      </Typography>

      <Typography variant="body2" sx={{ mb: 2.5, color: "text.disabled" }}>
        Thử tìm kiếm với
      </Typography>
      <List sx={{ py: 0 }}>
        <ListItem
          sx={{ py: 2 }}
          disablePadding
          onClick={() => setOpenDialog(false)}
        >
          <Box
            component={Link}
            to="/dashboard"
            sx={{
              display: "flex",
              alignItems: "center",
              textDecoration: "none",
              "&:hover > *": { color: "primary.main" },
            }}
          >
            <Box sx={{ mr: 2.5, display: "flex", color: "text.primary" }}>
              <Icon icon="material-symbols:dashboard-outline" fontSize={20} />
            </Box>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
              Dashboard
            </Typography>
          </Box>
        </ListItem>
        <ListItem
          sx={{ py: 2 }}
          disablePadding
          onClick={() => setOpenDialog(false)}
        >
          <Box
            component={Link}
            to="/projects"
            sx={{
              display: "flex",
              alignItems: "center",
              textDecoration: "none",
              "&:hover > *": { color: "primary.main" },
            }}
          >
            <Box sx={{ mr: 2.5, display: "flex", color: "text.primary" }}>
              <Icon icon="octicon:project-roadmap-16" fontSize={20} />
            </Box>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
              Danh sách dự án
            </Typography>
          </Box>
        </ListItem>
      </List>
    </Box>
  );
};

const DefaultSuggestions = ({ setOpenDialog }) => {
  return (
    <Grid container spacing={6} sx={{ ml: 0 }}>
      {defaultSuggestionsData.map((item, index) => (
        <Grid item xs={12} sm={6} key={index}>
          <Typography
            component="p"
            variant="overline"
            sx={{ lineHeight: 1.25, color: "text.disabled" }}
          >
            {item.category}
          </Typography>
          <List>
            {item.suggestions.map((suggestionItem, index2) => (
              <ListItem key={index2} sx={{ py: 2 }} disablePadding>
                <Box
                  component={Link}
                  to={suggestionItem.link}
                  onClick={() => setOpenDialog(false)}
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    "& svg": { mr: 2.5 },
                    color: "text.primary",
                    textDecoration: "none",
                    "&:hover > *": { color: "primary.main" },
                  }}
                >
                  <Icon icon={suggestionItem.icon} fontSize={20} />
                  <Typography variant="body2" sx={{ color: "text.primary" }}>
                    {suggestionItem.suggestion}
                  </Typography>
                </Box>
              </ListItem>
            ))}
          </List>
        </Grid>
      ))}
    </Grid>
  );
};

const AutocompleteComponent = ({ hidden }) => {
  const [isMounted, setIsMounted] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const [options] = useState(searchData);

  const theme = useTheme();
  const navigate = useNavigate();
  const wrapper = useRef(null);
  const fullScreenDialog = useMediaQuery(theme.breakpoints.down("sm"));

  useEffect(() => {
    if (!openDialog) {
      setSearchValue("");
    }
  }, [openDialog]);

  useEffect(() => {
    setIsMounted(true);

    return () => setIsMounted(false);
  }, []);

  // Handle click event on a list item in search result
  const handleOptionClick = (obj) => {
    setSearchValue("");
    setOpenDialog(false);
    if (obj.url) {
      navigate(obj.url);
    }
  };

  // Handle ESC & shortcut keys keydown events
  const handleKeydown = useCallback(
    (event) => {
      // ** Shortcut keys to open searchbox (Ctrl + I)
      if (!openDialog && event.ctrlKey && event.which === 73) {
        setOpenDialog(true);
      }
    },
    [openDialog]
  );

  // Handle shortcut keys keyup events
  const handleKeyUp = useCallback(
    (event) => {
      // ** ESC key to close searchbox
      if (openDialog && event.keyCode === 27) {
        setOpenDialog(false);
      }
    },
    [openDialog]
  );

  useEffect(() => {
    document.addEventListener("keydown", handleKeydown);
    document.addEventListener("keyup", handleKeyUp);

    return () => {
      document.removeEventListener("keydown", handleKeydown);
      document.removeEventListener("keyup", handleKeyUp);
    };
  }, [handleKeyUp, handleKeydown]);

  if (!isMounted) {
    return null;
  } else {
    return (
      <Box
        ref={wrapper}
        onClick={() => !openDialog && setOpenDialog(true)}
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          height: "28px",
          backgroundColor: "#ffffff1a",
          borderRadius: "6px",
          minWidth: "380px",
          border: "0.8px solid #0000",
          color: "#ddeeddee",
          gap: "5px",
          cursor: "pointer",
          padding: "0px 34px",
          position: "relative",

          "&:hover": {
            backgroundColor: "#ffffff2a",
          },
        }}
      >
        <Icon icon="material-symbols:search" />
        <Typography color="#ddeeddee" fontSize="14px">
          Tìm kiếm...
        </Typography>
        <Typography
          color="#ddeeddee"
          fontSize="14px"
          sx={{ position: "absolute", right: 8 }}
        >
          Ctrl+I
        </Typography>
        {openDialog && (
          <Dialog
            fullWidth
            open={openDialog}
            fullScreen={fullScreenDialog}
            onClose={() => setOpenDialog(false)}
          >
            <Box sx={{ top: 0, width: "100%", position: "sticky" }}>
              <Autocomplete
                autoHighlight
                disablePortal
                options={options}
                id="appBar-search"
                isOptionEqualToValue={() => true}
                onInputChange={(event, value) => setSearchValue(value)}
                onChange={(event, obj) => handleOptionClick(obj)}
                noOptionsText={
                  <NoResult value={searchValue} setOpenDialog={setOpenDialog} />
                }
                getOptionLabel={(option) => option.title || ""}
                groupBy={(option) =>
                  searchValue.length ? categoryTitle[option.category] : ""
                }
                sx={{
                  "& + .MuiAutocomplete-popper": {
                    ...(searchValue.length
                      ? {
                          overflow: "auto",
                          maxHeight: "calc(100vh - 69px)",
                          borderTop: `1px solid ${theme.palette.divider}`,
                          height: fullScreenDialog ? "calc(100vh - 69px)" : 481,
                          "& .MuiListSubheader-root": {
                            p: theme.spacing(3.75, 6, 0.75),
                          },
                        }
                      : {
                          "& .MuiAutocomplete-listbox": { pb: 0 },
                        }),
                  },
                }}
                renderInput={(params) => {
                  return (
                    <TextField
                      {...params}
                      value={searchValue}
                      onChange={(event) => setSearchValue(event.target.value)}
                      inputRef={(input) => {
                        if (input) {
                          if (openDialog) {
                            input.focus();
                          } else {
                            input.blur();
                          }
                        }
                      }}
                      InputProps={{
                        ...params.InputProps,
                        sx: { p: `${theme.spacing(3.75, 6)} !important` },
                        startAdornment: (
                          <InputAdornment
                            position="start"
                            sx={{ color: "text.primary" }}
                          >
                            <Icon icon="mdi:magnify" />
                          </InputAdornment>
                        ),
                        endAdornment: (
                          <InputAdornment
                            position="end"
                            onClick={() => setOpenDialog(false)}
                            sx={{
                              display: "flex",
                              cursor: "pointer",
                              alignItems: "center",
                            }}
                          >
                            {!hidden ? (
                              <Typography
                                sx={{ mr: 2.5, color: "text.disabled" }}
                              >
                                [esc]
                              </Typography>
                            ) : null}
                            <IconButton size="small" sx={{ p: 1 }}>
                              <Icon icon="mdi:close" fontSize={20} />
                            </IconButton>
                          </InputAdornment>
                        ),
                      }}
                    />
                  );
                }}
                renderOption={(props, option) => {
                  return searchValue.length ? (
                    <ListItem
                      {...props}
                      key={option.title}
                      className={`suggestion ${props.className}`}
                      onClick={() => handleOptionClick(option)}
                      secondaryAction={
                        <Icon
                          icon="mdi:subdirectory-arrow-left"
                          fontSize={20}
                        />
                      }
                      sx={{
                        "& .MuiListItemSecondaryAction-root": {
                          "& svg": {
                            cursor: "pointer",
                            color: "text.disabled",
                          },
                        },
                      }}
                    >
                      <ListItemButton
                        sx={{
                          py: 2.5,
                          px: `${theme.spacing(6)} !important`,
                          "& svg": { mr: 2.5, color: "text.primary" },
                        }}
                      >
                        <Icon
                          fontSize={20}
                          icon={option.icon || themeConfig.navSubItemIcon}
                        />
                        <Typography
                          variant="body2"
                          sx={{ color: "text.primary" }}
                        >
                          {option.title}
                        </Typography>
                      </ListItemButton>
                    </ListItem>
                  ) : null;
                }}
              />
            </Box>
            {searchValue.length === 0 ? (
              <Box
                sx={{
                  px: 6,
                  display: "grid",
                  overflow: "auto",
                  alignItems: "center",
                  justifyContent: "center",
                  borderTop: `1px solid ${theme.palette.divider}`,
                  height: fullScreenDialog ? "calc(100vh - 69px)" : "70%",
                }}
              >
                <DefaultSuggestions setOpenDialog={setOpenDialog} />
              </Box>
            ) : null}
          </Dialog>
        )}
      </Box>
    );
  }
};

export default AutocompleteComponent;