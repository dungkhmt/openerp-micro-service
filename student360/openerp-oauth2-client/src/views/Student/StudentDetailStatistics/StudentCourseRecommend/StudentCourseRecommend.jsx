import React, { useEffect, useState } from "react";
import {
  Rating,
  Link,
  Stack,
  Typography,
  Chip,
  useTheme,
  Divider,
} from "@mui/material";
import { errorNoti } from "../../../../utils/notification";
import { request } from "../../../../api";
import SwitchText from "../../../../components/switch/SwitchText";
import SkeletonCourse from "../../../../components/skeleton/Skeleton";
import RatingIndexMenu from "../../../../components/menu/RatingIndexMenu";
import EmptyState from "../../../../components/emtystate/EmptyState";
import HoursIndexMenu from "../../../../components/menu/HoursIndexMenu";

function StudentCourseRecommend(props) {
  const theme = useTheme();
  const studentLoginId = props.studentLoginId;
  const [studentDetail, setStudentDetail] = useState({});
  const [changeRating, setChangeRating] = useState();
  const [changeHour, setChangeHour] = useState();

  const navigation = ["free", "Paid"];
  const [active, setActive] = useState(navigation[0]);

  const listHour = [0, 1, 3, 6, 17];

  useEffect(getStudentDetail, [
    active,
    studentLoginId,
    changeRating,
    changeHour,
  ]);

  function getStudentDetail() {
    let successHandler = (res) => {
      let detail = res.data;
      setStudentDetail(detail);
    };
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000),
    };
    request(
      "GET",
      `/course-recommender/${studentLoginId}?price=${active}&rating=${changeRating}&duration=${changeHour}`,
      successHandler,
      errorHandlers
    );
  }

  const handleChangeRating = (value) => {
    setChangeRating(value);
  };

  const handleChangeHour = (value) => {
    setChangeHour(listHour[value]);
  };

  if (Array.isArray(studentDetail)) {
    return (
      <Stack gap={1.25}>
        <Stack justifyContent="space-between" marginBottom={1} direction="row">
          <Stack direction="row" gap={2}>
            <RatingIndexMenu handleChangeRating={handleChangeRating} />
            <HoursIndexMenu handleChangeHours={handleChangeHour} />
          </Stack>
          <SwitchText
            active={active}
            setActive={setActive}
            option={navigation}
          />
        </Stack>

        <Typography marginBottom={2} variant="h4">
          Course Recommendations
        </Typography>

        <Stack direction="column" gap={1}>
          {studentDetail.length > 0 ? (
            studentDetail?.map((item) => (
              <>
                <Stack
                  key={`item-${item.id}`}
                  direction="row"
                  borderRadius={1}
                  marginTop={1}
                  gap={1.25}
                  sx={{ border: `1px solid ${theme.palette.grey[200]}` }}
                >
                  <Stack gap={1} width="60%">
                    <Typography variant="contentMRegular">
                      <Link href={item.url} target="_blank">
                        {item.title}
                      </Link>
                    </Typography>
                    <Typography variant="contentSRegular" width="80%">
                      {item.subtitle.replace(/<\/?strong>/g, "")}
                    </Typography>
                    <Stack direction="row" alignItems="end">
                      <Typography variant="contentLRegular">
                        {item.rating}
                      </Typography>
                      <Rating
                        size="small"
                        value={item.rating}
                        precision={0.1}
                        readOnly
                      />
                      <Typography variant="contentSRegular" marginLeft={1}>
                        ({item.reviews} reviews)
                      </Typography>
                    </Stack>
                    <Typography variant="contentSRegular">
                      {item.hours} total hours
                    </Typography>
                    <Typography variant="contentSRegular">
                      {item.lectures} lectures
                    </Typography>
                    <Chip
                      sx={{
                        fontWeight: 500,
                        fontSize: "14px",
                        width: "fit-content",
                        padding: "0rem 0.25rem",
                      }}
                      label={item.level}
                    />
                  </Stack>
                  {item.currentPrice != 0 && item.originalPrice != 0 ? (
                    <Stack>
                      <Typography variant="contentMBold">
                        đ {item.currentPrice}
                      </Typography>
                      <Typography
                        style={{ textDecoration: "line-through" }}
                        variant="contentSRegular"
                      >
                        đ {item.originalPrice}
                      </Typography>
                    </Stack>
                  ) : (
                    <Stack>
                      <Typography variant="contentMBold">Free</Typography>
                    </Stack>
                  )}
                </Stack>
                <Divider
                  orientation="horizontal"
                  flexItem
                  width="75%"
                  sx={{ color: "grey.400" }}
                />
              </>
            ))
          ) : (
            <EmptyState search="courses" />
          )}
        </Stack>
      </Stack>
    );
  } else return <SkeletonCourse />;
}
export default StudentCourseRecommend;
