import React, {useState} from "react";
import {Box, Button, Card, CardContent} from "@material-ui/core";
import {useDispatch, useSelector} from "react-redux";
import {MuiThemeProvider} from "@material-ui/core/styles";
import MaterialTable, {MTableToolbar} from "material-table";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import {useHistory, useParams} from "react-router-dom";
import {authGet, authPost} from "../../../api";
import {tableIcons} from "../../../utils/iconutil";
import ModalCreateResource from "./ModalCreateResource"

function ResourceList(props) {
  const history = useHistory();
  const params = useParams();
  const dispatch = useDispatch();
  const [open,setOpen] = useState(false);
  const token = useSelector((state) => state.auth.token);
  const columns = [
    { field: "link", title: "Link" ,
    render: rowData => <a href={rowData.link} target="_blank">{rowData.link}</a>
    },
    { field: "description", title: "Description"},
    { field: "statusId", title: "Status" },
  ];

  const handleClose = () => {
    setOpen(false);
  }

  const onClickCreateNewButton = () => {
    setOpen(true);
  };

  const onClickBackButton = () => {
    history.push({
      pathname: "/edu/teach/resource-links/list",
      state: {},
    });
  };

  return (
    <MuiThemeProvider>
        <Card>
          <CardContent>
            <MaterialTable
              title="Danh sách link tham khảo"
              columns={columns}
              data={(query) =>
                new Promise((resolve, reject) => {
                let sortParam = "";
                if (query.orderBy !== undefined) {
                  sortParam =
                    "&sort=" + query.orderBy.field + "," + query.orderDirection;
                }

                if (query.search.length > 0) {
                  authPost(
                  dispatch,
                  token,
                  `/domains/${params.id}/resources`,
                  {description:query.search}
                ).then(
                  (res) => {
                   console.log(res)
                    resolve({
                      data: res,
                    });
                  },
                  (error) => {
                    console.log("error");
                  }
                );
                }else {
                  authGet(
                  dispatch,
                  token,
                  `/domains/${params.id}/resources` +
                    "?size=" +
                    query.pageSize +
                    "&page=" +
                    query.page +
                    sortParam 
                ).then(
                  (res) => {
                    resolve({
                      data: res.content,
                      page: res.number,
                      totalCount: res.totalElements,
                    });
                  },
                  (error) => {
                    console.log(error);
                  }
                );
                }
                
              })
           }
              icons={tableIcons}
              localization={{
                header: {
                  actions: "",
                },
                body: {
                  emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
                  filterRow: {
                    filterTooltip: "Lọc",
                  },
                },
              }}
              options={{
                search: true,
                actionsColumnIndex: -1,
              }}
              components={{
                Toolbar: (props) => (
                  <div>
                    <MTableToolbar {...props} />
                    <MuiThemeProvider>
                      <Box display="flex" justifyContent="space-between" width="98%">
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={onClickBackButton}
                            startIcon={<ArrowBackIosIcon />}
                            style={{ marginRight: 16, marginLeft:26 }}
                          >
                            Quay lai
                          </Button>
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={onClickCreateNewButton}
                          startIcon={<AddCircleIcon />}
                          style={{ marginRight: 16 }}
                        >
                          Thêm mới
                        </Button>
                        <ModalCreateResource open={open} handleClose = {handleClose} domainId = {params.id}/>
                      </Box>
                      
                    </MuiThemeProvider>
                  </div>
                ),
              }}
            />
          </CardContent>
        </Card>
      </MuiThemeProvider>
  );
}

export default ResourceList;
