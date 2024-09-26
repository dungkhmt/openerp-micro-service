import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import Tooltip, {tooltipClasses} from "@mui/material/Tooltip";
// import { makeStyles } from "@material-ui/core/styles";
import {styled, TablePagination} from "@mui/material";
import Typography from "@mui/material/Typography";
import {request} from "api";
import map from "lodash/map";
import range from "lodash/range";
import {forwardRef, memo, useEffect, useState} from "react";
import SimpleBar from "simplebar-react";

// const useStyles = makeStyles((theme) => ({
//   root: {
//     flexGrow: 1,
//   },
//   paper: {
//     padding: theme.spacing(2),
//     textAlign: "center",
//     color: theme.palette.text.secondary,
//   },
// }));

// const defaultProps = {
//   bgcolor: "background.paper",
//   style: { width: "5rem", height: "2rem" },
//   borderColor: "text.primary",
// };

const listSession = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
const WU = 2; // change this for width of cell
const SU = 8;

const TimeTableHeaderDay = memo(({ day, ...rest }) => {
  let width = listSession.length;

  return (
    <div>
      <div>
        <Box
          border={1}
          borderTop={0}
          borderRight={0}
          width={width * WU + "rem"}
          height={"2rem"}
          display="flex"
          justifyContent={"center"}
          alignItems={"center"}
          {...rest}
        >
          Thứ {day}
        </Box>
      </div>
      <div>
        <Box display="flex" justifyContent="center">
          {listSession.map((session) => {
            return (
              <BoxClass
                sz={1}
                code={session}
                borderRight={0}
                borderTop={0}
                borderBottom={2}
              />
            );
          })}
        </Box>
      </div>
    </div>
  );
});

const TimeTableHeaderFirstCell = memo(() => {
  return (
    <div>
      <BoxClass
        code={"Giảng viên"}
        sz={SU}
        height={"4rem"}
        borderTop={0}
        borderLeft={0}
        borderBottom={2}
      />
    </div>
  );
});

const TimeTableHeader = memo(() => {
  return (
    <Box display="flex" border={"none"}>
      <TimeTableHeaderFirstCell />
      <TimeTableHeaderDay day={2} />
      <TimeTableHeaderDay day={3} />
      <TimeTableHeaderDay day={4} />
      <TimeTableHeaderDay day={5} />
      <TimeTableHeaderDay day={6} />
      <TimeTableHeaderDay day={7} />
    </Box>
  );
});

const BoxClass = forwardRef(function BoxClass(props, ref) {
  const { code, planId, sz, bgColor, ...rest } = props;

  // function handleClick() {
  //   alert("Class " + code);
  //   //props.root.suggestTeacherListForClass(code);

  //   let data = {
  //     classId: code,
  //     planId: planId,
  //   };

  //   request(
  //     "POST",
  //     "get-suggested-teacher-and-actions-for-class",
  //     (res) => {
  //       alert("suggested teacher list: " + JSON.stringify(res.data));
  //     },
  //     { 401: () => {} },
  //     data
  //   );
  // }

  return (
    <Box
      border={1}
      width={sz * WU + "rem"}
      height={"2rem"}
      display="flex"
      justifyContent={"center"}
      alignItems="center"
      bgcolor={bgColor}
      color={"text.primary"}
      {...rest}
      ref={ref}
      // onClick={() => {
      //   handleClick();
      // }}
    >
      {code}
    </Box>
  );
});

const TimeTableSpace = memo(({ sz }) => {
  return (
    <Box display="flex" justifyContent="center">
      {map(range(sz), (_) => {
        return (
          <Box
            border={1}
            borderRight={0}
            borderTop={0}
            height={"2rem"}
            width={WU + "rem"}
            // borderColor={grey[500]}
          />
        );
      })}
    </Box>
  );
});

const HtmlTooltip = styled(({ className, ...props }) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(({ theme }) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    maxWidth: "none",
    paddingTop: 6,
    backgroundColor: theme.palette.common.white,
    color: "rgba(0, 0, 0, 0.87)",
    borderRadius: 6,
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
    // fontSize: theme.typography.pxToRem(12),
    // border: "1px solid #dadde9",
    "& .MuiTooltip-arrow": {
      color: theme.palette.common.white,
    },
  },
}));

// This is component in MUI v4
// const HtmlTooltip = withStyles((theme) => ({
//   tooltip: {
//     paddingTop: 6,
//     backgroundColor: theme.palette.common.white,
//     color: "rgba(0, 0, 0, 0.87)",
//     borderRadius: 6,
//     boxShadow:
//       "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
//     // fontSize: theme.typography.pxToRem(12),
//     // border: "1px solid #dadde9",
//     "& .MuiTooltip-arrow": {
//       color: theme.palette.common.white,
//     },
//   },
// }))(Tooltip);

const TimeTableRow = memo((props) => {
  const { planId, root, list, remainEmptySlots, teacherId } = props;

  const drawClass = (
    startIndexFromPrevious,
    classCode,
    duration,
    bgColor,
    classes
  ) => (
    <Box display="flex" justifyContent="center">
      <TimeTableSpace sz={startIndexFromPrevious} />
      <HtmlTooltip
        arrow
        placement="bottom"
        title={
          <>
            {classes.map((c) => (
              <>
                <Typography style={{ fontWeight: "bold" }}>
                  {c.classCode}
                  {c?.courseId && " - " + c.courseId}
                  {c?.classType && " - " + c.classType}
                </Typography>
                <Typography variant="body2">{c.courseName}</Typography>
                <Typography variant="body2" style={{ paddingBottom: 8 }}>
                  {c.timetable}
                </Typography>
              </>
            ))}
          </>
        }
        PopperProps={{
          modifiers: [
            {
              name: "flip",
              enabled: false,
            },
            {
              name: "offset",
              enabled: true,
              options: {
                offset: [0, -16],
              },
            },
            // {
            //   name: 'preventOverflow',
            //   enabled: true,
            //   options: {
            //     altAxis: true,
            //     altBoundary: true,
            //     tether: true,
            //     rootBoundary: 'document',
            //     padding: 8,
            //   },
            // },
          ],
        }}
      >
        <BoxClass
          code={classCode}
          planId={planId}
          sz={duration}
          bgColor={bgColor}
          root={root}
          borderRight={0}
          borderTop={0}
          // borderColor={grey[500]}
          // data-tip
          // data-for={classCode}
        />
        {/* <ReactTooltip id={classCode} place="bottom" type="light" effect="solid">
        {classes.map((c) => (
          <>
            <Typography style={{ fontWeight: "bold" }}>
              {c.classCode}
              {c?.courseId && " - " + c.courseId}
              {c?.classType && " - " + c.classType}
            </Typography>
            <Typography variant="body2">{c.courseName}</Typography>
            <Typography variant="body2">{c.timetable}</Typography>
            <br />
          </>
        ))}
      </ReactTooltip> */}
      </HtmlTooltip>
    </Box>
  );

  const drawConflictClasses = (classes, fromIndex, toIndex) => {
    const firstClass = classes[fromIndex];
    let classCode = firstClass.classCode + ", ";
    let duration = firstClass.duration;

    for (let i = fromIndex + 1; i <= toIndex; i++) {
      const c = classes[i];
      classCode += c.classCode + (i === toIndex ? "" : ", ");
      duration += c.startIndexFromPrevious + c.duration;
    }

    return drawClass(
      firstClass.startIndexFromPrevious,
      classCode,
      duration,
      "error.main",
      classes.slice(fromIndex, toIndex + 1)
    );
  };

  const drawClasses = () => {
    const classes = list;
    const n = classes.length;
    const result = [];

    for (let i = 0; i < n; i++) {
      for (let j = i + 1; j < n; j++) {
        const boxClassJ = classes[j];

        if (boxClassJ.startIndexFromPrevious > -1) {
          if (j === i + 1) {
            result.push(
              drawClass(
                classes[i].startIndexFromPrevious,
                classes[i].classCode,
                classes[i].duration,
                "info.main",
                classes.slice(i, i + 1)
              )
            );
          } else {
            // Paint class i to j-1
            result.push(drawConflictClasses(classes, i, j - 1));
          }
          i = j - 1;
          break;
        } else {
          if (j === n - 1) {
            // The last block
            result.push(drawConflictClasses(classes, i, j));
            i = j + 1; // To break for i loop because all element are drew
          }
        }
      }

      if (i === n - 1) {
        // Paint class j
        result.push(
          drawClass(
            classes[i].startIndexFromPrevious,
            classes[i].classCode,
            classes[i].duration,
            "info.main",
            classes.slice(i, i + 1)
          )
        );
      }
    }

    result.push(
      <>
        {remainEmptySlots > 1 && <TimeTableSpace sz={remainEmptySlots - 1} />}
        {remainEmptySlots > 0 && (
          <Box
            border={1}
            borderRight={2}
            borderTop={0}
            height={"2rem"}
            width={WU + "rem"}
            // borderColor={grey[500]}
          />
        )}
      </>
    );

    return result;
  };

  return (
    <Box display="flex" justifyContent="left">
      <BoxClass
        code={teacherId}
        sz={SU}
        borderLeft={0}
        borderTop={0}
        justifyContent="flex-start"
        pl={2}
      />
      {drawClasses()}
    </Box>
  );
});

const TimeTableBody = memo(({ data, planId, root }) => {
  return (
    <div>
      {data.map((e) => {
        return (
          <Box display="flex" justifyContent="left">
            <TimeTableRow
              list={e.classList}
              planId={planId}
              teacherId={e.teacherId}
              remainEmptySlots={e.remainEmptySlots}
              root={root}
            />
          </Box>
        );
      })}
    </div>
  );
});

function TeacherBasedTimeTableAssignmentInSolution(props) {
  const planId = props.planId;
  // const classes = useStyles();
  const [dataTimeTable, setDataTimeTable] = useState([]);
  // const [selectedTeacherId, setSelectedTeacherId] = useState(null);
  // const [selectedClassId, setSelectedClassId] = useState(null);

  // const [openSuggestion, setOpenSuggestion] = React.useState(false);

  // Table
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  // Funcs
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const getDataTimeTableList = () => {
    request(
      "GET",
      //"/get-classes-assigned-to-a-teacher-solution/" + planId,
      `edu/teaching-assignment/plan/${planId}/solution/grid-view`,
      (res) => {
        setDataTimeTable(res.data);
      }
    );
  };

  // const suggestTeacherListForClass = (classId) => {
  //   alert("suggest teacher list for class " + classId);
  // };
  // const customSelectTeacherHandle = (selectedTeacherId) => {};

  // const handleSuggestionModalClose = () => {
  //   setOpenSuggestion(false);
  // };

  // function handleBtnClick() {
  //   alert("Phân công lại");
  // }

  useEffect(() => {
    getDataTimeTableList();
  }, []);

  return (
    dataTimeTable.length > 0 && (
      <Card
        style={{
          maxWidth: "100%",
          padding: 16,
          marginTop: 48,
        }}
      >
        <Box display="flex" pt={1} pb={2}>
          <Typography component="h6" style={{ fontSize: "1.25rem" }}>
            Biểu đồ lịch giảng dạy theo tuần
          </Typography>
          {/* <PrimaryButton
          // className={classes.btn}
          onClick={(e) => {
            handleBtnClick(e);
          }}
        >
          Phân công lại
        </PrimaryButton> */}
        </Box>
        <SimpleBar
          style={{
            maxWidth: "100%",
            paddingBottom: 16,
          }}
        >
          <Box border={2} borderBottom={1} width={160 * 16 + 2 + "px"}>
            <TimeTableHeader />
            <TimeTableBody
              data={dataTimeTable.slice(
                page * rowsPerPage,
                (page + 1) * rowsPerPage - 1
              )}
              root={this}
              planId={planId}
            />
          </Box>
        </SimpleBar>
        <TablePagination
          component="div"
          count={dataTimeTable.length}
          showFirstButton
          showLastButton
          page={page}
          rowsPerPageOptions={[10, 15]}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Card>
    )
  );
}

export default TeacherBasedTimeTableAssignmentInSolution;
