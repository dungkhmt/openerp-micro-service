import { Card, CardContent } from "@material-ui/core/";
import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import { errorNoti, successNoti } from "../../../../utils/notification";
import { request } from "../../../../api";
import { Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import StandardTable from "../../../table/StandardTable";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  tableWrapper: {
    "& table thead tr": {
      "& th:nth-child(3)": {
        maxWidth: "140px !important",
      },
      "& th:nth-child(4)": {
        maxWidth: "140px !important",
        padding: "5px",
      },
    },
  },
}));

export default function TeacherViewClassMaterialList(props) {
  const classes = useStyles();
  const classId = props.classId;
  const history = useHistory();
  const [materialOfClass, setMaterialOfClass] = useState([]);

  useEffect(getMaterialOfCourse, []);

  async function getMaterialOfCourse() {
    const successHandler = (res) => setMaterialOfClass(res.data);
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", true),
    };
    request(
      "GET",
      `/edu/class/get-material/${classId}`,
      successHandler,
      errorHandlers
    );
  }

  function toggleMaterialStatus(material) {
    const successHandler = (res) => {
      successNoti(
        "Thay đổi trạng thái tài liệu, xem kết quả trên giao diện!",
        true
      );
      material.status = material.status === true ? false : true;
      setMaterialOfClass([...materialOfClass]);
    };
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi thay đổi trạng thái", true),
    };
    const data = {
      classId: material.classId,
      chapterId: material.chapterId,
      materialId: material.materialId,
      status: !material.status,
    };
    request(
      "POST",
      `/edu/class/update-material-status`,
      successHandler,
      errorHandlers,
      data
    );
  }

  const CreateChapterButton = (
    <Button
      color="primary"
      variant="contained"
      onClick={() => history.push(`chapter/create/${courseId}`)}
    >
      <AddIcon /> Thêm mới
    </Button>
  );

  const ToggleMaterialStatusButton = ({ material }) => (
    <Button
      color="primary"
      variant="outlined"
      onClick={() => toggleMaterialStatus(material)}
    >
      Update status
    </Button>
  );

  const columns = [
    {
      title: "Material ID",
      field: "eduCourseChapterMaterial.eduCourseMaterialName",
      render: (material) => (
        <Link
          to={`/edu/teacher/course/chapter/material/detail/${material.materialId}`}
        >
          {material.eduCourseChapterMaterial.eduCourseMaterialName}
        </Link>
      ),
    },
    {
      title: "Tên chapter",
      field: "eduCourseChapterMaterial.eduCourseChapter.chapterName",
    },
    {
      title: "Trạng thái",
      field: "status",
      lookup: MATERIAL_STATUSES,
      cellStyle: { maxWidth: "140px" },
    },
    {
      field: "",
      title: "",
      cellStyle: { maxWidth: "140px", padding: "5px", textAlign: "center" },
      render: (chapter) => <ToggleMaterialStatusButton material={chapter} />,
    },
  ];

  const actions = [{ icon: () => CreateChapterButton, isFreeAction: true }];

  return (
    <Card>
      <CardContent className={classes.tableWrapper}>
        <StandardTable
          title="Danh sách tài liệu"
          columns={columns}
          data={materialOfClass}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
          }}
          actions={actions}
        />
      </CardContent>
    </Card>
  );
}

TeacherViewClassMaterialList.propTypes = {
  classId: PropTypes.string.isRequired,
};

const MATERIAL_STATUSES = {
  true: "Công khai",
  false: "Không công khai",
};
