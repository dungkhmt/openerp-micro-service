import {
  Button,
  Card,
  CardActions,
  CardContent,
  Checkbox,
  MenuItem,
  TextField,
  Tooltip,
  Typography,
} from "@material-ui/core/";
import React, {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import MaterialTable, {MTableToolbar} from "material-table";
import ModalLoading from "./ModalLoading"
import ModalDelete from "./ModalDelete"
import Delete from '@material-ui/icons/Delete';


function ThesisBelongPlan(props) {
  const defensePlanId = props.defensePlanId;
  const history = useHistory();
  const [toggle, setToggle] = useState(false)
  const [thesiss, setThesiss] = useState([]);
  const [thesisId, setThesisId] = useState();
  const [loginID, setLoginID] = useState();
  const [openLoading, setOpenLoading] = React.useState(false);
  const [open, setOpen] = React.useState(false);

  const columns = [
    {title: "ID", field: "id"},
    {title: "Tên luận văn", field: "thesisName"},
    {title: "Mô tả", field: "thesisAbstract"},
    {title: "Ngày tạo", field: "createdTime"},
  ];

  async function getAllThesisBelongPlan() {
    console.log(defensePlanId)
    setOpenLoading(true);
    request(
      // token,
      // history,
      "GET",
      `/${defensePlanId}/thesisBelongPlan`,
      (res) => {
        console.log(res.data.result)
        setThesiss(res.data.result)
        setOpenLoading(false);
      }
    );
  }

  const handleModalOpen = () => {
    history.push({
      pathname: `/thesis/create`,
    });

  };

  async function DeleteThesisById(thesisID, userLoginID) {
    setOpenLoading(true)
    var body = {
      id: thesisID,
      userLogin: userLoginID
    }
    request(
      "post",
      `/thesis/delete`,
      (res) => {
        console.log(res.data)
        setOpenLoading(false)
        setToggle(!toggle)
        setOpen(false)
        // setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
      },
      {
        onError: (e) => {
          // setShowSubmitSuccess(false);
          console.log(e)
        }
      },
      body
    ).then();
  }

  const handleModalClose = () => {
    setOpen(false);
  };


  useEffect(() => {
    getAllThesisBelongPlan();
  }, [toggle]);

  return (
    <Card>
      <MaterialTable
        title={"Danh sách luận văn"}
        columns={columns}
        data={thesiss}
        onRowClick={(event, rowData) => {
          console.log(rowData)
          history.push({
            pathname: `/thesis/${rowData.id}`,
            state: {},
          });
        }}
        actions={[
          {
            icon: Delete,
            tooltip: "Delete Thesis",
            onClick: (event, rowData) => {
              console.log(rowData)
              console.log(rowData.id)
              setThesisId(rowData.id)
              setLoginID(rowData.userLogin)
              setOpen(true)
              // DeleteThesisById(rowData.id,rowData.userLogin)


            }
          }
        ]}
        components={{
          Toolbar: (props) => (
            <div style={{position: "relative"}}>
              <MTableToolbar {...props} />
              <div
                style={{position: "absolute", top: "16px", right: "350px"}}
              >
                <Button onClick={handleModalOpen} color="primary">
                  Thêm mới
                </Button>
              </div>
            </div>
          ),
        }}
      />
      <ModalLoading openLoading={openLoading}/>
      <ModalDelete openDelete={open} handleDeleteClose={handleModalClose} thesisId={thesisId} userLoginID={loginID}
                   DeleteThesisById={DeleteThesisById}/>
    </Card>
  );
}

export default ThesisBelongPlan;
  