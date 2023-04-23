import {Link} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {Fragment, useEffect, useState} from "react";
import {useParams,} from "react-router";
import {Link as RouterLink} from "react-router-dom";
import {classState} from "state/ClassState";
import {request} from "../../../../api";
import StudentViewClassDetail from "./StudentViewClassDetail";

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

function SClassDetail() {
  const classes = useStyles();
  const params = useParams();

  const [classDetail, setClassDetail] = useState({});

  const [chapterList, setChapterList] = useState([]);

  const [activeTab, setActiveTab] = useState(0);

  const handleChange = (event, tabIndex) => {
    setActiveTab(tabIndex);
  };

  const chapterColumns = [
    {
      title: "Chapter",
      field: "chapterId",
      render: (rowData) => (
        <Link
          component={RouterLink}
          to={`/edu/student/course/chapter/detail/${rowData["chapterId"]}`}
        >
          {rowData["chapterName"]}
        </Link>
      ),
    },
  ];

  // Functions.
  const getClassDetail = () => {
    const classId = params.id;
    request("get", `/edu/class/${params.id}`, (res) => {
        setClassDetail(res.data);
      classState.classId.set(classId);
    });
  };

  const getChapterListOfClass = () => {
    //request( "get", `/get-quiz-of-class/${params.id}`, (res) => {
    request("get", `/edu/class/get-chapters-of-class/${params.id}`, (res) => {
        console.log("getChapterListOfClass, res.data = ", res.data);
        setChapterList(res.data);
    });
  };

  useEffect(() => {
    getClassDetail();
    getChapterListOfClass();
    // console.log("classDetail = ", classDetail);
  }, []);

  return (
    <Fragment>
      <StudentViewClassDetail />
      <br />

      {/*
      <div className={classes.tabs}>
        <StyledTabs
          value={activeTab}
          onChange={handleChange}
          aria-label="ant tabs"
          centered
        >
          <StyledTab label="General Information" {...a11yProps(0)} />
          <StyledTab label="Content" {...a11yProps(1)} />
          <StyledTab label="Quiz" {...a11yProps(2)} />
          <StyledTab label="Students" {...a11yProps(3)} />
          <StyledTab label="Exercises" {...a11yProps(4)} />
          <StyledTab label="Sessions" {...a11yProps(5)} />
        </StyledTabs>
        <Typography className={classes.padding} />
      </div>

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
        <Card>
          <CardContent>
            <MaterialTable
              title={"Chapter"}
              columns={chapterColumns}
              data={chapterList}
              components={{
                Container: (props) => <Paper {...props} elevation={0} />,
              }}
            />
          </CardContent>
        </Card>
      </TabPanel>

      <TabPanel value={activeTab} index={2}>
        <QuizTab classId={params.id} />
      </TabPanel>

      <TabPanel value={activeTab} index={3}>
        <StudentListTab classId={params.id} />
      </TabPanel>

      <TabPanel value={activeTab} index={4}>
        <AssignmentTab classId={params.id} />
      </TabPanel>
      <TabPanel value={activeTab} index={5}>
        <StudentViewLearningSessionList classId={params.id} />
      </TabPanel>

            */}
    </Fragment>
  );
}

export default SClassDetail;
