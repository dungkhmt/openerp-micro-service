import { Box } from "@mui/material";
import { StandardTable } from "erp-hust/lib/StandardTable";
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { request } from "../../../api";
function Thesis() {
  const navigate = useNavigate();
  const [thesiss, setThesiss] = useState([]);
  const [selectThesiss, setSelectThesiss] = useState([]);
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);
  const [listPlan, setListPlan] = React.useState([]);
  const [searchText, setSearchText] = useState("");
  const [thesisPlanName, setThesisPlanName] = React.useState("");
  const [defenseJuryName, setDefenseJuryName] = React.useState("");
  const [listJury, setListJury] = React.useState([]);
  const [key, setKey] = React.useState("");
  const [toggle, setToggle] = useState(false);
  const [openLoading, setOpenLoading] = React.useState(false);
  const columns = [
    { title: "Tên luận văn", field: "name" },
    { title: "Mô tả", field: "thesis_abstract" },
    // {title:"Tên chương trình",field:"program_name"},
    { title: "Đợt bảo vệ", field: "thesisPlanName" },
    { title: "Tên HĐ", field: "defense_jury_name" },
    { title: "Người hướng dẫn", field: "supervisor_name" },
    // {title:"Keyword",field:"keyword"},
    { title: "Người tạo", field: "student_name" },
    { title: "Ngày tạo", field: "createdTime" },
  ];
  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;
  const handlerSearch = (event) => {
    event.preventDefault();
    setOpenLoading(true);
    let planRes = listPlan.filter((e) => e.name == thesisPlanName);
    console.log(planRes);
    let juryRes = listJury.filter((e) => e.name == defenseJuryName);
    console.log(juryRes);
    let planInput = "";
    let juryInput = "";
    if (planRes.length > 0) {
      planInput = planRes[0].id;
    }
    if (juryRes.length > 0) {
      juryInput = juryRes[0].id;
    }
    let body = {
      key: key,
      thesisPlanId: planInput,
      juryId: juryInput,
    };
    request(
      "post",
      "/thesis/_search",
      (res) => {
        console.log(res.data);
        setOpenLoading(false);
        if (res.data.ok) {
          setThesiss(res.data.result);
        } else {
          setThesiss([]);
        }
      },
      {
        onError: (e) => {},
      },
      body
    ).then();
  };

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      "/thesis_defense_plan",
      (res) => {
        console.log("Plan", res.data);
        let objAll = {
          id: "",
          name: "All",
        };
        res.data.unshift(objAll);
        console.log("Plan", res.data);
        setListPlan(res.data);
      }
    );
  }

  async function getAllJury() {
    request(
      // token,
      // history,
      "GET",
      "/defense_jurys",
      (res) => {
        console.log("Jury", res.data);
        let objAll = {
          id: "",
          name: "All",
        };
        res.data.DefenseJurys.unshift(objAll);
        console.log("Plan", res.data.DefenseJurys);
        setListJury(res.data.DefenseJurys);
      }
    );
  }

  const displayedPlanOptions = useMemo(
    () => listPlan.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  const displayedJuryOptions = useMemo(
    () => listJury.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );

  async function getAllThesis() {
    setOpenLoading(true);
    request(
      // token,
      // history,
      "GET",
      "/thesis",
      (res) => {
        console.log(res.data.content);
        setOpenLoading(false);
        setThesiss(res.data.content);
      }
    );
  }

  const handlerCreate = () => {
    navigate(`/thesis/create`);
  };

  async function DeleteThesisById(thesisID, userLoginID) {
    setOpenLoading(true);
    var body = {
      id: thesisID,
      userLogin: userLoginID,
    };
    request(
      "post",
      `/thesis/delete`,
      (res) => {
        console.log(res.data);
        setOpenLoading(false);
        setToggle(!toggle);
        // setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
      },
      {
        onError: (e) => {
          // setShowSubmitSuccess(false);
          console.log(e);
        },
      },
      body
    ).then();
  }

  useEffect(() => {
    getAllJury();
    getAllPlan();
  }, [showSubmitSuccess]);
  useEffect(() => {
    getAllThesis();
  }, [toggle]);

  return (
    <Box>
      {/* <MaterialTable
        title={"Danh sách đề tài"}
        columns={columns}
        // data={thesiss}
        actions={[
          {
            icon: Delete,
            tooltip: "Delete Thesis",
            onClick: (event, rowData) => {
              console.log(rowData);
              console.log(rowData.id);
              DeleteThesisById(rowData.id, rowData.userLoginID);
            },
          },
        ]}
        components={{
          Toolbar: (props) => (
            <div style={{ position: "relative" }}>
              <MTableToolbar {...props} />
              <div
                style={{ position: "absolute", top: "16px", right: "350px" }}
              ></div>
            </div>
          ),
        }}
      /> */}
      <StandardTable
        title={"Danh sách đề tài"}
        data={thesiss}
        columns={columns}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
      {/* <ModalLoading openLoading={openLoading} /> */}
    </Box>
  );
}

export default Thesis;
