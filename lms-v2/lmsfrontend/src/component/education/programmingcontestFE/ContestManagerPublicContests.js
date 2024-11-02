import { LinearProgress } from "@mui/material";
import { request } from "api"; // Ensure this is correctly set up to make API requests
import StandardTable from "component/table/StandardTable";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toFormattedDateTime } from "utils/dateutils";

export default function ContestManagerPublicContests() {
  const [publicContests, setPublicContests] = useState([]);
  const [loading, setLoading] = useState(true);

  const columns = [
    {
      title: "Contest",
      field: "contestName",
      render: (rowData) => (
        <Link
          to={{
            pathname: "/programming-contest/public-view-contest-detail/" + rowData.contestId,
          }}
        >
          {rowData.contestName}
        </Link>
      ),
    },
    { title: "Status", field: "status" },
    { title: "Created By", field: "createdBy" },
    { title: "Created At", field: "createdAt" },
  ];

  const getPublicContestList = async () => {
    console.log("alo")
    console.log("API Response:", res);
    try {
      const res = await request("get", "/contests/public");
 
      const data = res.data.map((e, index) => ({ 
        index: index + 1,
        contestId: e.contestId,
        contestName: e.contestName,
        status: e.statusId,
        createdBy: e.userId, // Ensure this matches the API response
        createdAt: toFormattedDateTime(e.startAt), // Ensure this matches the API response
      }));
      setPublicContests(data);
    } catch (error) {
      console.error("Error fetching public contests:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getPublicContestList();
  }, []);

  return (
    <>
      {loading && <LinearProgress />}
      <StandardTable
        title={"Public Contests"}
        columns={columns}
        data={publicContests}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
      />
    </>
  );
}