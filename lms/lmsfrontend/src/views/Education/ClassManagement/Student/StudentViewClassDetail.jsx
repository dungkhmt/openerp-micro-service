import {Avatar, Card, CardContent, CardHeader, Divider, Grid, Link, Typography,} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {useEffect, useState} from "react";
import {BiDetail} from "react-icons/bi";
import {useParams,} from "react-router";
import {classState} from "state/ClassState";
import {request} from "../../../../api";
import QuizTab from "../../../../component/education/classmanagement/student/QuizTab";
import {a11yProps, AntTab, AntTabs, TabPanel,} from "../../../../component/tab";
import StudentViewClassDetailAssignmentList from "./StudentViewClassDetailAssignmentList";
import StudentViewClassDetailChapterList from "./StudentViewClassDetailChapterList";
import StudentViewClassDetailLearningSessionList from "./StudentViewClassDetailLearningSessionList";
import StudentViewClassDetailStudentList from "./StudentViewClassDetailStudentList";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  tabs: {
    backgroundColor: theme.palette.background.paper,
  },
}));

export default function StudentViewClassDetail() {
  const classes = useStyles();
  const params = useParams();

  const classId = params.id;
  const [classDetail, setClassDetail] = useState({});
  const [activeTab, setActiveTab] = useState(0);

  const handleChangeTab = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };

  const getClassDetail = () => {
    const classId = params.id;
    request("get", `/edu/class/${params.id}`, (res) => {
        setClassDetail(res.data);
      classState.classId.set(classId);
    });
  };

  useEffect(() => {
    getClassDetail();
  }, []);

  const tabsLabel = [
    "Thông tin chung",
    "Nội dung",
    "Bài tập trắc nghiệm",
    "DS sinh viên",
    "Bài tập",
    "Buổi học",
  ];

  return (
    <div>
      <AntTabs
        value={activeTab}
               onChange={handleChangeTab}
               aria-label="student-view-class-detail-tabs"
               scrollButtons="auto"
        variant="scrollable"
      >
        {tabsLabel.map((label, idx) => (
          <AntTab key={label} label={label} {...a11yProps(idx)} />
        ))}
      </AntTabs>

      <TabPanel value={activeTab} index={0}>
        <Card className={classes.card}>
          <CardHeader
            avatar={
              <Avatar style={{ background: "#ff7043" }}>
                <BiDetail size={32} />
              </Avatar>
            }
            title={<Typography variant="h5">Thông tin lớp</Typography>}
          />
          <CardContent>
            <Grid container className={classes.grid}>
              <Grid item md={3} sm={3} xs={3} direction="column">
                <Typography>Class Code</Typography>
                <Typography>Course Code</Typography>
                <Typography>Course Name</Typography>
                <Typography>Class Type</Typography>
              </Grid>
              <Grid item md={8} sm={8} xs={8}>
                <Typography>
                  <b>:</b> {classDetail.code}
                </Typography>
                <Typography>
                  <b>:</b> {classDetail.courseId}
                </Typography>
                <Typography>
                  <b>:</b> {classDetail.name}
                </Typography>
                <Typography>
                  <b>:</b> {classDetail.classType}
                </Typography>
              </Grid>

              <div className={classes.divider}>
                <Divider
                  variant="fullWidth"
                  classes={{ root: classes.rootDivider }}
                />
              </div>

              <Grid item md={3} sm={3} xs={3}>
                <Typography>Teacher</Typography>
                <Typography>Email</Typography>
              </Grid>
              <Grid item md={8} sm={8} xs={8}>
                <Typography>
                  <b>:</b> {classDetail.teacherName}
                </Typography>
                <div
                  style={{
                    display: "flex",
                    fontSize: "1rem",
                  }}
                >
                  <b>:&nbsp;</b>
                  {
                    <Link href={`mailto:${classDetail.email}`}>
                      {classDetail.email}
                    </Link>
                  }
                </div>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      </TabPanel>

      <TabPanel value={activeTab} index={1}>
        <StudentViewClassDetailChapterList classId={classId} />
      </TabPanel>

      <TabPanel value={activeTab} index={2}>
        <QuizTab classId={params.id} />
      </TabPanel>

      <TabPanel value={activeTab} index={3}>
        <StudentViewClassDetailStudentList classId={classId} />
      </TabPanel>
      <TabPanel value={activeTab} index={4}>
        <StudentViewClassDetailAssignmentList classId={params.id} />
      </TabPanel>
      <TabPanel value={activeTab} index={5}>
        <StudentViewClassDetailLearningSessionList classId={params.id} />
      </TabPanel>
    </div>
  );
}
