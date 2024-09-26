import React, {useEffect, useState} from 'react';
import {Card, CardContent, CardHeader, Grid, Typography} from "@material-ui/core";
import {errorNoti} from "../../../utils/notification";
import {request} from "../../../api";

function StudentDetail(props) {
  const studentLoginId = props.studentLoginId;
  const [studentDetail, setStudentDetail] = useState({});

  useEffect(getStudentDetail, []);

  function getStudentDetail() {
    let successHandler = res => {
      let detail = res.data;
      if (detail.gender === 'M') {
        detail.genderLabel = "Nam"
      } else if (detail.gender === 'F') {
        detail.genderLabel = "Nữ"
      }
      setStudentDetail(detail);
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", `/users/${studentLoginId}/detail`, successHandler, errorHandlers);
  }

  const studentAttrs = ["userName", "email", "fullName", "genderLabel", "birthDate", "affiliations"];
  const studentAttrLabels = {
    "userName": "User login ID",
    "email": "Email",
    "fullName": "Họ tên",
    "genderLabel": "Giới tính",
    "birthDate": "Ngày sinh",
    "affiliations": "Đơn vị"
  }

  return (
    <Card>
      <CardHeader title={<Typography variant="h5">Thông tin chung SV</Typography>}/>

      <CardContent>
        <Grid container>
          <Grid item md={3} sm={3} xs={3} container direction="column">
            { studentAttrs.map(attr =>
              <Typography key={attr}>
                {studentAttrLabels[attr]}
              </Typography>
            )}
          </Grid>
          <Grid item md={8} sm={8} xs={8} container direction="column">
            { studentAttrs.map(attr =>
              <Typography key={attr}>
                <b>:</b> {studentDetail[attr]}
              </Typography>
            )}
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}

export default StudentDetail;