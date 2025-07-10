// import { Box, Button, Grid, TextField, Typography } from "@mui/material";
// import useStyles from "screens/styles";
// import { useForm } from "react-hook-form";
// import StandardTable from "components/StandardTable";
// import { request } from "api";
// import { API_PATH } from "../apiPaths";
// import { convertTimeStampToDate } from "../utils/utils";
// import { errorNoti, successNoti } from "utils/notification";
// import { useHistory } from "react-router";
// import { useRouteMatch } from "react-router-dom";

// import { Fragment, useState, useEffect } from "react";
// import LoadingScreen from "components/common/loading/loading";
// import withScreenSecurity from "components/common/withScreenSecurity";

// const ReceiptRequestForApproval = ( props ) => {

//   const receiptId = props.match?.params?.id;
//   const history = useHistory();
//   const { path } = useRouteMatch();
  
//   const [receiptInfo, setReceiptInfo] = useState(null);
//   const [productTableData, setProductTableData] = useState([]);
//   const [isLoading, setLoading] = useState(true);

//   const classes = useStyles();
//   const { register, errors, handleSubmit, watch, getValues } = useForm();

//   const approve = () => {
//     request(
//       "put",
//       API_PATH.APPROVE_RECEIPT_REQUEST + "/" + receiptId,
//       (res) => {
//         if (res.status == 200) {
//           successNoti("Phê duyệt yêu cầu nhập hàng thành công");
//           history.push(`${path.slice(0, path.lastIndexOf("/"))}`);
//         } else {
//           errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau.");
//           history.push(`${path.slice(0, path.lastIndexOf("/"))}`);
//         }
//       }
//     )
//   }

//   const cancel = () => {
//     request(
//       "put",
//       API_PATH.CANCEL_RECEIPT_REQUEST + "/" + receiptId,
//       (res) => {
//         if (res.status == 200) {
//           successNoti("Hủy yêu cầu nhập hàng thành công");
//         } else {
//           errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau.");
//         }
//       }
//     )
//   }

//   useEffect(() => {
//     async function fetchData () {
//       await request(
//         "get",
//         API_PATH.SALE_MANAGEMENT_RECEIPT_REQUEST + "/" + receiptId,
//         (res) => {
//           setReceiptInfo(res.data);
//           setProductTableData(res.data?.items);
//         }
//       );
//       setLoading(false);
//     }

//     fetchData();
//   }, []);

//   return (
//   isLoading ? <LoadingScreen /> :
//   <Fragment>
//     <Box>
//       <Grid container justifyContent="space-between" className={classes.headerBox}>
//         <Grid>
//           <Typography variant="h5">Phê duyệt yêu cầu nhập hàng</Typography>
//         </Grid>
//         <Grid className={classes.buttonWrap}>
//         <Button variant="contained" className={classes.addButton} 
//           type="submit" onClick={handleSubmit(approve)} >Phê duyệt</Button>
//         </Grid>
//         <Grid classNam={classes.buttonWrap}>
//           <Button variant="contained" className={classes.addButton} 
//             type="submit" onClick={handleSubmit(cancel)}>Hủy</Button>
//         </Grid>
//       </Grid>
//     </Box>

//     <Box className={classes.formWrap} component="form">
//       <Box>
//         <Grid container spacing={2}>
//           <Grid item xs={12}>
//             <Box className={classes.boxInfor}>
//               <Typography className={classes.inforTitle} variant="h6">
//                 Thông tin chung
//               </Typography>
//               <Grid container spacing={3} className={classes.inforWrap}>
//                 <Grid item xs={12}>
//                   <Box className={classes.labelInput}>
//                     Người tạo đơn
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: false })}
//                     name="createdBy"
//                     InputProps={{
//                       readOnly: true,
//                     }}
//                     value={receiptInfo?.createdBy} />
//                 </Grid>
//                 <Grid item xs={6}>
//                   <Box className={classes.labelInput}>
//                     Ngày tạo đơn nhận hàng
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: false })}
//                     name="createdDate"
//                     InputProps={{
//                       readOnly: true,
//                     }}
//                     value={receiptInfo?.createdDate} />
//                 </Grid>
//                 <Grid item xs={6}>
//                   <Box className={classes.labelInput}>
//                     Ngày muốn nhận hàng
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: false })}
//                     name="expectedReceiveDate"
//                     InputProps={{
//                       readOnly: true,
//                     }}
//                     value={receiptInfo?.expectedReceiveDate} />
//                 </Grid>
//                 <Grid item xs={12}>
//                   <Box className={classes.labelInput}>
//                     Lý do tạo đơn
//                   </Box>
//                   <TextField 
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: false })}
//                     name="createdReason"
//                     multiline
//                     rows={4}
//                     InputProps={{
//                       readOnly: true,
//                     }}
//                     value={receiptInfo?.createdReason}
//                   />
//                 </Grid>
//               </Grid>
//             </Box>

//             <Box className={classes.boxInfor}>
//               <StandardTable 
//                 title="Danh sách hàng hóa"
//                 columns={[
//                   { title: "Tên hàng hóa", field: "productName" },
//                   { title: "Số lượng", field: "quantity" },
//                   { title: "Kho nhận", field: "warehouseName" }
//                 ]}
//                 data={productTableData}
//                 options={{
//                   selection: false,
//                   pageSize: 5,
//                   search: true,
//                   sorting: true,
//                 }}
//               />
//             </Box>
//           </Grid>
//         </Grid>
//       </Box>
//     </Box>
//   </Fragment>);
// }

// const SCR_ID = "SCR_WMSv2_RECEIPT_REQUEST_DETAIL_FOR_APPROVER";
// export default withScreenSecurity(ReceiptRequestForApproval, SCR_ID, true);
import { Box, Button, Grid, TextField, Typography } from "@mui/material";
import useStyles from "screens/styles";
import { useForm } from "react-hook-form";
import StandardTable from "components/StandardTable";
import { request } from "api";
import { API_PATH_2 } from "../apiPaths";
import { errorNoti, successNoti } from "utils/notification";
import { useHistory } from "react-router";
import { useRouteMatch } from "react-router-dom";

import { Fragment, useState, useEffect } from "react";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const ReceiptRequestForApproval = ( props ) => {

  const receiptId = props.match?.params?.id;
  const history = useHistory();
  const { path } = useRouteMatch();
  
  const [receiptInfo, setReceiptInfo] = useState(null);
  const [productTableData, setProductTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);

  const classes = useStyles();
  const { register, errors, handleSubmit, watch, getValues } = useForm();

  const approve = () => {
    request(
      "put",
      API_PATH_2.APPROVE_RECEIPT_REQUEST + "/" + receiptId,
      (res) => {
        if (res.status == 200) {
          successNoti("Phê duyệt yêu cầu nhập hàng thành công");
          history.push(`${path.slice(0, path.lastIndexOf("/"))}`);
        } else {
          errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau.");
          history.push(`${path.slice(0, path.lastIndexOf("/"))}`);
        }
      }
    )
  }

  const cancel = () => {
    request(
      "put",
      API_PATH_2.CANCEL_RECEIPT_REQUEST + "/" + receiptId,
      (res) => {
        if (res.status == 200) {
          successNoti("Hủy yêu cầu nhập hàng thành công");
          history.push(`${path.slice(0, path.lastIndexOf("/"))}`);
        } else {
          errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau.");
          history.push(`${path.slice(0, path.lastIndexOf("/"))}`);
        }
      }
    )
  }

  useEffect(() => {
    async function fetchData () {
      await request(
        "get",
        API_PATH_2.SALE_MANAGEMENT_RECEIPT_REQUEST + "/" + receiptId,
        (res) => {
          setReceiptInfo(res.data);
          setProductTableData(res.data?.items);
        }
      );
      setLoading(false);
    }

    fetchData();
  }, []);

  return (
  isLoading ? <LoadingScreen /> :
  <Fragment>
    <Box>
      <Grid container justifyContent="space-between" className={classes.headerBox}>
        <Grid>
          <Typography variant="h5">Phê duyệt yêu cầu nhập hàng</Typography>
        </Grid>
        <Grid className={classes.buttonWrap}>
        <Button variant="contained" className={classes.addButton} 
          type="submit" onClick={handleSubmit(approve)} >Phê duyệt</Button>
        </Grid>
        <Grid classNam={classes.buttonWrap}>
          <Button variant="contained" className={classes.addButton} 
            type="submit" onClick={handleSubmit(cancel)}>Hủy</Button>
        </Grid>
      </Grid>
    </Box>

    <Box className={classes.formWrap} component="form">
      <Box>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Box className={classes.boxInfor}>
              <Typography className={classes.inforTitle} variant="h6">
                Thông tin chung
              </Typography>
              <Grid container spacing={3} className={classes.inforWrap}>
                <Grid item xs={12}>
                  <Box className={classes.labelInput}>
                    Người tạo đơn
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    inputRef={register({ required: false })}
                    name="createdBy"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdBy} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Ngày tạo đơn nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    inputRef={register({ required: false })}
                    name="createdDate"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdDate} />
                </Grid>
                <Grid item xs={6}>
                  <Box className={classes.labelInput}>
                    Ngày muốn nhận hàng
                  </Box>
                  <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    inputRef={register({ required: false })}
                    name="expectedReceiveDate"
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.expectedReceiveDate} />
                </Grid>
                <Grid item xs={12}>
                  <Box className={classes.labelInput}>
                    Lý do tạo đơn
                  </Box>
                  <TextField 
                    fullWidth
                    variant="outlined"
                    size="small"
                    inputRef={register({ required: false })}
                    name="createdReason"
                    multiline
                    rows={4}
                    InputProps={{
                      readOnly: true,
                    }}
                    value={receiptInfo?.createdReason}
                  />
                </Grid>
              </Grid>
            </Box>

            <Box className={classes.boxInfor}>
              <StandardTable 
                title="Danh sách hàng hóa"
                columns={[
                  { title: "Tên hàng hóa", field: "productName" },
                  { title: "Số lượng", field: "quantity" },
                  { title: "Kho nhận", field: "warehouseName" }
                ]}
                data={productTableData}
                options={{
                  selection: false,
                  pageSize: 5,
                  search: true,
                  sorting: true,
                }}
              />
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Box>
  </Fragment>);
}

const SCR_ID = "SCR_WMSv2_RECEIPT_REQUEST_DETAIL_FOR_APPROVER";
export default withScreenSecurity(ReceiptRequestForApproval, SCR_ID, true);
