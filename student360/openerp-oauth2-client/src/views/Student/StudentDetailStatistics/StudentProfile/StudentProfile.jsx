import React, { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  Typography,
  Stack,
  Divider,
} from "@mui/material";
import { errorNoti } from "../../../../utils/notification";
import { request } from "../../../../api";

function StudentProfile(props) {
  const studentLoginId = props.studentLoginId;
  const [studentDetail, setStudentDetail] = useState({});

  useEffect(getStudentDetail, []);

  function getStudentDetail() {
    let successHandler = (res) => {
      let detail = res.data;
      if (detail.gender === "M") {
        detail.genderLabel = "Nam";
      } else if (detail.gender === "F") {
        detail.genderLabel = "Nữ";
      }

      setStudentDetail(detail);
    };
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000),
    };
    request("GET", `/user/${studentLoginId}`, successHandler, errorHandlers);
  }

  const studentInforAttrs = [
    "id",
    "fullName",
    "gender",
    "email",
    "class",
    "affiliation",
  ];

  const studentInforAttrLabels = {
    id: "Mã số sinh viên",
    fullName: "Họ và tên",
    gender: "Giới tính",
    class: "Lớp",
    affiliation: "Khóa",
    email: "Email",
  };

  return (
    <Stack gap={2}>
      {/* Thông tin sinh viên  */}
      <Card>
        <CardHeader
          title={
            <Typography variant="contentLRegular">Thông tin cơ bản</Typography>
          }
        />

        <CardContent>
          <Grid container>
            <Grid item md={3} sm={4} xs={3} container direction="column">
              {studentInforAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMRegular" py={1} key={attr}>
                    {studentInforAttrLabels[attr]}
                  </Typography>
                </>
              ))}
            </Grid>
            <Grid item md={8} sm={8} xs={10} container direction="column">
              {studentInforAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMBold" py={1} key={attr}>
                    <b>:</b> {studentDetail[attr]}
                  </Typography>
                </>
              ))}
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </Stack>
  );
}

export default StudentProfile;
