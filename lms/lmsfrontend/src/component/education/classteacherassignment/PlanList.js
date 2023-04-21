import {Stack} from "@mui/material";
import {request} from "api";
import PrimaryButton from "component/button/PrimaryButton";
import StandardTable from "component/table/StandardTable";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {toFormattedDateTime} from "utils/dateutils";
import CreatePlanModal from "./CreatePlanModal";

function PlanList() {
  const [plans, setPlans] = useState([]);
  const [open, setOpen] = React.useState(false);

  const columns = [
    {
      title: "Tên",
      field: "planName",
      render: (rowData) => (
        <Link
          to={{
            pathname: `/edu/teaching-assignment/plan/${rowData.id}/?tab=0`,
          }}
          style={{
            textDecoration: "none",
            whiteSpace: "pre-wrap" /* css-3 */,
            // whiteSpace: "-moz-pre-wrap" /* Mozilla, since 1999 */,
            // whiteSpace: "-pre-wrap" /* Opera 4-6 */,
            // whiteSpace: "-o-pre-wrap" /* Opera 7 */,
            wordWrap: "break-word" /* Internet Explorer 5.5+ */,
          }}
        >
          {rowData.planName}
        </Link>
      ),
    },
    { title: "Người tạo", field: "createdByUserLoginId" },
    { title: "Ngày tạo", field: "createdStamp" },
  ];

  function getClassTeacherAssignmentList() {
    request("GET", "/get-all-class-teacher-assignment-plan", (res) => {
      const data = res.data.map((plan) => {
        const created = toFormattedDateTime(plan.createdStamp);
        return { ...plan, createdStamp: created };
      });

      setPlans(data);
    });
  }

  const handleModalOpen = () => {
    setOpen(true);
  };

  const handleModalClose = () => {
    setOpen(false);
  };

  function createPlan(planName) {
    let data = { planName: planName };
    request(
      "post",
      "create-class-teacher-assignment-plan",
      (res) => {
        console.log("create-class-teacher-assignment-plan ", res);
        //alert("create-class-teacher-assignment-plan " + res.data);
      },
      { 401: () => {} },
      data
    );
  }

  const customCreateHandle = (planName) => {
    console.log(planName);
    //setSearchString(sString);
    alert("create plan " + planName);
    createPlan(planName);
    handleModalClose();
  };

  useEffect(() => {
    getClassTeacherAssignmentList();
  }, []);

  return (
    <Stack spacing={2}>
      <PrimaryButton onClick={handleModalOpen} sx={{ alignSelf: "flex-end" }}>
        Thêm mới
      </PrimaryButton>
      <StandardTable
        title={"Kế hoach phân công"}
        columns={columns}
        data={plans}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: false,
          sorting: false,
        }}
      />

      <CreatePlanModal
        open={open}
        onClose={handleModalClose}
        onCreate={customCreateHandle}
      />
    </Stack>
  );
}

export default PlanList;
