import {GetApp} from "@material-ui/icons";
import AddIcon from "@material-ui/icons/Add";
import {Box, Chip, Divider, Grid, IconButton, Paper, Stack, TextField, Tooltip, Typography,} from "@mui/material";
import {BASE_URL, request} from "api";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {Link} from "react-router-dom";
import {toFormattedDateTime} from "utils/dateutils";
import {errorNoti} from "utils/notification";
import StandardTable from "component/table/StandardTable";
import {getColorLevel, getColorStatus} from "./lib";
import FilterByTag from "component/table/FilterByTag";
import PrimaryButton from "../../button/PrimaryButton";
import SearchIcon from "@mui/icons-material/Search";
import AutorenewIcon from "@mui/icons-material/Autorenew";
import TertiaryButton from "../../button/TertiaryButton";
import StyledSelect from "../../select/StyledSelect";
import {useKeycloak} from "@react-keycloak/web";
import {getLevels, getStatuses} from "./CreateProblem";

const filterInitValue = {levelIds: [], tags: [], name: "", statuses: []}
const selectProps = (options) => ({
  multiple: true,
  renderValue: (selected) => (
    <Box sx={{display: 'flex', flexWrap: 'wrap', gap: 0.5}}>
      {selected.map((value) => (
        <Chip
          size="small"
          key={value}
          label={options.find(item => item.value === value).label}
          sx={{
            marginRight: "6px",
            marginBottom: "6px",
            border: "1px solid lightgray",
            fontStyle: "italic",
          }}
        />
      ))}
    </Box>
  )
})

function ListProblemContent({type}) {
  const {keycloak} = useKeycloak();

  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [totalCount, setTotalCount] = useState(0);
  const [filter, setFilter] = useState(filterInitValue);

  const handleChangePage = (newPage) => {
    setPage(newPage);
  };

  const handleChangePageSize = (newSize) => {
    setPage(0)
    setPageSize(newSize)
  }

  const handleSelectLevels = (event) => {
    setFilter(prevFilter => ({...prevFilter, levelIds: event.target.value}));
  };

  const handleSelectTags = (tags) => {
    setFilter(prevFilter => ({...prevFilter, tags: tags}));
  }

  const handleChangeProblemName = (event) => {
    setFilter(prevFilter => ({...prevFilter, name: event.target.value}));
  }

  const handleChangeStatus = (event) => {
    setFilter(prevFilter => ({...prevFilter, statuses: event.target.value}));
  }

  const resetFilter = () => {
    setFilter(filterInitValue)
  }

  const handleSearch = () => {
    setLoading(true);
    let url;
    switch (type) {
      case 0:
        url = `/teacher/owned-problems?page=${page}&size=${pageSize}`;
        break
      case 1:
        url = `/teacher/shared-problems?page=${page}&size=${pageSize}`;
        break
      case 2:
        url = `/teacher/public-problems?page=${page}&size=${pageSize}`;
        break
    }

    if (filter.name) {
      url += `&name=${filter.name}`;
    }
    url += `&levelIds=${filter.levelIds}`;
    url += `&tagIds=${filter.tags.map(item => item.tagId)}`;
    url += `&statusIds=${filter.statuses}`;

    request("get",
      url,
      (res) => {
        setLoading(false);

        const data = res.data
        const myProblems = data.content

        if (data.numberOfElements === 0 && data.number > 0) {
          setPage(0)
        } else {
          setProblems(myProblems);
          setTotalCount(data.totalElements)
        }
      },
      {
        onError: (e) => {
          setLoading(false);
          errorNoti(t("common:error", 3000))
        }
      });
  }

  const {t} = useTranslation(["education/programmingcontest/problem", "common"]);
  const levels = getLevels(t);
  const statuses = getStatuses(t)

  const ACCESS_TOKEN = keycloak?.token;

  const onSingleDownload = async (problem) => {
    try {
      const url = `${BASE_URL}/problems/${problem.problemId}/export`;
      console.log("Download URL:", url);

      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${ACCESS_TOKEN}`,
        },
      });

      if (!response.ok) throw new Error("Failed to download file");

      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = downloadUrl;
      a.download = `${problem.problemId}.zip`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(downloadUrl);
    } catch (error) {
      console.error("Download error:", error);
    }
  };

  const COLUMNS = [
    {
      title: "ID",
      field: "problemId",
      cellStyle: {minWidth: 300},
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/manager-view-problem-detail/" +
              encodeURIComponent(rowData["problemId"]),
          }}
          style={{
            textDecoration: "none",
            color: "blue",
            cursor: "",
          }}

        >
          {rowData["problemId"]}
        </Link>
      ),
    },
    {title: t("problemName"), field: "problemName", cellStyle: {minWidth: 300}},
    {title: t("common:createdBy"), field: "userId", cellStyle: {minWidth: 120}},
    {
      title: t("level"),
      field: "levelId",
      align: 'center',
      cellStyle: {minWidth: 120},
      render: (rowData) => (
        <Typography component="span" variant="subtitle2" sx={{color: getColorLevel(`${rowData.levelId}`)}}>
          {`${levels.find(item => item.value === rowData.levelId).label}`}
        </Typography>
      ),
    },
    {
      title: t("status"),
      field: "statusId",
      align: 'center',
      cellStyle: {minWidth: 120},
      render: (rowData) => (
        <Typography component="span" variant="subtitle2" sx={{color: getColorStatus(`${rowData.statusId}`)}}>
          {`${statuses.find(item => item.value === rowData.statusId)?.label || ''}`}
        </Typography>
      )
    },
    {
      title: t("tag"),
      fields: "tags",
      render: (rowData) => (
        <Box>
          {rowData.tags?.length > 0 &&
            rowData.tags.map((tag) => (
              <Chip
                size="small"
                label={tag.name}
                key={tag.tagId}
                sx={{
                  marginRight: "6px",
                  marginBottom: "6px",
                  border: "1px solid lightgray",
                  fontStyle: "italic",
                }}
              />
            ))}
        </Box>
      ),
    },
    {
      title: t("appearances"),
      field: "appearances",
      align: 'right',
      cellStyle: {minWidth: 200},
      render: (rowData) => {
        return (
          <span style={{marginLeft: "24px"}}>{rowData.appearances}</span>
        );
      },
    },
    {
      title: t("common:createdTime"),
      field: "createdAt",
      cellStyle: {minWidth: 200},
      render: (rowData) => toFormattedDateTime(rowData.createdAt)
    },
    {
      title: t("common:action"),
      cellStyle: {minWidth: 120},
      render: (rowData) => {
        return (
          <Tooltip title={t('export')}>
            <IconButton
              variant="contained"
              color="primary"
              onClick={() => onSingleDownload(rowData)}
            >
              <GetApp/>
            </IconButton>
          </Tooltip>
        );
      },
    },
  ];

  useEffect(() => {
    handleSearch()
  }, [page, pageSize]);

  return (
    <Paper elevation={1} sx={{padding: "16px 24px", borderRadius: 4}} square={false}>
      <Typography variant="h6" sx={{marginBottom: "12px"}}>{t("search")}</Typography>
      <Grid container spacing={3}>
        <Grid item xs={3}>
          <TextField
            size='small'
            fullWidth
            label={t("problemName")}
            value={filter.name}
            onChange={handleChangeProblemName}
          />
        </Grid>
        <Grid item xs={3}>
          <StyledSelect
            fullWidth
            key={t("level")}
            label={t("level")}
            options={levels}
            value={filter.levelIds}
            sx={{minWidth: 'unset', mr: 'unset'}}
            SelectProps={selectProps(levels)}
            onChange={(event) => {
              handleSelectLevels(event);
            }}
          />
        </Grid>
        <Grid item xs={3}>
          <FilterByTag onSelect={handleSelectTags} value={filter.tags}/>
        </Grid>
        <Grid item xs={3}>
          <StyledSelect
            fullWidth
            key={t("status")}
            label={t("status")}
            options={statuses}
            value={filter.statuses}
            sx={{minWidth: 'unset', mr: 'unset'}}
            SelectProps={selectProps(statuses)}
            onChange={(event) => {
              handleChangeStatus(event);
            }}
          />
        </Grid>
      </Grid>
      <Stack direction="row" justifyContent='flex-end' spacing={2} sx={{mt: 3}}>
        <TertiaryButton
          onClick={resetFilter}
          variant="outlined"
          startIcon={<AutorenewIcon/>}
        >
          {t("reset")}
        </TertiaryButton>
        <PrimaryButton
          disabled={loading}
          onClick={handleSearch}
          startIcon={<SearchIcon/>}
        >
          {t("search")}
        </PrimaryButton>
      </Stack>

      <Divider sx={{mt: 2, mb: 2}}/>

      <Stack direction="row" justifyContent='space-between' mb={1.5}>
        <Typography variant="h6">{t("problemList")}</Typography>
        <PrimaryButton
          startIcon={<AddIcon/>}
          onClick={() => {
            window.open("/programming-contest/create-problem");
          }}>
          {t("common:create", {name: ''})}
        </PrimaryButton>
      </Stack>
      <StandardTable
        columns={COLUMNS.filter(item => {
          if (type === 0) {
            return item.field !== 'userId'
          } else {
            return true
          }
        })}
        data={problems}
        hideCommandBar
        hideToolBar
        options={{
          selection: false,
          pageSize: pageSize,
          search: false,
          sorting: false,
        }}
        components={{
          Container: (props) => <Paper {...props} elevation={0}/>,
        }}
        isLoading={loading}
        page={page}
        totalCount={totalCount}
        onChangePage={handleChangePage}
        onChangeRowsPerPage={handleChangePageSize}
      />
    </Paper>
  );
}

export default ListProblemContent
