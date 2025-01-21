//import { makeStyles } from "@mui/styles"; //"@material-ui/core/styles";
import {makeStyles} from "@material-ui/core/styles";
import {
  Autocomplete,
  Avatar,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Popper,
  Stack,
  TextField,
  Tooltip,
} from "@mui/material";
import {autocompleteClasses} from "@mui/material/Autocomplete";
import {styled} from "@mui/material/styles";
import {debounce} from "@mui/material/utils";
import {request} from "api";
import PrimaryButton from "component/button/PrimaryButton";
import StyledSelect from "component/select/StyledSelect";
import {getTextAvatar} from "layout/account/AccountButton";
import {isEmpty, trim} from "lodash";
import {useEffect, useMemo, useState} from "react";
import {successNoti} from "utils/notification";
import UploadUserToContestDialog from "./UploadUserToContestDialog";
import UploadUserUpdateFullNameContestDialog from "./UploadUserUpdateFullNameContestDialog";


// https://mui.com/material-ui/react-avatar/#letter-avatars
function stringToColor(string) {
  if (!string) return "#000";
  let hash = 0;
  let i;

  /* eslint-disable no-bitwise */
  for (i = 0; i < string.length; i += 1) {
    hash = string.charCodeAt(i) + ((hash << 5) - hash);
  }

  let color = "#";

  for (i = 0; i < 3; i += 1) {
    const value = (hash >> (i * 8)) & 0xff;
    color += `00${value.toString(16)}`.slice(-2);
  }
  /* eslint-enable no-bitwise */

  return color;
}

export function stringAvatar(id, name) {
  return {
    children: getTextAvatar(name)?.toLocaleUpperCase(),
    sx: {
      bgcolor: stringToColor(id),
    },
  };
}

const StyledAutocompletePopper = styled(Popper)(({ theme }) => ({
  [`& .${autocompleteClasses.paper}`]: {
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
    margin: 0,
    padding: 8,
    borderRadius: 8,
    // backgroundColor: "black",
  },
  [`& .${autocompleteClasses.listbox}`]: {
    padding: 0,
    // backgroundColor: "orange",
    [`& .${autocompleteClasses.option}`]: {
      padding: "0px 8px",
      borderRadius: 8,
      // backgroundColor: "red",
      "&:hover": {
        backgroundColor: "#eeeeee",
      },
    },
  },
}));

export function PopperComponent(props) {
  // console.log(props);
  // const { disablePortal, anchorEl, open, ...other } = props;
  return <StyledAutocompletePopper {...props} />;
}

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 480,
    minHeight: 64,
  },
  btn: { margin: "4px 8px" },
}));

const roles = [
  {
    label: "Participant",
    value: "PARTICIPANT",
  },
  {
    label: "Manager",
    value: "MANAGER",
  },
  {
    label: "Owner",
    value: "OWNER",
  },
];

const defaultPageSize = 10;

export default function AddMember2Contest(props) {
  const contestId = props.contestId;

  const classes = useStyles();
  const [selectedRole, setSelectedRole] = useState(roles[0].value);

  //
  const [openUploadDialog, setOpenUploadDialog] = useState(false);
  const [openUploadToUpdateUserFullnameDialog, setOpenUploadToUpdateUserFullnameDialog] = useState(false);

  //
  const [value, setValue] = useState(undefined);
  const [options, setOptions] = useState([]);
  // const [roles, setRoles] = useState([]);
  const [keyword, setKeyword] = useState("");

  const delayedSearch = useMemo(
    () =>
      debounce(({ keyword, exclude }, callback) => {
        search(
          { keyword, exclude, pageSize: defaultPageSize, page: 1 },
          callback
        );
      }, 400),
    []
  );

  function search({ keyword, exclude, pageSize, page }, callback) {
    request(
      "get",
      // "/contests/" + contestId + "/users" +
      `/users?size=${pageSize}&page=${page - 1}&keyword=${keyword}${
        exclude
          ? exclude.map((user) => "&exclude=" + user.userName).join("")
          : ""
      }`,
      (res) => {
        const data = res.data.content.map((e) => {
          const user = {
            userName: e.userLoginId,
            fullName: `${e.firstName || ""} ${e.lastName || ""}`,
          };

          if (isEmpty(trim(user.fullName))) {
            user.fullName = "Anonymous";
          }

          return user;
        });

        callback(data);
      }
    );
  }

  function onAddMembers() {
    let body = {
      userIds: value.map((user) => user.userName),
      role: selectedRole,
    };

    request(
      "post",
      `/contests/${contestId}/users`,
      (res) => {
        successNoti("Users were successfully added", 3000);
        props.onAddedSuccessfully();
      },
      {},
      body
    );
  }

  useEffect(() => {
    let active = true;

    // if (keyword === "" && value) {
    //   setOptions(value ? [value] : []);
    //   return undefined;
    // }

    // console.log(value);
    const excludeIds = value;
    delayedSearch({ keyword, exclude: excludeIds }, (results) => {
      if (active) {
        let newOptions = [];

        // console.log("before combine, value", value);
        // combine exist option with new options
        // if (value) {
        //   newOptions = value;
        // }

        if (results) {
          newOptions = [...newOptions, ...results];
        }

        // console.log("NEWOPS", newOptions);
        setOptions(newOptions);
      }
    });

    return () => {
      active = false;
    };
  }, [value, keyword, delayedSearch]);

  return (
    <>
      <Stack
        spacing={3}
        alignItems={"flex-start"}
        sx={{
          p: 2,
          mb: 2,
          backgroundColor: "#ffffff",
          boxShadow: 1,
          borderRadius: 2,
        }}
      >
        <Autocomplete
          id="add-members"
          multiple
          fullWidth
          size="small"
          PopperComponent={PopperComponent}
          getOptionLabel={(option) => {
            // console.log("getOptionLabel with option", option);
            return option.fullName || "";
          }}
          filterOptions={(x) => x} // disable filtering on client
          options={options}
          // autoComplete
          // includeInputInList
          // filterSelectedOptions
          // value={value}
          noOptionsText="No matches found"
          onChange={(event, newValue) => {
            // console.log("onChange with new value:", newValue);
            setOptions(newValue ? [newValue, ...options] : options);
            setValue(newValue);
          }}
          onInputChange={(event, newInputValue, reason) => {
            setKeyword(newInputValue);
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Users"
              placeholder="Search by id or name"
              inputProps={{
                ...params.inputProps,
                autoComplete: "new-password", // disable autocomplete and autofill
              }}
            />
          )}
          renderOption={(props, option) => {
            // console.log(props);
            // console.log(option);
            if (Array.isArray(option) && option.length === 0) return null;
            else
              return (
                // props include key with value = option.fullName so must override
                <ListItem {...props} key={option.userName} sx={{ p: 0 }}>
                  <ListItemAvatar>
                    <Avatar
                      alt="account avatar"
                      {...stringAvatar(option.userName, option.fullName)}
                    />
                  </ListItemAvatar>
                  <ListItemText
                    primary={option.fullName}
                    secondary={`${option.userName}`}
                  />
                </ListItem>
              );
          }}
        />
        <StyledSelect
          required
          key={"Role"}
          label="Role"
          options={roles}
          value={selectedRole}
          onChange={(e) => {
            setSelectedRole(e.target.value);
          }}
        />
        <Stack direction={"row"} spacing={2}>
          <PrimaryButton
            disabled={!value?.length > 0}
            className={classes.btn}
            onClick={onAddMembers}
          >
            Add
          </PrimaryButton>
          <Tooltip arrow title="Add members by uploading Excel file">
            <PrimaryButton
              onClick={() => {
                setOpenUploadDialog(true);
              }}
            >
              Import
            </PrimaryButton>
          </Tooltip>
          <Tooltip arrow title="Add members by uploading Excel file">
            <PrimaryButton
              onClick={() => {
                setOpenUploadToUpdateUserFullnameDialog(true);
              }}
            >
              Import update fullname
            </PrimaryButton>
          </Tooltip>
        </Stack>
      </Stack>
      <UploadUserToContestDialog
        isOpen={openUploadDialog}
        contestId={contestId}
        onClose={() => {
          setOpenUploadDialog(false);
          props.onAddedSuccessfully();
        }}
      />
      <UploadUserUpdateFullNameContestDialog
        isOpen={openUploadToUpdateUserFullnameDialog}
        contestId={contestId}
        onClose={() => {
          setOpenUploadToUpdateUserFullnameDialog(false);
          props.onAddedSuccessfully();
        }}
      />
    </>
  );
}
