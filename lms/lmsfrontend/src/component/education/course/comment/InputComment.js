import {makeStyles} from "@material-ui/core/styles";
import {Avatar, Button, TextField} from '@material-ui/core';
import {useState} from "react";

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
    flexDirection: "row",
    alignItems: 'center',
  },
  growItem: {
    flexGrow: 1,
    marginLeft: theme.spacing(1)
  },
  btnComment: {
    background: '#1976d2',
    color: 'white',
    marginLeft: theme.spacing(1),
    '&:hover': {
      backgroundColor: '#ccc',
      color: '#1976d2',
  }
  },
}));

export default function InputComment(props){
  const [value, setValue] = useState("")
  const classes = useStyles();

  const onChangeValue = (event) => {
    setValue(event.target.value);
    props.getMessageFromInput(event.target.value, "")
  }
  return(
    <div
      className={classes.root}
    >
      <Avatar>
        U
      </Avatar>
      <TextField
        className={classes.growItem}
        placeholder="Viết gì đó về video này"
        value={value}
        onChange={(event)=>onChangeValue(event)}
      />
      <Button
        className={classes.btnComment}
        onClick={()=>{
          props.commentOnCourse();
          setValue("")
        }}
      >
        Bình luận
      </Button>
    </div>
  )
}