import { makeStyles } from "@mui/styles";

const useStyles = makeStyles(() => ({
  header: {
    boxShadow: "0 3px 5px rgba(57, 63, 72, 0.3)",
    backgroundColor: "#FFF",
  },
  title: {
    padding: 8,
    boxShadow: "0 3px 5px rgba(57, 63, 72, 0.3)",
    backgroundColor: "#FFF",
    textAlign: "center",
    position: "relative",
  },
  boxWrap: {
    backgroundColor: "#FFF",
    borderRadius: "0 0 3px 3px",
    boxShadow: "0px 2px 4px rgb(168 168 168 / 25%)",
  },
  rootInput: {
    marginRight: 10,
  },
  settingInput: {
    "& .MuiOutlinedInput-input": {
      padding: "10.5px 14px ",
      width: "100%",
    }
  },
  shelfInput: {
    borderRadius: 6,
    border: "1px solid #ccc"
  },
  icon: {
    position: "absolute",
    top: 0,
    right: 0,
    minWidth: 30,
    width: 30,
    height: 30,
    borderTopLeftRadius: 0,
    borderBottomRightRadius: 0,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 0,
    backgroundColor: "#D23",
    "& .MuiButton-label": {
      display: "contents",
    }
  },
  iconColor: {
    color: "#FFF",
  },
  removeIcon:{
    color: "#CCC",
    fontSize: '32px !important',
    cursor:"pointer",
  },
  removeIconBox:{
    "& :hover":{
      color: "#D23",
    }
  },
  addIcon:{
    color: "#1976d2",
    marginRight: 8,
    fontSize: '32px !important',
  },
  addIconBox:{
    "& :hover":{
      filter: `brightness(80%)`
    },
  },
  rerloadIconBox:{
    "& :hover":{
      filter: `brightness(80%)`
    },
    padding: 4,
    borderRadius: 3,
    background:"#f0f8ff"
  },
  addIconWrap:{
    width:"100%",
    display:"flex", 
    alignItems:"center",
    marginTop:"8px",
    padding:"8px",
    cursor: "pointer",
    color: "#1976d2",
    justifyContent: "center",
  },
  reloadIconWrap:{
    width:"100%",
    display:"flex", 
    alignItems:"center",
    cursor: "pointer",
    color: "#1976d2",
    justifyContent: "center",
  },
  listWrap: {
    overflowY: "auto",
    margin: 0,
    maxHeight: 700,
    padding: 0,
    listStyle: "none",
    '&::-webkit-scrollbar': {
      width: '0.3em'
    },
    '&::-webkit-scrollbar-track': {
      boxShadow: 'inset 0 0 6px rgba(0,0,0,0.00)',
      webkitBoxShadow: 'inset 0 0 6px rgba(0,0,0,0.00)'
    },
    '&::-webkit-scrollbar-thumb': {
      backgroundColor: '#87CEFA',
      outline: '1px solid #87CEFA'
    }
  },
  btnSubmit: {
    marginTop: 10,
    padding: "4px 16px",
    backgroundColor: "#0aaaaa",
    position: "absolute",
    right: 20,
  },
  reserBtn: {
    marginLeft: "auto",
    backgroundColor: "#0aaaaa",
    position: "absolute",
    right: 0,
    top: 0,
    padding: "8px",
    cursor: "pointer",
  },
  canvasWrap: {
    padding: 10,
    height: 800,
    // maxHeight: 800,
    width: "100%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  stageWrap:{
    position:"relative",
    height: "100%",
    width: "100%",
  },
  warehousePage: {

  },
  titleWap:{
    display: "flex",
    justifyContent:"space-between",
    alignItems: "center",
    padding: "4px 8px",
    borderBottom: "1px solid #E8EAEB",
  },
  headerBox: {
    backgroundColor: "#FFF",
    marginBottom: 30,
    boxShadow: "0px 2px 4px rgb(168 168 168 / 25%)",
    zIndex: 1,
    display: "flex",
    alignItems: "center",
    padding: "0 8px",
    borderRadius: 3,
  },
  buttonWrap:{
    "& .MuiButton-contained:hover" :{
      backgroundColor : "#1565c0"
    }
  },
  addButton:{
    color: "#FFF",
    backgroundColor: "#1976d2",
    margin: "10px 0",
  },
  formWrap:{
    width: "100%",
    marginBottom: 24,
    borderRadius: 3,
  },
  boxInfor:{
    // backgroundColor: "#FFF",
    marginBottom: 40,
    boxShadow: "0px 2px 4px rgb(168 168 168 / 25%)",
    borderRadius: 3,
    "& .MuiGrid-spacing-xs-3":{
      width: "100%",
      margin: 0,
    }
  },
  inforTitle:{
    backgroundColor: "#FFF",
    padding: "8px",
    borderBottom: "1px solid #E8EAEB",
  },
  inforWrap:{
    backgroundColor: "#FFF",
    padding: "16px 8px",
    "& .MuiGrid-spacing-xs-3":{
      width :"100%",
      margin: 0
    }
  },
  labelInput:{
    marginBottom: 8,
    fontSize: 16,
  },
  detailWrap:{
    margin: 0,
  }
})
)
export default useStyles;
