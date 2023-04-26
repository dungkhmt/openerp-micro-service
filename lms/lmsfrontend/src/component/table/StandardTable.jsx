import {makeStyles, MuiThemeProvider, styled} from "@material-ui/core/styles";
import {Box, IconButton, Paper, Typography} from "@mui/material";
import MaterialTable, {MTableCell, MTableToolbar} from "material-table";
import PropTypes from "prop-types";
import React, {useCallback} from "react";
import {components, localization, tableIcons, themeTable,} from "utils/MaterialTableUtils";
import {useTranslation} from "react-i18next";
import FirstPageIcon from "@mui/icons-material/FirstPage";
import KeyboardArrowLeft from "@mui/icons-material/KeyboardArrowLeft";
import KeyboardArrowRight from "@mui/icons-material/KeyboardArrowRight";
import LastPageIcon from "@mui/icons-material/LastPage";

export const Offset = styled("div")(({theme}) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
  justifyContent: "flex-end",
}));

export function TablePaginationActions(props) {
  const {count, page, rowsPerPage, onPageChange} = props;

  const handleFirstPageButtonClick = (event) => {
    onPageChange(event, 0);
  };

  const handleBackButtonClick = (event) => {
    onPageChange(event, page - 1);
  };

  const handleNextButtonClick = (event) => {
    onPageChange(event, page + 1);
  };

  const handleLastPageButtonClick = (event) => {
    onPageChange(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
  };

  return (
    <Box sx={{flexShrink: 0, ml: 2.5}}>
      <IconButton
        onClick={handleFirstPageButtonClick}
        disabled={page === 0}
        aria-label="first page"
      >
        <FirstPageIcon/>
      </IconButton>
      <IconButton
        onClick={handleBackButtonClick}
        disabled={page === 0}
        aria-label="previous page"
      >
        <KeyboardArrowLeft/>
      </IconButton>
      <IconButton
        onClick={handleNextButtonClick}
        disabled={page >= Math.ceil(count / rowsPerPage) - 1}
        aria-label="next page"
      >
        <KeyboardArrowRight/>
      </IconButton>
      <IconButton
        onClick={handleLastPageButtonClick}
        disabled={page >= Math.ceil(count / rowsPerPage) - 1}
        aria-label="last page"
      >
        <LastPageIcon/>
      </IconButton>
    </Box>
  );
}


const useStyles = makeStyles(() => ({
  tableToolbarHighlight: {backgroundColor: "transparent"},
}));

export default function StandardTable(props) {
  const classes = useStyles();
  const {t} = useTranslation(["common"]);

  const rowStyle = useCallback(
    (rowData) => ({
      backgroundColor: rowData.tableData.checked ? "#e0e0e0" : "#ffffff",
    }),
    []
  );

  return (
    <>
      {!props.hideCommandBar && (
        <>
          <Box
            sx={{
              width: "100%",
              height: 40,
              display: "flex",
              justifyContent: "flex-start",
              alignItems: "center",
              borderBottom: "1px solid rgb(224, 224, 224)",
              pl: 2,
              backgroundColor: "#f5f5f5",
              ...props.sx?.commandBar,
            }}
            // className={props.classNames?.commandBar}
          >
            {props.commandBarComponents}
          </Box>
          {/* <Offset /> */}
        </>
      )}
      <MuiThemeProvider theme={themeTable}>
        <MaterialTable
          {...props}
          title={props.title ? <Typography variant="h5">{props.title}</Typography> : <></>}
          localization={{
            ...localization,
            toolbar: {
              searchPlaceholder: t("search"),
            },
            ...props.localization,
          }}
          icons={tableIcons}
          options={{
            selection: true,
            pageSize: 20,
            headerStyle: {
              backgroundColor: "#f4f4f4",
              color: "#404040",
              fontWeight: 600,
            },
            rowStyle: rowStyle,
            ...props.options,
          }}
          onSelectionChange={(rows) => {
            props.onSelectionChange(rows);
          }}
          onRowClick={props.onRowClick}
          components={{
            ...components,
            Container: (props) => (
              <Paper
                {...props}
                elevation={3}
              />
            ),
            Toolbar: (props) => (
              <MTableToolbar
                {...props}
                classes={{
                  highlight: classes.tableToolbarHighlight,
                }}
                searchFieldStyle={{
                  height: 40,
                }}
              />
            ),
            Cell: (props) => (
              <MTableCell
                {...props}
                style={{padding: "16px 16px"}}
              />
            ),
            ...props.components,
          }}
          actions={props.actions}
          editable={props.editable}
        />
      </MuiThemeProvider>
    </>
  );
}

StandardTable.propTypes = {
  hideCommandBar: PropTypes.bool,
  classNames: PropTypes.object,
  localization: PropTypes.object,
  options: PropTypes.object,
  onSelectionChange: PropTypes.func,
  onRowClick: PropTypes.func,
  components: PropTypes.object,
  title: PropTypes.string,
  columns: PropTypes.array.isRequired,
  actions: PropTypes.array,
  data: PropTypes.array,
  commandBarComponents: PropTypes.element,
  editable: PropTypes.object
};

