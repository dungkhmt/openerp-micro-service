import { request } from "api";
import { useEffect, useState } from "react";
import { dashboardUrl, semesterUrl } from "../apiURL";
import Chart from "../components/Chart";
import styles from "./index.style";
import { Paper, Typography } from "@mui/material";
import { SEMESTER } from "../config/localize";
import ClassIcon from "@mui/icons-material/Class";
import PersonIcon from "@mui/icons-material/Person";
import HistoryEduIcon from "@mui/icons-material/HistoryEdu";
import AssignmentIndIcon from "@mui/icons-material/AssignmentInd";

const findValue = (data, semester) => {
  const item = data?.find((d) => d.label === semester);
  return item ? item.value : null;
};

const Dashboard = () => {
  const [applicatorData, setApplicatorData] = useState(null);
  const [applicationData, setApplicationData] = useState(null);
  const [taData, setTaData] = useState(null);
  const [classData, setClassData] = useState(null);
  const [courseData, setCourseData] = useState(null);
  const [semester, setSemester] = useState(SEMESTER);
  useEffect(() => {
    request("get", semesterUrl.getCurrentSemester, (res) => {
      setSemester(res.data);
    });
    request("get", dashboardUrl.getApplicatorData, (res) => {
      setApplicatorData(res.data);
    });
    request("get", dashboardUrl.getApplicationData, (res) => {
      setApplicationData(res.data);
    });
    request("get", dashboardUrl.getTaData, (res) => {
      setTaData(res.data);
    });
    request("get", dashboardUrl.getClassNumbData, (res) => {
      console.log(res.data);
      setClassData(res.data);
    });
    request("get", dashboardUrl.getCourseData, (res) => {
      setCourseData(res.data);
    });
  }, []);

  return (
    <Paper>
      <div>
        <Typography
          variant="h4"
          style={{
            fontWeight: "bold",
            paddingTop: "1em",
            marginLeft: "1em",
          }}
        >
          Thống kê
        </Typography>
        <Typography
          variant="h5"
          style={{
            fontWeight: "bold",
            paddingTop: "1em",
            marginLeft: "1.5em",
          }}
        >
          Học kỳ hiện tại: {semester}
        </Typography>
      </div>
      <div style={styles.firstRow}>
        <Paper elevation={3} style={{ width: "24%" }}>
          <Typography
            variant="h6"
            style={{ marginLeft: "1em", marginTop: "0.5em" }}
          >
            Số lớp
          </Typography>
          <div style={styles.inlineRow}>
            <ClassIcon
              color="primary"
              style={{ fontSize: 40, marginRight: "1em", marginTop: "0.1em" }}
            />
            <Typography variant="h4" style={{ fontWeight: "bold" }}>
              {findValue(classData, semester)}
            </Typography>
          </div>
        </Paper>
        <Paper elevation={3} style={{ width: "24%" }}>
          <Typography
            variant="h6"
            style={{ marginLeft: "1em", marginTop: "0.5em" }}
          >
            Số sinh viên đăng ký
          </Typography>
          <div style={styles.inlineRow}>
            <PersonIcon
              color="primary"
              style={{ fontSize: 40, marginRight: "1em", marginTop: "0.1em" }}
            />
            <Typography variant="h4" style={{ fontWeight: "bold" }}>
              {findValue(applicatorData, semester)}
            </Typography>
          </div>
        </Paper>
        <Paper elevation={3} style={{ width: "24%" }}>
          <Typography
            variant="h6"
            style={{ marginLeft: "1em", marginTop: "0.5em" }}
          >
            Số đơn xin
          </Typography>
          <div style={styles.inlineRow}>
            <HistoryEduIcon
              color="primary"
              style={{ fontSize: 40, marginRight: "1em", marginTop: "0.1em" }}
            />
            <Typography variant="h4" style={{ fontWeight: "bold" }}>
              {findValue(applicationData, semester)}
            </Typography>
          </div>
        </Paper>
        <Paper elevation={3} style={{ width: "24%" }}>
          <Typography
            variant="h6"
            style={{ marginLeft: "1em", marginTop: "0.5em" }}
          >
            Số trợ giảng
          </Typography>
          <div style={styles.inlineRow}>
            <AssignmentIndIcon
              color="primary"
              style={{ fontSize: 40, marginRight: "1em", marginTop: "0.1em" }}
            />
            <Typography variant="h4" style={{ fontWeight: "bold" }}>
              {findValue(taData, semester)}
            </Typography>
          </div>
        </Paper>
      </div>
      <div style={styles.row}>
        <div style={styles.chart}>
          <Paper elevation={3} style={styles.paperElement}>
            <Chart data={classData} title="Số lớp mở" />
          </Paper>
        </div>
        <div style={styles.chart}>
          <Paper elevation={3} style={styles.paperElement}>
            <Chart
              data={applicatorData}
              title="Số lượng sinh viên đăng ký trợ giảng"
            />
          </Paper>
        </div>
      </div>
      <div style={styles.row}>
        <div style={styles.chart}>
          <Paper elevation={3} style={styles.paperElement}>
            <Chart data={applicationData} title="Số đơn xin trợ giảng" />
          </Paper>
        </div>
        <div style={styles.chart}>
          <Paper elevation={3} style={styles.paperElement}>
            <Chart data={taData} title="Số trợ giảng của học kỳ" />
          </Paper>
        </div>
      </div>
      <div style={styles.lastChart}>
        <Paper elevation={3} style={styles.paperElement}>
          <Chart data={courseData} title="Số đơn xin mỗi môn học kỳ này" />
        </Paper>
      </div>
    </Paper>
  );
};

export default Dashboard;
