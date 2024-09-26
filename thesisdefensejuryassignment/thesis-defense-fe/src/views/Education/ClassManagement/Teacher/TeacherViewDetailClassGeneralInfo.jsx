// import {
//   Avatar,
//   Card,
//   CardContent,
//   CardHeader,
//   Grid,
//   IconButton,
//   Typography,
// } from "@material-ui/core";
// //import AppBar from "@material-ui/core/AppBar";
// //import Box from "@material-ui/core/Box";
// // import withAsynchScreenSecurity from "../../../../component/education/classmanagement/withAsynchScreenSecurity";
// //import Button from "@material-ui/core/Button";
// import { makeStyles } from "@material-ui/core/styles";
// //import Tab from "@material-ui/core/Tab";
// //import Tabs from "@material-ui/core/Tabs";
// //import MaterialTable from "material-table";
// //import PropTypes from "prop-types";
// import React, { useEffect, useState } from "react";
// //import ReactExport from "react-data-export";
// import { BiDetail } from "react-icons/bi";
// //import { useSelector } from "react-redux";
// //import { useHistory, useParams } from "react-router";
// import { request } from "../../../../api";
// import { drawerWidth } from "../../../../assets/jss/material-dashboard-react";
// //import CustomizedDialogs from "../../../../component/dialog/CustomizedDialogs";
// //import AssignList from "../../../../component/education/classmanagement/AssignList";
// //import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
// //import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
// //import { StyledBadge } from "../../../../component/education/classmanagement/StyledBadge";
// //import TeacherViewLogUserCourseChapterMaterialList from "../../../../component/education/course/TeacherViewLogUserCourseChapterMaterialList";
// //import TeacherViewLogUserQuizList from "../../../../component/education/course/TeacherViewLogUserQuizList";
// //import displayTime from "../../../../utils/DateTimeUtils";
// //import changePageSize, {
// //  localization,
// //  tableIcons,
// //} from "../../../../utils/MaterialTableUtils";
// //import { errorNoti } from "../../../../utils/notification";
// import EditIcon from "@material-ui/icons/Edit";
// import TClassUpdatePopup from "./TClassUpdatePopup";
// import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
//
// const useStyles = makeStyles((theme) => ({
//   root: {
//     // flexGrow: 1,
//     margin: "auto",
//     width: `calc(100vw - ${drawerWidth + theme.spacing(4) * 2 + 1}px)`,
//     backgroundColor: theme.palette.background.paper,
//   },
//   card: {
//     marginTop: theme.spacing(2),
//   },
//   grid: {
//     paddingLeft: 56,
//   },
//   negativeBtn: {
//     minWidth: 112,
//     marginLeft: 10,
//     marginRight: 10,
//   },
//   positiveBtn: {
//     minWidth: 112,
//   },
//   dialogRemoveBtn: {
//     fontWeight: "normal",
//   },
//   listItem: {
//     height: 48,
//     borderRadius: 6,
//     marginBottom: 6,
//     backgroundColor: "#f5f5f5",
//     "&:hover": {
//       backgroundColor: "#e0e0e0",
//     },
//   },
//   open: { transform: "rotate(-180deg)", transition: "0.3s" },
//   close: { transition: "0.3s" },
//   item: {
//     paddingLeft: 32,
//   },
//   tabs: { padding: theme.spacing(2) },
//   tabSelected: {
//     background: "rgba(254,243,199,1)",
//     color: "rgba(180,83,9,1) !important",
//   },
//   tabRoot: {
//     margin: "0px 0.5rem",
//     borderRadius: "0.375rem",
//     textTransform: "none",
//   },
// }));
//
// export default function TeacherViewDetailClassGeneralInfo(props) {
//   const classId = props.classId;
//   const classes = useStyles();
//   const [classDetail, setClassDetail] = useState({});
//   const [fetchedClassDetail, setFetchedClassDetail] = useState(false);
//   const [open, setOpen] = useState(false);
//   // Functions.
//   const getClassDetail = () => {
//     request(
//       // token, history,
//       "get",
//       `/edu/class/${classId}`,
//       (res) => {
//         setClassDetail(res.data);
//         setFetchedClassDetail(!fetchedClassDetail);
//       }
//     );
//   };
//   function onUpdateClass() {
//     setOpen(true);
//   }
//
//   useEffect(() => {
//     getClassDetail();
//   }, []);
//
//   return (
//     <div>
//       <Card className={classes.card}>
//         <CardHeader
//           avatar={
//             <Avatar style={{ background: "#ff7043" }}>
//               <BiDetail size={32} />
//             </Avatar>
//           }
//           title={<Typography variant="h5">Thông tin lớp</Typography>}
//           action={
//             <PositiveButton
//               label="Chỉnh sửa"
//               className={classes.positiveBtn}
//               onClick={() => {
//                 onUpdateClass();
//               }}
//             />
//           }
//         />
//         <CardContent>
//           <Grid container className={classes.grid}>
//             <Grid item md={3} sm={3} xs={3} container direction="column">
//               <Typography>Mã lớp</Typography>
//               <Typography>Mã học phần</Typography>
//               <Typography>Tên học phần</Typography>
//               <Typography>Loại lớp</Typography>
//             </Grid>
//             <Grid item md={8} sm={8} xs={8} container direction="column">
//               <Typography>
//                 <b>:</b> {classDetail.code}
//               </Typography>
//               <Typography>
//                 <b>:</b> {classDetail.courseId}
//               </Typography>
//               <Typography>
//                 <b>:</b> {classDetail.name}
//               </Typography>
//               <Typography>
//                 <b>:</b> {classDetail.classType}
//               </Typography>
//             </Grid>
//           </Grid>
//         </CardContent>
//       </Card>
//
//       <TClassUpdatePopup open={open} setOpen={setOpen} classId={classId} />
//     </div>
//   );
// }


import {Card, CardContent, CardHeader, Grid, Typography,} from "@material-ui/core";
//import AppBar from "@material-ui/core/AppBar";
//import Box from "@material-ui/core/Box";
// import withAsynchScreenSecurity from "../../../../component/education/classmanagement/withAsynchScreenSecurity";
//import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
//import Tab from "@material-ui/core/Tab";
//import Tabs from "@material-ui/core/Tabs";
//import MaterialTable from "material-table";
//import PropTypes from "prop-types";
import React, {useEffect, useState} from "react";
//import ReactExport from "react-data-export";
//import { useSelector } from "react-redux";
//import { useHistory, useParams } from "react-router";
import {request} from "../../../../api";
//import CustomizedDialogs from "../../../../component/dialog/CustomizedDialogs";
//import AssignList from "../../../../component/education/classmanagement/AssignList";
//import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
//import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
//import { StyledBadge } from "../../../../component/education/classmanagement/StyledBadge";
//import TeacherViewLogUserCourseChapterMaterialList from "../../../../component/education/course/TeacherViewLogUserCourseChapterMaterialList";
//import TeacherViewLogUserQuizList from "../../../../component/education/course/TeacherViewLogUserQuizList";
//import displayTime from "../../../../utils/DateTimeUtils";
//import changePageSize, {
//  localization,
//  tableIcons,
//} from "../../../../utils/MaterialTableUtils";
//import { errorNoti } from "../../../../utils/notification";
import TClassUpdatePopup from "./TClassUpdatePopup";
import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
import {useTranslation} from "react-i18next";

const useStyles = makeStyles((theme) => ({
  // root: {
  //   // flexGrow: 1,
  //   margin: "auto",
  //   width: `calc(100vw - ${drawerWidth + theme.spacing(4) * 2 + 1}px)`,
  //   backgroundColor: theme.palette.background.paper,
  // },
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  // negativeBtn: {
  //   minWidth: 112,
  //   marginLeft: 10,
  //   marginRight: 10,
  // },
  positiveBtn: {
    minWidth: 112,
  },
  // dialogRemoveBtn: {
  //   fontWeight: "normal",
  // },
  // listItem: {
  //   height: 48,
  //   borderRadius: 6,
  //   marginBottom: 6,
  //   backgroundColor: "#f5f5f5",
  //   "&:hover": {
  //     backgroundColor: "#e0e0e0",
  //   },
  // },
  // open: { transform: "rotate(-180deg)", transition: "0.3s" },
  // close: { transition: "0.3s" },
  // item: {
  //   paddingLeft: 32,
  // },
  // tabs: { padding: theme.spacing(2) },
  // tabSelected: {
  //   background: "rgba(254,243,199,1)",
  //   color: "rgba(180,83,9,1) !important",
  // },
  // tabRoot: {
  //   margin: "0px 0.5rem",
  //   borderRadius: "0.375rem",
  //   textTransform: "none",
  // },
}));

export default function TeacherViewDetailClassGeneralInfo(props) {
  const classId = props.classId;
  const classes = useStyles();
  const [classDetail, setClassDetail] = useState({});
  const [open, setOpen] = useState(false);
  const classAttrs = ["code", "courseId", "name", "classType"];
  const { t } = useTranslation("education/classmanagement/teacher/teacher-view-detail-class-general-info");

  useEffect(getClassDetail, []);

  function getClassDetail() {
    request("get", `/edu/class/${classId}`, res => setClassDetail(res.data));
  }

  function onUpdateClass() {
    setOpen(true);
  }



  return (
    <div>
      <Card className={classes.card}>
        <CardHeader
          title={<Typography variant="h5">{ t('title') }</Typography>}
          action={
            <PositiveButton
              label={ t('edit') }
              className={classes.positiveBtn}
              onClick={onUpdateClass}
            />
          }
        />

        <CardContent>
          <Grid container className={classes.grid}>
            <Grid item md={3} sm={3} xs={3} container direction="column">
              { classAttrs.map(attr =>
                <Typography key={attr}>
                  {t(`classDetail.${attr}`)}
                </Typography>
              )}
            </Grid>
            <Grid item md={8} sm={8} xs={8} container direction="column">
              { classAttrs.map(attr =>
                <Typography key={attr}>
                  <b>:</b> {classDetail[attr]}
                </Typography>
              )}
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      <TClassUpdatePopup open={open} setOpen={setOpen} classId={classId} />
    </div>
  );
}

