import { Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@mui/material";



const AutoScheduleDialog = ({
  title,
  submit,
  timeLimit,
  setTimeLimit,
  open,
  closeDialog,
}) => {
  return (
    <Dialog open={open} onClose={closeDialog}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Autocomplete
          options={[5, 10, 15, 30, 60]}
          value={timeLimit}
          onChange={(event, option) => {
            console.log(option);
            setTimeLimit(option);
          }}
          getOptionLabel={(option)=> `${option} giây`}
          renderInput={(params, option) => (
            <TextField {...params}>{option} giây</TextField>
          )}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={()=> closeDialog()}>Hủy</Button>
        <Button onClick={() => submit()}>Xác nhận</Button>
      </DialogActions>
    </Dialog>
  );
};

export default AutoScheduleDialog;
