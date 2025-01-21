import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import {
  Autocomplete,
  IconButton,
  LinearProgress,
  //Link,
  Paper,
  Popper,
  Stack,
  TextField,
  Tooltip,
} from "@mui/material";
import { Link } from "react-router-dom";
import { autocompleteClasses } from "@mui/material/Autocomplete";
import Box from "@mui/material/Box";
import { styled, useTheme } from "@mui/material/styles";
import { request } from "api";
import PrimaryButton from "component/button/PrimaryButton";
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { infoNoti, successNoti } from "utils/notification";
import { getSubmissionModeFromConstant } from "./Constant";
import ModalAddProblemToContest from "./ModalAddProblemToContest";
import ModalImportProblemsFromContest from "./ModalImportProblemsFromContest";
import ModalUpdateProblemInfoInContest from "./ModalUpdateProblemInfoInContest";
import { getColorLevel } from "./lib";

const StyledAutocompletePopper = styled(Popper)(({ theme }) => ({
  [`& .${autocompleteClasses.paper}`]: {
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
    margin: 0,
    color: "inherit",
    padding: 8,
    borderRadius: 8,
  },
  [`& .${autocompleteClasses.listbox}`]: {
    backgroundColor: theme.palette.mode === "light" ? "#fff" : "#1c2128",
    padding: 0,
    [`& .${autocompleteClasses.option}`]: {
      minHeight: "auto",
      alignItems: "flex-start",
      padding: 8,
      borderRadius: 8,
      // borderBottom: `1px solid  ${
      //   theme.palette.mode === "light" ? " #eaecef" : "#30363d"
      // }`,
      '&[aria-selected="true"]': {
        backgroundColor: "transparent",
      },
      [`&.${autocompleteClasses.focused}, &.${autocompleteClasses.focused}[aria-selected="true"]`]:
        {
          backgroundColor: theme.palette.action.hover,
        },
    },
  },
  // [`&.${autocompleteClasses.popperDisablePortal}`]: {
  //   position: "relative",
  // },
}));

function PopperComponent(props) {
  // console.log(props);
  // const { disablePortal, anchorEl, open, ...other } = props;
  return <StyledAutocompletePopper {...props} />;
}

export function ContestManagerManageProblem(props) {
  const contestId = props.contestId;
  const theme = useTheme();

  const [problems, setProblems] = useState([]);
  const [contestProblems, setContestProblems] = useState([]);
  const [loading, setLoading] = useState(true);

  const [searchProblemValue, setSearchProblemValue] = useState("");
  const [chosenProblem, setChosenProblem] = useState(null);
  const [editingProblem, setEditingProblem] = useState(null);

  const [openModalAddProblem, setOpenModalAddProblem] = useState(false);
  const [openModalUpdateProblem, setOpenModalUpdateProblem] = useState(false);
  const [openModalImportProblem, setOpenModalImportProblem] = useState(false);

  //
  const columns = [
    {
      title: "Name",
      minWidth: 170,
      field: "problemName",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/contest-manager-view-problem/" + contestId + "/" + rowData["problemId"],
          }}
        >
          {rowData["problemName"]}
        
        </Link>
        
      ),
    },
    {
      title: "Name in Contest",
      field: "problemRename",
      minWidth: 170,
    },
    {
      title: "Code in Contest",
      field: "problemRecode",
      minWidth: 170,
    },
    {
      title: "Level",
      render: (problem) => (
        <span style={{ color: getColorLevel(`${problem.levelId}`) }}>
          {`${problem.levelId}`}
        </span>
      ),
    },
    {
      title: "Submission Mode",
      minWidth: 180,
      render: (problem) =>
        getSubmissionModeFromConstant(problem?.submissionMode),
    },
    {
      title: "Created By",
      field: "createdByUserId",
      minWidth: 130,
    },
    {
      title: "",
      render: (problem) => (
        <IconButton
          onClick={() => {
            setEditingProblem(problem);
            setOpenModalUpdateProblem(true);
          }}
          variant="contained"
          color="success"
        >
          <EditIcon />
        </IconButton>
      ),
    },
    {
      title: "",
      render: (problem) => (
        <IconButton
          variant="contained"
          color="error"
          onClick={() => {
            request(
              "delete",
              `/contest-problem?contestId=${contestId}&problemId=${problem.problemId}`,
              () => {
                successNoti("Problem removed from contest", 5000);
                getProblemsInContest();
              },
              {}
            );
          }}
        >
          <DeleteIcon />
        </IconButton>
      ),
    },
  ];

  const getProblems = () => {
    request("get", "/problems/general-info", (res) => {
      setProblems(res.data);
    });
  };

  const getProblemsInContest = () => {
    request("get", "/contests/" + contestId, (res) => {
      setLoading(false);
      setContestProblems(res.data.list);
    });
  };

  useEffect(() => {
    getProblems();
    getProblemsInContest();
  }, []);

  function addNewProblem(newProblem) {
    if (newProblem?.addNew) return;

    if (
      contestProblems.filter(
        (problem) => problem.problemId === newProblem.problemId
      ).length > 0
    ) {
      infoNoti("Problem added", 3000);
      return;
    }
    setChosenProblem(newProblem);
    setOpenModalAddProblem(true);
  }

  const handleAddProblemToContestSuccess = () => {
    successNoti("Problem saved to contest successfully", 5000);
    getProblemsInContest();
  };
  const handleCloseModal = () => {
    setOpenModalAddProblem(false);
    setOpenModalUpdateProblem(false);
  };

  const handleCloseModalImportProblems = () => {
    setOpenModalImportProblem(false);
    getProblemsInContest();
  };

  return (
    <Paper sx={{ p: 2 }}>
      {loading && <LinearProgress />}
      <Stack
        direction={"row"}
        spacing={2}
        alignItems="center"
        justifyContent={"flex-end"}
        sx={{ mb: 1.5 }}
      >
        <Autocomplete
          id="add-problem"
          size="small"
          sx={{ minWidth: "500px", width: "100%" }}
          disableClearable
          inputValue={searchProblemValue}
          onInputChange={(event, newInputValue, reason) => {
            if (reason === "reset") setSearchProblemValue("");
            else setSearchProblemValue(newInputValue);
          }}
          onChange={(event, newValue, reason) => {
            if (
              event.type === "keydown" &&
              event.key === "Backspace" &&
              reason === "removeOption"
            ) {
              return;
            }
            addNewProblem(newValue);
          }}
          PopperComponent={PopperComponent}
          noOptionsText="No matches found"
          renderOption={(props, option, { selected }) => {
            // console.log(props);
            // if (option.addNew === "true") {
            //   return (
            //     <Box {...props} key={"btn-create-new-problem"}>
            //       <PrimaryButton
            //         sx={{
            //           width: "100%",
            //         }}
            //         onClick={() =>
            //           window.open("/programming-contest/create-problem")
            //         }
            //       >
            //         Create new Problem
            //       </PrimaryButton>
            //     </Box>
            //   );
            // } else {
            return (
              <li {...props} key={option.problemId}>
                <Box
                  component="span"
                  sx={{
                    width: 14,
                    height: 14,
                    flexShrink: 0,
                    borderRadius: 1,
                    mr: 1,
                    mt: "2px",
                  }}
                  style={{ backgroundColor: getColorLevel(option.levelId) }}
                />
                <Box
                  sx={{
                    flexGrow: 1,
                    "& span": {
                      color:
                        theme.palette.mode === "light" ? "#586069" : "#8b949e",
                    },
                  }}
                >
                  {option.problemName}
                  <br />
                  <span>{option.problemId}</span>
                </Box>
              </li>
            );
            // }
          }}
          options={[
            // { addNew: "true" },
            ...problems,
          ]}
          getOptionLabel={(option) => option.problemName || ""}
          renderInput={(params) => {
            return (
              <TextField
                {...params}
                autoFocus
                placeholder="Search by problem name"
              />
            );
          }}
        />
        <Tooltip arrow title="Import all problems from other contest">
          <PrimaryButton
            onClick={() => {
              setOpenModalImportProblem(true);
            }}
          >
            Import
          </PrimaryButton>
        </Tooltip>
        <PrimaryButton
          href={"/programming-contest/create-problem"}
          target="_blank"
          sx={{ minWidth: "136px" }}
          // endIcon={<ArrowRightAltRoundedIcon />}
        >
          Create Problem
        </PrimaryButton>
      </Stack>

      <StandardTable
        // title={"Problems in Contest"}
        columns={columns}
        data={contestProblems}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />

      <ModalAddProblemToContest
        contestId={contestId}
        chosenProblem={chosenProblem}
        isOpen={openModalAddProblem}
        handleSuccess={handleAddProblemToContestSuccess}
        handleClose={handleCloseModal}
      />

      <ModalUpdateProblemInfoInContest
        contestId={contestId}
        editingProblem={editingProblem}
        isOpen={openModalUpdateProblem}
        handleSuccess={handleAddProblemToContestSuccess}
        handleClose={handleCloseModal}
      />

      <ModalImportProblemsFromContest
        contestId={contestId}
        isOpen={openModalImportProblem}
        handleClose={handleCloseModalImportProblems}
      />
    </Paper>
  );
}
