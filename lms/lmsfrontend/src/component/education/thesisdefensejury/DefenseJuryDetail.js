import {Button} from "@material-ui/core";
import {Box, IconButton} from "@material-ui/core/";
import {MuiThemeProvider} from "@material-ui/core/styles";
import Grid from "@mui/material/Grid";
import React, {useEffect, useState} from "react";
import {useHistory, useLocation, useParams} from "react-router-dom";
import {request} from "../../../api";
import ElementAddTeacher from "./ElementAddTeacher";
import ElementAddThesis from "./ElementAddThesis";
import ElementDeleteTeacher from "./ElementDeleteTeacher";
import ElementDeleteThesis from "./ElementDeleteThesis";


function DefenseJuryDetail(props) {

  const history = useHistory();
  const params = useParams();
  const [toggle, setToggle] = useState(false);
  const [toggleTeacher, setToggleTeacher] = useState(false);
  const [thesis, setThesis] = useState([]);
  const [notBelongThesis, setNotBelongThesis] = useState([]);
  const [teacher, setTeacher] = useState([]);
  const [listTeacher, setListTeacher] = React.useState([]);
  const location = useLocation();
  const [loading, setLoading] = useState(true);

  // Table
  const handlerIsLoad = () => {
    console.log("Loadding: ", loading);
    setLoading(true);
  };
  const handlerNotLoad = () => {
    console.log("Loadding: ", loading);
    setLoading(false);
  };

  async function getListThesisOfDefenseJury() {
    request(
      // token,
      // history,
      "GET",
      `/defense_jury/${params.id}/thesiss`,
      (res) => {
        // console.log(res.data)
        getAllThesisNotBelongToDefenseJury(res.data);
        let listThesis = [];
        for (let i = 0; i < res.data.length; i++) {
          var ele = {
            stt: i + 1,
            id: res.data[i].id,
            thesisName: res.data[i].thesisName,
            studentName: res.data[i].studentName,
          };
          listThesis.push(ele);
        }
        setThesis(listThesis);
      }
    );
  }

  async function getAllThesisNotBelongToDefenseJury(thesis) {
    console.log(location.state.defensePlanId);
    request(
      // token,
      // history,
      "GET",
      `${location.state.defensePlanId}/thesisBelongPlan`,
      (res) => {
        console.log("Resp:", res.data);
        // console.log("Thesis Resp:",thesis)
        let listThesis = [];
        let listThesisId = [];
        var data = [];
        for (let i = 0; i < thesis.length; i++) {
          listThesisId.push(thesis[i].id);
        }
        listThesis = res.data.result.filter(
          (ele) => !listThesisId.includes(ele.id)
        );
        // console.log("List Thesis :",listThesis)
        for (let j = 0; j < listThesis.length; j++) {
          var ele = {
            stt: j + 1,
            id: listThesis[j].id,
            thesisName: listThesis[j].thesisName,
            studentName: listThesis[j].studentName,
          };
          data.push(ele);
        }

        // console.log(data)
        setNotBelongThesis(data);
      }
    );
  }

  async function getAllTeacherNotBelongToDefenseJury(teacher) {
    request(
      // token,
      // history,
      "GET",
      "/teachers",
      (res) => {
        console.log("Resp:", res.data);
        console.log("Teacher Resp:", teacher);
        let listTeachers = [];
        let listTeachersId = [];
        var data = [];
        for (let i = 0; i < teacher.length; i++) {
          listTeachersId.push(teacher[i].id);
        }
        listTeachers = res.data.filter(
          (ele) => !listTeachersId.includes(ele.id)
        );
        console.log("List Teachers :", listTeachers);
        for (let j = 0; j < listTeachers.length; j++) {
          var ele = {
            stt: j + 1,
            teacherId: listTeachers[j].id,
            teacherName: listTeachers[j].teacherName,
            // studentName:listTeachers[j].student_name
          };
          data.push(ele);
        }

        console.log(data);
        setListTeacher(data);
      }
    );
  }

  async function getListTeacherOfDefenseJury() {
    request(
      // token,
      // history,
      "GET",
      `/defense_jury/${params.id}/teachers`,
      (res) => {
        console.log("List Teachers", res.data);
        let teachersBelongJury = [];
        if (res.data.result != null) {
          teachersBelongJury = res.data.result;
        }
        getAllTeacherNotBelongToDefenseJury(teachersBelongJury);
        let listTeachers = [];
        for (let i = 0; i < teachersBelongJury.length; i++) {
          var ele = {
            stt: i + 1,
            teacherId: res.data.result[i].id,
            teacherName: res.data.result[i].teacherName,
          };
          listTeachers.push(ele);
        }
        setTeacher(listTeachers);
      }
    );
  }

  const handleToggle = () => {
    // console.log("First:",toggle)
    setToggle(!toggle);
    // console.log("Last:",toggle)
  };
  const handleToggleTeacher = () => {
    // console.log("First:",toggle)
    setToggleTeacher(!toggleTeacher);
    // console.log("Last:",toggle)
  };

  const handleBack = (e) => {
    e.preventDefault();
    history.push({
      pathname: `/thesis/thesis_defense_plan/${location.state.defensePlanId}`,
      state: {
        valueTab: 1,
      },
    });
  };

  useEffect(() => {
    console.log("Loading");
    getListThesisOfDefenseJury();
    getListTeacherOfDefenseJury();
    // getAllTeacherNotBelongToDefenseJury();
    // getAllThesisNotBelongToDefenseJury();
  }, [toggle, toggleTeacher]);
  return (
    <>
      <Box
        width="100%"
        height={40}
        display="flex"
        justifyContent="flex-start"
        alignItems="center"
        borderBottom={1}
        mt={-3}
        mb={3}
        style={{borderColor: "#e8e8e8"}}
      />
      <MuiThemeProvider>
        <Button color="primary" type="submit" onClick={handleBack} width="20%">
          Back
        </Button>
        <div>
          <Grid container spacing={2}>
            <Grid item sm={12} md={6}>
              <h2>Danh sách luận văn trong HĐ</h2>
              <ElementDeleteThesis
                thesis={thesis}
                defenseJuryID={params.id}
                toggle={toggle}
                handleToggle={handleToggle}
                getListThesisOfDefenseJury={getListThesisOfDefenseJury}
              />
            </Grid>
            <Grid item sm={12} md={6}>
              <h2>Danh sách luận văn</h2>
              <ElementAddThesis
                notBelongThesis={notBelongThesis}
                defenseJuryID={params.id}
                toggle={toggle}
                handleToggle={handleToggle}
                getAllThesisNotBelongToDefenseJury={
                  getAllThesisNotBelongToDefenseJury
                }
              />
            </Grid>
          </Grid>
        </div>
        <div>
          <Grid container spacing={2}>
            <Grid item sm={12} md={6}>
              <h2>Danh sách giảng viên trong HĐ</h2>
              <ElementDeleteTeacher
                teacher={teacher}
                defenseJuryID={params.id}
                toggleTeacher={toggleTeacher}
                handleToggleTeacher={handleToggleTeacher}
                getListTeacherOfDefenseJury={getListTeacherOfDefenseJury}
                handlerIsLoad={handlerIsLoad}
                handlerNotLoad={handlerNotLoad}
              />
            </Grid>
            <Grid item sm={12} md={6}>
              <h2>Danh sách giảng viên</h2>
              <ElementAddTeacher
                listTeacher={listTeacher}
                defenseJuryID={params.id}
                toggleTeacher={toggleTeacher}
                handleToggleTeacher={handleToggleTeacher}
                getAllTeacherNotBelongToDefenseJury={
                  getAllTeacherNotBelongToDefenseJury
                }
                handlerIsLoad={handlerIsLoad}
                handlerNotLoad={handlerNotLoad}
              />
            </Grid>
          </Grid>
        </div>
        {/* {loading ?  (<LoadingOverlay
          active={true}
          spinner={true}
          text="Loading your content..."
        > </LoadingOverlay>) :<></>} */}
      </MuiThemeProvider>
    </>
  );
}

export default DefenseJuryDetail;
