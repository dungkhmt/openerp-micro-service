import { request } from "api";
import { useEffect, useState } from "react";
import { dashboardUrl } from "../apiURL";
import Chart from "../components/Chart";
import styles from "./index.style";
import { Paper, Typography } from "@mui/material";

const Dashboard = () => {
  const [applicatorData, setApplicatorData] = useState(null);
  const [applicationData, setApplicationData] = useState(null);
  const [taData, setTaData] = useState(null);
  const [classData, setClassData] = useState(null);
  const [courseData, setCourseData] = useState(null);
  useEffect(() => {
    request("get", dashboardUrl.getApplicatorData, (res) => {
      console.log(res.data);
      setApplicatorData(res.data);
    });
    request("get", dashboardUrl.getApplicationData, (res) => {
      console.log(res.data);
      setApplicationData(res.data);
    });
    request("get", dashboardUrl.getTaData, (res) => {
      console.log(res.data);
      setTaData(res.data);
    });
    request("get", dashboardUrl.getClassNumbData, (res) => {
      console.log(res.data);
      setClassData(res.data);
    });
    request("get", dashboardUrl.getCourseData, (res) => {
      console.log(res.data);
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
      </div>
      <div style={styles.row}>
        <div style={styles.chart}>
          <Paper style={styles.paperElement}>
            <Chart data={classData} title="Số lớp mở" />
          </Paper>
        </div>
        <div style={styles.chart}>
          <Paper style={styles.paperElement}>
            <Chart data={applicatorData} title="Số lượng trợ giảng" />
          </Paper>
        </div>
      </div>
      <div style={styles.row}>
        <div style={styles.chart}>
          <Paper style={styles.paperElement}>
            <Chart data={applicationData} title="Số đơn xin trợ giảng" />
          </Paper>
        </div>
        <div style={styles.chart}>
          <Paper style={styles.paperElement}>
            <Chart data={taData} title="Số trợ giảng của học kỳ" />
          </Paper>
        </div>
      </div>
      <div style={styles.lastChart}>
        <Chart data={courseData} title="Số đơn xin mỗi môn học kỳ này" />
      </div>
    </Paper>
  );
};

export default Dashboard;
