import DateFnsUtils from "@date-io/date-fns";
import {
  Avatar,
  Card,
  CardContent,
  CardHeader,
  Grid,
  IconButton,
  InputAdornment,
  TextField,
  Typography,
} from "@material-ui/core";
import {makeStyles, MuiThemeProvider} from "@material-ui/core/styles";
import AddIcon from "@material-ui/icons/Add";
import EditIcon from "@material-ui/icons/Edit";
import {DateTimePicker, MuiPickersUtilsProvider} from "@material-ui/pickers";
import {ContentState, convertToRaw, EditorState} from "draft-js";
import draftToHtml from "draftjs-to-html";
import htmlToDraft from "html-to-draftjs";
import _ from "lodash";
import React, {useEffect, useState} from "react";
import {Editor} from "react-draft-wysiwyg";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {useForm} from "react-hook-form";
import {FcCalendar} from "react-icons/fc";
//import { useSelector } from "react-redux";
import {useHistory, useParams} from "react-router";
import {request} from "../../../../api";
import NegativeButton from "../../../../component/education/classmanagement/NegativeButton";
import useOnMount from "../../../../component/education/classmanagement/onMount";
import PositiveButton from "../../../../component/education/classmanagement/PositiveButton";
import {errorNoti} from "../../../../utils/notification";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
    borderRadius: 6,
  },
  textField: {
    width: 380,
  },
  container: {},
  cancelBtn: {
    minWidth: 112,
    fontWeight: "normal",
    marginRight: 10,
  },
  createOrUpdateBtn: {
    minWidth: 112,
    fontWeight: "normal",
  },
}));

const editorStyle = {
  toolbar: {
    background: "#90caf9",
  },
  editor: {
    border: "1px solid black",
    minHeight: "300px",
  },
};

function CreateAssignment() {
  const classes = useStyles();
  const params = useParams();
  const assignId = params.assignmentId;
  const history = useHistory();
  //const token = useSelector((state) => state.auth.token);

  // Use for updating.
  const [assignDetail, setAssignDetail] = useState({});

  // Editor.
  const [editorState, setEditorState] = useState(EditorState.createEmpty());

  // Pickers.
  const pickerProps = {
    inputVariant: "outlined",
    size: "small",
    cancelLabel: "Huỷ",
    okLabel: "Chọn",
    ampm: false,
    disablePast: true,
    format: "yyyy-MM-dd  HH:mm:ss",
    strictCompareDates: true,
    InputProps: {
      endAdornment: (
        <InputAdornment position="end">
          <IconButton>
            <FcCalendar size={32} />
          </IconButton>
        </InputAdornment>
      ),
    },
    TextFieldComponent: (props) => (
      <TextField disabled className={classes.textField} {...props} />
    ),
  };

  // Form.
  const [notAllowedChanging, setNotAllowedChanging] = useState(false);
  const [invalidChange, setInvalidChange] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const {
    register,
    errors,
    watch,
    handleSubmit,
    setValue,
    setError,
    clearError,
  } = useForm({
    defaultValues: {
      name: "",
      openTime: (() => {
        let date = new Date();

        date.setDate(date.getDate() + 1);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        date.setMilliseconds(0);

        return date;
      })(),
      closeTime: (() => {
        let date = new Date();

        date.setDate(date.getDate() + 1);
        date.setHours(23);
        date.setMinutes(59);
        date.setSeconds(59);
        date.setMilliseconds(0);

        return date;
      })(),
    },
  });

  // Functions.
  const getData = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/assignment/${params.assignmentId}/student`,
      (res) => {
        let data = res.data.assignmentDetail;
        let openTime = new Date(data.openTime);
        let closeTime = new Date(data.closeTime);

        setValue([
          { name: data.name },
          { openTime: openTime },
          { closeTime: closeTime },
        ]);

        const blocksFromHtml = htmlToDraft(data.subject);
        const { contentBlocks, entityMap } = blocksFromHtml;
        const contentState = ContentState.createFromBlockArray(
          contentBlocks,
          entityMap
        );

        setEditorState(EditorState.createWithContent(contentState));

        setAssignDetail({
          ...data,
          openTime,
          closeTime,
        });
      },
      {}
    );
  };

  // onChangeHandlers.
  const onChangeOpenTime = (newDate) => {
    if (newDate.getTime() !== new Date(watch("openTime")).getTime()) {
      // Handle server errors.
      if (errors.openTime) {
        if (errors.openTime.type === "require future date") {
          // Accept just changing the value will clear the error,
          // because the new value may fall in [start of day, current open time) or (current open time, submit time).
          clearError("openTime");
        } else if (errors.openTime.type === "not allowed changing") {
          if (newDate.getTime() === assignDetail.openTime.getTime()) {
            clearError("openTime");
          }
        }
      } else {
        if (notAllowedChanging) {
          // Runs in each OnChange and runs only when recieve 'not allowed changing' error after submiting.
          if (newDate.getTime() !== assignDetail.openTime.getTime()) {
            setError(
              "openTime",
              "not allowed changing",
              "Vui lòng chọn thời điểm ban đầu vì bài tập đã được giao"
            );
          }
        }
      }

      // Handle client error.
      if (newDate.getTime() > new Date(watch("closeTime")).getTime()) {
        // Only set this error when 'close time' is not having any errors now.
        if (!errors.closeTime && !notAllowedChanging) {
          setError(
            "closeTime",
            "require subsequent date",
            "Vui lòng chọn thời điểm sau ngày giao"
          );
        }
      } else {
        if (errors.closeTime?.type === "require subsequent date") {
          clearError("closeTime");
        }
      }

      setValue("openTime", newDate);
    }
  };

  const onChangeCloseTime = (newDate) => {
    if (newDate.getTime() !== new Date(watch("closeTime")).getTime()) {
      if (errors.closeTime) {
        if (errors.closeTime.type === "require subsequent date") {
          // The 'require subsequent date' error can be removed.
          if (new Date(watch("openTime")).getTime() <= newDate.getTime()) {
            if (invalidChange) {
              // Revalidate the 'ic' error is REQUIRED, the 'rsd' error must be removed.
              if (newDate.getTime() < assignDetail.closeTime.getTime()) {
                // Removed the 'rsd' error but got the 'ic' error, so set 'ic' error instead of 'rsd' error.
                setError(
                  "closeTime",
                  "invalid change",
                  "Vui lòng chọn thời điểm ban đầu hoặc trong tương lai"
                );
              } else {
                // Remove 'rsd' error.
                clearError("closeTime");
              }
            } else {
              // Revalidate the 'ic' error is NOT REQUIRED, the 'rsd' error must be removed.
              clearError("closeTime");
            }
          }
        } else if (errors.closeTime.type === "invalid change") {
          if (newDate.getTime() >= assignDetail.closeTime.getTime()) {
            clearError("closeTime");
          }
        }
      } else {
        if (invalidChange) {
          if (newDate.getTime() < assignDetail.closeTime.getTime()) {
            setError(
              "closeTime",
              "invalid change",
              "Vui lòng chọn thời điểm ban đầu hoặc trong tương lai"
            );
          }
        } else if (new Date(watch("openTime")).getTime() > newDate.getTime()) {
          setError(
            "closeTime",
            "require subsequent date",
            "Vui lòng chọn thời điểm sau ngày giao"
          );
        }
      }

      newDate.setSeconds(59);
      setValue("closeTime", newDate);
    }
  };

  const onChangeEditorState = (editorState) => {
    setEditorState(editorState);
  };

  const onSubmit = (formData) => {
    setIsProcessing(true);
    let subject = draftToHtml(convertToRaw(editorState.getCurrentContent()));

    if (assignId) {
      // Check for changes.
      if (
        formData.name !== assignDetail.name ||
        new Date(formData.openTime).getTime() !==
          new Date(assignDetail.openTime).getTime() ||
        new Date(formData.closeTime).getTime() !==
          new Date(assignDetail.closeTime).getTime() ||
        subject !== assignDetail.subject
      ) {
        request(
          // token,
          // history,
          "put",
          `/edu/assignment/${assignId}`,
          () => {
            history.goBack();
          },
          {
            onError: () => {
              setIsProcessing(false);
            },
            400: (e) => {
              let errors = e.response.data?.errors;

              if (errors) {
                errors.forEach((error) => {
                  switch (error.location) {
                    case "openTime":
                      setError("openTime", error.type, error.message);

                      if (error.type === "require future date") {
                        setNotAllowedChanging(false);
                      } else {
                        setNotAllowedChanging(true);
                      }
                      break;
                    case "closeTime":
                      setError("closeTime", error.type, error.message);

                      if (error.type === "require subsequent date") {
                        setInvalidChange(false);
                      } else {
                        setInvalidChange(true);
                      }
                      break;
                    case "id":
                      errorNoti("Bài tập đã bị xoá trước đó.");
                      break;
                    default:
                      break;
                  }
                });
              } else {
                errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
              }
            },
            rest: () => {
              errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
            },
          },
          { ...formData, subject: subject }
        );
      } else {
        history.goBack();
      }
    } else {
      request(
        // token,
        // history,
        "post",
        "/edu/assignment",
        () => {
          history.goBack();
        },
        {
          onError: () => {
            setIsProcessing(false);
          },
          400: (e) => {
            let errors = e.response.data?.errors;

            if (errors) {
              errors.forEach((error) => {
                switch (error.location) {
                  case "openTime":
                    setError("openTime", error.type, error.message);
                    break;
                  case "closeTime":
                    setError("closeTime", error.type, error.message);
                    break;
                  case "classId":
                    errorNoti("Lớp đã bị xoá trước đó.");
                    break;
                  default:
                    break;
                }
              });
            } else {
              errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
            }
          },
          rest: () => {
            errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
          },
        },
        { ...formData, subject: subject, classId: params.classId }
      );
    }
  };

  const onCancel = () => {
    history.goBack();
  };

  useOnMount(() => {
    register({ name: "openTime", type: "text" });
    register({ name: "closeTime", type: "text" });
  });

  useEffect(() => {
    if (assignId) getData();
  }, []);

  return (
    <MuiThemeProvider>
      <Card className={classes.card}>
        <CardHeader
          avatar={
            <Avatar style={{ background: "#5e35b1" }}>
              {assignId ? <EditIcon /> : <AddIcon />}
            </Avatar>
          }
          title={
            <Typography variant="h5">
              {assignId ? "Chỉnh sửa thông tin bài tập" : "Tạo bài tập"}
            </Typography>
          }
        />
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container md={12} justify="center">
              <Grid
                container
                md={11}
                direction="column"
                alignItems="flex-start"
                spacing={3}
                className={classes.container}
              >
                <Grid item>
                  <TextField
                    name="name"
                    label="Tên bài tập*"
                    variant="outlined"
                    value={watch("name")}
                    size="small"
                    error={!!errors.name}
                    helperText={errors.name?.message}
                    className={classes.textField}
                    inputRef={register({
                      required: "Trường này được yêu cầu",
                      maxLength: {
                        value: 255,
                        message: "Vui lòng chọn tên không vượt quá 255 kí tự",
                      },
                      validate: (name) => {
                        if (_.isEmpty(name.trim()))
                          return "Vui lòng chọn tên hợp lệ";
                      },
                    })}
                  />
                </Grid>
                <Grid item>
                  <MuiPickersUtilsProvider utils={DateFnsUtils}>
                    <DateTimePicker
                      name="openTime"
                      label="Ngày giao"
                      value={watch("openTime")}
                      error={!!errors.openTime}
                      helperText={errors.openTime?.message}
                      onChange={onChangeOpenTime}
                      KeyboardButtonProps={{
                        "aria-label": "openTime",
                      }}
                      {...pickerProps}
                      minDate={
                        assignId
                          ? assignDetail.openTime?.getTime() >
                            new Date().getTime()
                            ? new Date()
                            : assignDetail.openTime
                          : undefined
                      }
                      disablePast={assignId ? false : true}
                    />
                  </MuiPickersUtilsProvider>
                </Grid>
                <Grid item>
                  <MuiPickersUtilsProvider utils={DateFnsUtils}>
                    <DateTimePicker
                      name="closeTime"
                      label="Hạn nộp bài"
                      value={watch("closeTime")}
                      error={!!errors.closeTime}
                      helperText={errors.closeTime?.message}
                      onChange={onChangeCloseTime}
                      KeyboardButtonProps={{
                        "aria-label": "closeTime",
                      }}
                      {...pickerProps}
                    />
                  </MuiPickersUtilsProvider>
                </Grid>
                <Grid item>
                  <Editor
                    editorState={editorState}
                    handlePastedText={() => false}
                    onEditorStateChange={onChangeEditorState}
                    toolbarStyle={editorStyle.toolbar}
                    editorStyle={editorStyle.editor}
                  />
                </Grid>
              </Grid>

              <Grid item md={10}>
                <NegativeButton
                  label="Huỷ"
                  className={classes.cancelBtn}
                  onClick={onCancel}
                />
                <PositiveButton
                  disabled={isProcessing}
                  type="submit"
                  label={assignId ? "Chỉnh sửa" : "Tạo bài tập"}
                  className={classes.createOrUpdateBtn}
                />
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>
      {/* <DevTool control={control} /> */}
    </MuiThemeProvider>
  );
}

export default CreateAssignment;
