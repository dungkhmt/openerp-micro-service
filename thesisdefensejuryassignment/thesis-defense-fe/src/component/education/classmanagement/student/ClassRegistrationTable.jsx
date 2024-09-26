import React, {useEffect, useState} from 'react';
import {request} from "../../../../api";
import {errorNoti, successNoti} from "../../../../utils/notification";
import StandardTable from "../../../table/StandardTable";
import PositiveButton from "../PositiveButton";
import {useTranslation} from "react-i18next";

let translator;

function ClassRegistrationTable(props) {
  const [semesterId, setSemesterId] = useState(0);
  const [openingClasses, setOpeningClasses] = useState([]);
  const [registeredClassesOfCurrentUser, setRegisteredClassesOfCurrentUser] = useState(new Set());
  const [filterParams, setFilterParams] = useState({ page: 0, pageSize: 20, searchText: '' });

  const { t } = useTranslation("education/classmanagement/student/classregistration")
  translator = t;

  useEffect(() => getClassRegistrationData(filterParams, setClassRegistrationData), [filterParams]);

  function setClassRegistrationData(response) {
    let responseData = response.data;
    setSemesterId(responseData.semesterId);
    setOpeningClasses(responseData.page.content);
    setRegisteredClassesOfCurrentUser(new Set(responseData.registeredClasses));
  }

  const columns = [
    { title: t('columns.classCode'), field: "classCode" },
    { title: t('columns.courseId'), field: "courseId" },
    { title: t('columns.name'), field: "courseName" },
    { title: t('columns.classType'), field: "classType" },
    { title: t('columns.departmentId'), field: "departmentId" },
    { title: "", field: "",
      render: aClass => (registeredClassesOfCurrentUser.has(aClass.id) ? null :
        <PositiveButton
          label={ t('columns.join') }
          disableRipple
          onClick={() => registerClass(aClass.id, setRegisteredClassesOfCurrentUser)} />
      )
    }
  ];

  return (
    <StandardTable
      title={t('title') + semesterId}
      columns={columns}
      data={openingClasses}
      hideCommandBar
      options={{
        selection: false,
        search: true,
        sorting: true,
        pageSize: filterParams.pageSize,
        searchText: filterParams.searchText,
        debounceInterval: 500
      }}
      onPageChange={ page => setFilterParams({...filterParams, page})}
      onRowsPerPageChange={ pageSize => setFilterParams({...filterParams, pageSize}) }
      onSearchChange={ searchText => setFilterParams({page: 0, pageSize: filterParams.pageSize, searchText}) }
    />
  );
}

function getClassRegistrationData({ page, pageSize, searchText }, setClassRegistrationData) {
  let url = `/edu/class?page=${page}&size=${pageSize}`;
  let errorHandlers = {
    onError: (e) => {
      console.log(e);
      errorNoti(translator('registerNoti.error'), 3000);
    }
  }
  request("post", url, setClassRegistrationData, errorHandlers, { courseName: searchText });
}

function registerClass(classId, setRegisteredClasses) {
  let successHandler = () => {
    successNoti(translator('registerNoti.success'), 3000);
    setRegisteredClasses(oldRegisteredClasses => new Set([...oldRegisteredClasses, classId]))
  }
  let errorHandlers = {
    400: (e) => {
      console.log(e);
      errorNoti(e.response.body, 3000)
    }
  }
  request("post", "/edu/class/register", successHandler, errorHandlers, { classId });
}

export default ClassRegistrationTable;