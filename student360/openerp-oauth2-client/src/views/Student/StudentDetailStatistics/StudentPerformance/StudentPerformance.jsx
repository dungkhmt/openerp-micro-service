import React, { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  Typography,
  Stack,
  Divider,
  useTheme,
  Skeleton,
} from "@mui/material";
import { errorNoti } from "../../../../utils/notification";
import { request } from "../../../../api";
function StudentPerformance(props) {
  const theme = useTheme();
  const studentLoginId = props.studentLoginId;
  const [studentDetail, setStudentDetail] = useState({});

  useEffect(getStudentDetail, []);

  function getStudentDetail() {
    let successHandler = (res) => {
      let detail = res.data;
      console.log("huuson",detail)
      if (detail.firstSubmissionAccuracyRate > 80) {
        detail.carefulness = "Cao";
      } else if (detail.firstSubmissionAccuracyRate > 50) {
        detail.carefulness = "Trung bình";
      } else {
        detail.carefulness = "Thấp";
      }

      if (detail.numberProgramLanguage > 5) {
        detail.languageVariety = "Đa dạng";
      } else if (detail.numberProgramLanguage > 3) {
        detail.languageVariety = "Nhiều";
      } else if (detail.numberProgramLanguage > 1) {
        detail.languageVariety = "Cơ bản";
      } else {
        detail.languageVariety = "Hạn chế";
      }

      if (detail.averageSubmissionPerDay > "1") {
        detail.attitude = "Tích cực";
      } else if (detail.averageSubmissionPerDay > "0.5") {
        detail.attitude = "Trung bình";
      } else {
        detail.attitude = "Cẩn cải thiện";
      }

      // if (
      //   detail.averageMinimumSubmissionToAccept <= 3 &&
      //   detail.averageMinimumSubmissionToAccept !== 0
      // )
      //   detail.evaluation = "Tốt";
      // else if (
      //   detail.averageMinimumSubmissionToAccept <= 5 &&
      //   detail.averageMinimumSubmissionToAccept !== 0
      // )
      //   detail.evaluation = "Khá";
      // else if (
      //   detail.averageMinimumSubmissionToAccept <= 10 &&
      //   detail.averageMinimumSubmissionToAccept !== 0
      // )
      //   detail.evaluation = "Trung bình";
      // else detail.evaluation = "Kém";

      if (detail.submittedMultipleTimes && detail.hasProgress)
        detail.learningStatus = "Tích cực, mong muốn hoàn thiện bài tập";

      if (detail.submittedMultipleTimes && !detail.hasProgress)
        detail.learningStatus = "Thiếu tự tin, chưa nắm vững kiến thức";

      if (!detail.submittedMultipleTimes && detail.hasHighScore)
        detail.learningStatus =
          "Tự tin, chủ động, khả năng hoàn thành bài tập tốt";

      if (!detail.submittedMultipleTimes && !detail.hasHighScore)
        detail.learningStatus = "Thiếu quan tâm, không đầu tư bài tập";

      // detail.numberProgramLanguage =
      //   detail.numberProgramLanguage +
      //   `  (${detail.programmingLanguageSubmitCounts
      //     .map((obj) => obj[0])
      //     .join(", ")})`;
      // detail.studentSemesterResult = detail?.studentSemesterResult.map(
      //   (semesterResult) => {
      //     switch (semesterResult.appearedInPlagiarism) {
      //       case 0:
      //         semesterResult.appearedInPlagiarism = "Không";
      //         break;
      //       case 1:
      //         semesterResult.appearedInPlagiarism = "Gian lận bài thi giữa kỳ";
      //         break;
      //       case 2:
      //         semesterResult.appearedInPlagiarism = "Gian lận bài thi cuối kỳ";
      //         break;
      //       default:
      //       // You can add a default action here
      //     }
      //     return semesterResult;
      //   }
      // );

      detail.mostLanguageUsed = detail.programmingLanguageSubmitCounts[0][0];

      setStudentDetail(detail);
    };
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000),
    };
    request(
      "GET",
      `/student-statistics/student-performance/${studentLoginId}`,
      successHandler,
      errorHandlers
    );
  }

  console.log("huuson1",studentDetail);

  const studentLearningInforAttrs = [
    "totalSubmitted",
    "totalProblemSubmitted",
    "numberProgramLanguage",
    "mostLanguageUsed",
    "passState",
  ];

  const studentLearningInforAttrLabels = {
    totalSubmitted: "Tổng số submissions",
    totalProblemSubmitted: "Tổng số bài đã nộp",
    numberProgramLanguage: "Số ngôn ngữ lập trình sử dụng",
    mostLanguageUsed: "Ngôn ngữ lập trình sử dụng nhiều nhất",
    passState: "Trạng thái",
  };

  const studentLearningRoutineAttrs = [
    "startTimeActive",
    "endTimeActive",
    "mostSubmittedTime",
    "mostEffectiveSubmittedTime",
    "learningBehavior",
  ];

  const studentLearningRoutineAttrLabels = {
    startTimeActive: "Thời gian bắt đầu học",
    endTimeActive: "Thời gian kết thúc học",
    mostSubmittedTime: "Thời gian active nhiều nhất",
    mostEffectiveSubmittedTime: "Thời gian hiệu quả nhất",
    learningBehavior: "Xu hướng học tập trong học kỳ",
  };

  const studentCharacterAttrs = [
    "carefulness",
    "languageVariety",
    "attitude",
    "evaluation",
    "learningStatus",
  ];

  const studentCharacterAttrLabels = {
    carefulness: "Tính cẩn thận",
    languageVariety: "Đa dạng ngôn ngữ",
    attitude: "Thái độ học tập",
    evaluation: "Khả năng tư duy thuật toán",
    learningStatus: "Tình trạng học tập",
  };

  const studentSemesterResultAttrs = [
    "totalSubmission",
    "totalProblem",
    "midtermPoint",
    "finalPoint",
    "appearedInPlagiarism",
    "passingState",
    "passingRate",
  ];
  const studentSemesterResultAttrLabels = {
    totalSubmission: "Tổng số submissions",
    totalProblem: "Tổng số bài đã nộp",
    midtermPoint: "Kết quả thi giữa kỳ",
    finalPoint: "Kết quả thi cuối kỳ",
    appearedInPlagiarism: "Phát hiện gian lận",
    passingState: "Trạng thái",
    passingRate: "Tỷ lệ qua môn",
  };
  
if (studentDetail !== null) {
  return (
    <Stack gap={2}>
      {/* Đặc tính sinh viên */}
      <Card>
        <CardHeader
          title={
            <Typography variant="contentLRegular">Thông tin học tập</Typography>
          }
        />

        <CardContent>
          <Grid container>
            <Grid item md={4} sm={3} xs={3} container direction="column">
              {studentLearningInforAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMRegular" py={1} key={attr}>
                    {studentLearningInforAttrLabels[attr]}
                  </Typography>
                </>
              ))}
            </Grid>
            <Grid item md={8} sm={8} xs={8} container direction="column">
              {studentLearningInforAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMBold" py={1} key={attr}>
                    <b>:</b>{" "}
                    {attr === "passState" ? (
                      studentDetail.passState !== 1 ? (
                        <span style={{ color: theme.palette.error.main }}>
                          Chưa đạt
                        </span>
                      ) : (
                        <span style={{ color: theme.palette.success.main }}>
                          Đạt
                        </span>
                      )
                    ) : (
                      studentDetail[attr]
                    )}
                  </Typography>
                </>
              ))}
            </Grid>
          </Grid>
        </CardContent>
      </Card>
      {/* <Grid container spacing={2}>
        {studentDetail.studentSemesterResult?.map((semesterResult, index) => (
          <Grid key={index} item xs={6}>
            <Card>
              <CardHeader
                title={
                  <Typography variant="contentLRegular">
                    Kết quả học tập {semesterResult.semester}
                  </Typography>
                }
              />

              <CardContent>
                <Grid container>
                  <Grid item md={5} sm={3} xs={4} container direction="column">
                    {studentSemesterResultAttrs.map((attr) => (
                      <>
                        <Divider
                          orientation="horizontal"
                          flexItem
                          sx={{ color: "grey.300" }}
                        />
                        <Typography variant="contentMRegular" py={1} key={attr}>
                          {studentSemesterResultAttrLabels[attr]}
                        </Typography>
                      </>
                    ))}
                  </Grid>
                  <Grid item md={7} sm={8} xs={7} container direction="column">
                    {studentSemesterResultAttrs.map((attr) => (
                      <>
                        <Divider
                          orientation="horizontal"
                          flexItem
                          sx={{ color: "grey.300" }}
                        />
                        <Typography variant="contentMBold" py={1} key={attr}>
                          <b>: </b>
                          {attr === "passingState" ? (
                            semesterResult.passingState === -1 ? (
                              <span style={{ color: theme.palette.error.main }}>
                                Chưa đạt
                              </span>
                            ) : (
                              <span
                                style={{ color: theme.palette.success.main }}
                              >
                                Đạt
                              </span>
                            )
                          ) : (
                            semesterResult[attr]
                          )}
                        </Typography>
                      </>
                    ))}
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid> */}
      {/* Thói quen học tập  */}
      <Card>
        <CardHeader
          title={
            <Typography variant="contentLRegular">Thói quen học tập</Typography>
          }
        />

        <CardContent>
          <Grid container>
            <Grid item md={4} sm={3} xs={3} container direction="column">
              {studentLearningRoutineAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMRegular" py={1} key={attr}>
                    {studentLearningRoutineAttrLabels[attr]}
                  </Typography>
                </>
              ))}
            </Grid>
            <Grid item md={8} sm={8} xs={8} container direction="column">
              {studentLearningRoutineAttrs.map((attr) => (
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
      {/* Tính cách sinh viên */}
      <Card>
        <CardHeader
          title={<Typography variant="contentLRegular">Tính cách</Typography>}
        />

        <CardContent>
          <Grid container>
            <Grid item md={4} sm={3} xs={3} container direction="column">
              {studentCharacterAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMRegular" py={1} key={attr}>
                    {studentCharacterAttrLabels[attr]}
                  </Typography>
                </>
              ))}
            </Grid>
            <Grid item md={8} sm={8} xs={8} container direction="column">
              {studentCharacterAttrs.map((attr) => (
                <>
                  <Divider
                    orientation="horizontal"
                    flexItem
                    sx={{ color: "grey.300" }}
                  />
                  <Typography variant="contentMBold" py={1} key={attr}>
                    <b>:</b>
                    {studentDetail[attr]}
                  </Typography>
                </>
              ))}
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </Stack>
  )}
  else {
    <>Huuson</>
  }
}

export default StudentPerformance;
