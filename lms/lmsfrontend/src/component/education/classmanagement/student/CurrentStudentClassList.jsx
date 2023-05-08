import React, {useEffect, useState} from 'react';
import {makeStyles} from "@material-ui/core/styles";
import {Box, Chip} from "@material-ui/core";
import {FcApproval} from "react-icons/fc";
import {GiSandsOfTime} from "react-icons/gi";
import {request} from "../../../../api";
import {useHistory} from "react-router-dom";
import {infoNoti} from "../../../../utils/notification";
import StandardTable from "../../../table/StandardTable";
import {useTranslation} from "react-i18next";

function CurrentStudentClassList(props) {
  const { t } = useTranslation("education/classmanagement/student/currentstudentclasslist");
  const columns = [
    { title: t("columns.classCode"), field: "classCode" },
    { title: t("columns.courseId"), field: "courseId" },
    { title: t("columns.name"), field: "name" },
    { title: t("columns.classType"), field: "classType" },
    { title: t("columns.semester"), field: "semester" },
    {
      title: t("columns.status"),
      field: "status",
      render: aClass => <RegisterStatusBox status={aClass.status}/>
    }
  ];

  const history = useHistory();
  const [classesOfCurrentStudent, setClassesOfCurrentStudent] = useState([]);

  useEffect(getClassesOfCurrentStudent, []);

  function getClassesOfCurrentStudent() {
    request("get", "/edu/class/list/student", (response) => {
      setClassesOfCurrentStudent(response.data);
    })
  }

  function viewClassDetailsIfApproved(aClass) {
    if (aClass.status === "APPROVED") {
      history.push(`/edu/student/class/${aClass.id}`);
    } else {
      infoNoti(t("notiWhenClickingNonApprovedClass") + aClass.name, 3000);
    }
  }

  return (
    <StandardTable
      title={ t("title") }
      columns={columns}
      data={classesOfCurrentStudent}
      onRowClick={ (event, aClass) => viewClassDetailsIfApproved(aClass) }
      hideCommandBar
      options={{
        selection: false,
        search: true,
        sorting: true,
      }}
    />
  );
}


const useRegisterStatusBoxStyles = makeStyles((theme) => ({
  approved: {
    color: "green",
    borderColor: "green",
    fontSize: "1rem",
    width: 160,
  },
  pendingApproval: {
    fontSize: "1rem",
  },
}))

/** Private component, do not expose it */
function RegisterStatusBox({ status }) {
  const classes = useRegisterStatusBoxStyles();
  const { t } = useTranslation("education/classmanagement/student/currentstudentclasslist");

  return (
    <Box display="flex" justifyContent="center">
      { status === "APPROVED" &&
        <Chip
          icon={<FcApproval size={24} />}
          label={ t("classStatus.approved") }
          variant="outlined"
          className={classes.approved}
        />
      }

      { status === "WAITING_FOR_APPROVAL" &&
        <Chip
        icon={<GiSandsOfTime size={24} />}
        label={ t("classStatus.pendingApproval") }
        color="primary"
        variant="outlined"
        className={classes.pendingApproval}
        />
      }
    </Box>
  );
}

export default CurrentStudentClassList;