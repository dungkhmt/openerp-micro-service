import React, {useEffect, useMemo, useState} from 'react';
import {request} from "../../../api";
import {errorNoti, infoNoti, successNoti} from "../../../utils/notification";
import StandardTable from "../../../component/table/StandardTable";
import {Card, CardContent} from "@material-ui/core";
import {defaultDatetimeFormat} from "../../../utils/dateutils";
import {LoadingButton} from "@mui/lab";
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

export default function LearningStatisticResults(props) {
  const [latestStatisticTime, setLatestStatisticTime] = useState();
  const [statisticResults, setStatisticResults] = useState({ content: [], totalElements: 0 });
  const [filterParams, setFilterParams] = useState({ loginId: '', page: 0, size: 20 });
  const [doingStatistics, setDoingStatistics] = useState(false);

  const tableTitle = useMemo(() => {
    return latestStatisticTime ?
      `Thống kê học tập (Cập nhật lần cuối: ${defaultDatetimeFormat(latestStatisticTime)})` :
      "Thống kê học tập"
  }, [latestStatisticTime])

  useEffect(getLearningStatisticResults, [filterParams]);

  function getLearningStatisticResults() {
    let successHandler = res => {
      const { latestStatisticTime, statisticResults } = res.data;
      setLatestStatisticTime(latestStatisticTime)
      setStatisticResults(statisticResults);
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", "/statistic/learning/basic", successHandler, errorHandlers,  null, { params: filterParams })
  }

  function statisticLearningGeneral() {
    setDoingStatistics(true);
    infoNoti("Đang thực hiện thống kê. Có thể mất một khoảng thời gian!", true);

    let successHandler = res => {
      setDoingStatistics(false);
      successNoti("Thống kê thành công, xem kết quả trên giao diện!", true);
      getLearningStatisticResults();
    }
    let errorHandlers = {
      onError: (error) => {
        errorNoti("Đã xảy ra lỗi trong khi thống kê!", true);
        console.log("Lỗi thống kê", error);
      }
    }

    request("POST", "/statistic/learning/basic", successHandler, errorHandlers);
  }

  function getSubmissions(){
    alert("getSubmissions");
    request("get", "/admin/data/get-accepted-submission", (res) => {
      const info = res.data;
      console.log('getSubmissions, info = ',info);
      
    },
    {
      onError: (e) => {
        console.error("Error", e);
        errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", 3000);
      },
    });
  }

  const StatisticAction = (
    <LoadingButton loading={doingStatistics}
                   loadingPosition="start"
                   startIcon={<TrendingUpIcon/>}
                   color="primary" variant="contained"
                   onClick={statisticLearningGeneral}>
      Statistic
    </LoadingButton>
  )
  const GetSubmissionsAction = (
    <LoadingButton loading={doingStatistics}
                   loadingPosition="start"
                   startIcon={<TrendingUpIcon/>}
                   color="primary" variant="contained"
                   onClick={getSubmissions}>
      Get Accepted Submissions
    </LoadingButton>
  )

  const columns = [
    { title: "Login ID", field: "loginId" },
    { title: "Total quiz doing times", field: "totalQuizDoingTimes" },
    { title: "Total code submissions", field: "totalCodeSubmissions" },
    { title: "Last doing quiz at", field: "latestTimeDoingQuiz",
      render: (statistic) => defaultDatetimeFormat(statistic.latestTimeDoingQuiz)
    },
    { title: "Last submit code at", field: "latestTimeSubmittingCode",
      render: (statistic) => defaultDatetimeFormat(statistic.latestTimeSubmittingCode)
    },
    { title: "Submissions accepted on the first submit", field: "submissionsAcceptedOnTheFirstTime" },
    { title: "Total quiz doing periods", field: "totalQuizDoingPeriods" },
    { title: "Total error submissions", field: "totalErrorSubmissions" }
  ]

  const actions = [{ icon: () => StatisticAction, isFreeAction: true },
    { icon: () => GetSubmissionsAction, isFreeAction: true }];

  return (
    <Card>
      <CardContent>
        <StandardTable
          title={tableTitle}
          columns={columns}
          actions={actions}
          data={statisticResults.content}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
            pageSize: filterParams.size,
            searchText: filterParams.loginId,
            debounceInterval: 500
          }}
          page={filterParams.page}
          totalCount={statisticResults.totalElements}
          onChangePage={ (page, size) => setFilterParams({...filterParams, page, size}) }
          onSearchChange={ loginId => setFilterParams({page: 0, size: filterParams.size, loginId}) }
        />
      </CardContent>
    </Card>
  );
}