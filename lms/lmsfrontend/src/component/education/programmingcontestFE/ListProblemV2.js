import React, { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BASE_URL, request } from "../../../api";
import { useTranslation } from "react-i18next";
import { toFormattedDateTime } from "../../../utils/dateutils";
import { Box, Chip, IconButton, TablePagination } from "@mui/material";
import { GetApp } from "@material-ui/icons";
import { getColorLevel } from "./lib";
import { StandardTable } from "erp-hust/lib/StandardTable";
import AddIcon from "@material-ui/icons/Add";
import { TablePaginationActions } from "component/table/StandardTable";

function ListProblemV2() {
  const [myProblems, setMyProblems] = useState([]);
  const [pageMyProblems, setPageMyProblems] = useState(0);
  const [sizeMyProblems, setSizeMyProblems] = useState(10);
  const [totalMyProblems, setTotalMyProblems] = useState(0);

  const [sharedProblems, setSharedProblems] = useState([]);
  const [pageSharedProblems, setPageSharedProblems] = useState(0);
  const [sizeSharedProblems, setSizeSharedProblems] = useState(10);
  const [totalSharedProblems, setTotalSharedProblems] = useState(0);

  const { t } = useTranslation("education/programmingcontest/listproblem");

  const onSingleDownload = (problem) => {
    const form = document.createElement("form");

    form.setAttribute("method", "post");
    form.setAttribute("target", "_blank");
    form.setAttribute(
      "action",
      `${BASE_URL}/export-problem/${problem.problemId}`
    );

    document.body.appendChild(form);
    form.submit();
    form.parentNode.removeChild(form);
  };

  const COLUMNS = [
    {
      title: "ID",
      field: "problemId",
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
    { title: "Name", field: "problemName" },
    { title: "Created By", field: "userId" },
    { title: "Created At", field: "createdAt" },
    {
      title: "Level",
      field: "levelId",
      render: (rowData) => (
        <span
          style={{ color: getColorLevel(`${rowData.levelId}`) }}
        >{`${rowData.levelId}`}</span>
      ),
    },
    { title: "Status", field: "statusId" },
    {
      title: "Tags",
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 &&
            rowData.tags.map((tag) => (
              <Chip
                size="small"
                label={tag.name}
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
      title: "Contests Used",
      field: "appearances",
      render: (rowData) => {
        return (
          <span style={{ marginLeft: "24px" }}>{rowData.appearances}</span>
        );
      },
    },
    {
      title: "Export",
      render: (rowData) => {
        return (
          <IconButton
            variant="contained"
            color="primary"
            onClick={() => onSingleDownload(rowData)}
          >
            <GetApp />
          </IconButton>
        );
      },
    },
  ];
  const PAGE_SIZES = [10, 20, 50];

  const getProblems = useCallback((path, setData) => {
    request("get", path, (res) => {
      const data = res.data.content.map((problem) => ({
        problemId: problem.problemId,
        problemName: problem.problemName,
        userId: problem.userId,
        createdAt: toFormattedDateTime(problem.createdAt),
        levelId: problem.levelId,
        tags: problem.tags,
        appearances: problem.appearances,
        statusId: problem.statusId,
      }));
      const totalElements = res.data.totalElements;
      setData(data, totalElements);
    }).then();
  }, []);

  useEffect(() => {
    getProblems(
      `/get-all-my-problems?page=${pageMyProblems}&size=${sizeMyProblems}`,
      (data, totalElements) => {
        setMyProblems(data);
        setTotalMyProblems(totalElements);
      }
    );
  }, [getProblems, pageMyProblems, sizeMyProblems]);

  useEffect(() => {
    getProblems(
      `/get-all-shared-problems?page=${pageSharedProblems}&size=${sizeSharedProblems}`,
      (data, totalElements) => {
        setSharedProblems(data);
        setTotalSharedProblems(totalElements);
      }
    );
  }, [getProblems, pageSharedProblems, sizeSharedProblems]);

  return (
    <div>
      <StandardTable
        title="My Problems"
        columns={COLUMNS}
        data={myProblems}
        hideCommandBar
        key="my-problems"
        options={{
          selection: false,
          pageSize: sizeMyProblems,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return <AddIcon fontSize="large" />;
            },
            tooltip: t("addNewProblem"),
            isFreeAction: true,
            onClick: () => {
              window.open("/programming-contest/create-problem");
            },
          },
        ]}
        components={{
          Pagination: (props) => (
            <TablePagination
              {...props}
              rowsPerPageOptions={PAGE_SIZES}
              rowsPerPage={sizeMyProblems}
              count={totalMyProblems}
              page={pageMyProblems}
              onPageChange={(e, value) => setPageMyProblems(value)}
              onRowsPerPageChange={(e) => {
                setSizeMyProblems(e.target.value);
                setPageMyProblems(0);
              }}
              ActionsComponent={TablePaginationActions}
            />
          ),
        }}
      />
      <br />

      <StandardTable
        title="Shared Problems"
        columns={COLUMNS}
        data={sharedProblems}
        hideCommandBar
        key="sharedProblems"
        options={{
          selection: false,
          pageSize: sizeSharedProblems,
          search: true,
          sorting: true,
        }}
        components={{
          Pagination: (props) => (
            <TablePagination
              {...props}
              rowsPerPageOptions={PAGE_SIZES}
              rowsPerPage={sizeSharedProblems}
              count={totalSharedProblems}
              page={pageSharedProblems}
              onPageChange={(e, value) => setPageSharedProblems(value)}
              onRowsPerPageChange={(e) => {
                setSizeSharedProblems(e.target.value);
                setPageSharedProblems(0);
              }}
              ActionsComponent={TablePaginationActions}
            />
          ),
        }}
      />
    </div>
  );
}

export default ListProblemV2;
