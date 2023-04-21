import React, {useState} from "react";
import {Box, Button, Card, CardContent} from "@material-ui/core";
import {useDispatch, useSelector} from "react-redux";
import {MuiThemeProvider} from "@material-ui/core/styles";
import MaterialTable, {MTableToolbar} from "material-table";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import {useHistory} from "react-router-dom";
import {authGet} from "../../../api";
import {tableIcons} from "../../../utils/iconutil";
import ModalCreate from "./ModalCreate";


function ResourceDomainList(props) {
  const history = useHistory();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const columns = [
    { field: "id", title: "Domain Id" },
    { field: "name", title: "Name"
    },
    { field: "createDateTime", title: "Created Time"},
  ];

  const [open,setOpen] = useState(false);
  // Functions
  const handleClose = () => {
    setOpen(false);
  }
  const onClickCreateNewButton = () => {
      setOpen(true);
  };
  return (
    <MuiThemeProvider>
        <Card>
          <CardContent>
            <MaterialTable
              title="Danh sách nguồn tham khảo"
              columns={columns}
              data={(query) =>
                new Promise((resolve, reject) => {
                let sortParam = "";
                if (query.orderBy !== undefined) {
                  sortParam =
                    "&sort=" + query.orderBy.field + "," + query.orderDirection;
                }
                let filterParam = "";
                if (query.filters.length > 0) {
                  let filter = query.filters;
                  filter.forEach((v) => {
                    filterParam = v.column.field + "=" + v.value + "&";
                  });
                  filterParam =
                    "&" + filterParam.substring(0, filterParam.length - 1);
                }

                authGet(
                  dispatch,
                  token,
                  "/domains" +
                    "?size=" +
                    query.pageSize +
                    "&page=" + query.page +
                    sortParam +
                    filterParam
                ).then(
                  (res) => {
                    console.log(res)
                    resolve({
                      data: res.Domains,
                      page: res.currentPagge,
                      totalCount: res.totalItems,
                    });
                  },
                  (error) => {
                    console.log("error");
                  }
                );
              })
           }
              onRowClick = {(event,rowData) => {
                console.log(rowData)
                history.push({
                pathname: `/edu/domains/${rowData.id}/resources`,
                state: {
                  domainId: rowData.id,
                },
              });
              }}
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
                filtering: true,
                search: false,
                actionsColumnIndex: -1,
              }}
              components={{
                Toolbar: (props) => (
                  <div>
                    <MTableToolbar {...props} />
                    <MuiThemeProvider>
                   
                      <Box display="flex" justifyContent="flex-end" width="98%">
                       
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={onClickCreateNewButton}
                          startIcon={<AddCircleIcon />}
                          style={{ marginRight: 16 }}
                        >
                          Thêm mới
                        </Button>
                        <ModalCreate open={open} handleClose = {handleClose}/>
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

export default ResourceDomainList;
