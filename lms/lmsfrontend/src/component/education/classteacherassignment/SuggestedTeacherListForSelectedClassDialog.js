import {makeStyles} from "@material-ui/core/styles";
import {Avatar, Divider, List, ListItem, ListItemAvatar, ListItemText, Typography,} from "@mui/material";
import {grey} from "@mui/material/colors";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import {FcBusinessman} from "react-icons/fc";
import SimpleBar from "simplebar-react";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    // paddingTop: theme.spacing(1) / 2,
    minWidth: 480,
    minHeight: 64,
  },
  listItem: {
    paddingLeft: theme.spacing(1),
    paddingRight: theme.spacing(1),
    paddingTop: theme.spacing(1) * 1.5,
    paddingBottom: theme.spacing(1) * 1.5,
  },
  btn: { marginRight: theme.spacing(1) },
  applyBtn: { minWidth: 72 },
}));

function SuggestedTeacherListForSelectedClassDialog({
  classId,
  suggestions,
  open,
  handleClose,
  onReassign,
}) {
  const classes = useStyles();

  return (
    <CustomizedDialogs
      open={open}
      handleClose={handleClose}
      title={
        suggestions
          ? `Gợi ý giảng viên cho lớp ${classId} (${suggestions.length})`
          : `Gợi ý giảng viên cho lớp ${classId}`
      }
      contentTopDivider
      content={
        suggestions ? (
          suggestions.length === 0 ? (
            <Typography
              color="textSecondary"
              gutterBottom
              style={{ padding: 8 }}
            >
              Không tìm thấy giảng viên nào phù hợp trong kế hoạch.
            </Typography>
          ) : (
            <SimpleBar
              style={{
                height: "100%",
                minHeight: 220,
                maxHeight: 480,
                width: 480,
                overflowX: "hidden",
                overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu's scrollbar reach end
              }}
            >
              <List disablePadding>
                {suggestions.map((teacher, index) => (
                  <>
                    <ListItem
                      alignItems="flex-start"
                      key={teacher.teacherId}
                      className={classes.listItem}
                    >
                      <ListItemAvatar style={{ minWidth: 52 }}>
                        <Avatar alt="Avatar" style={{ background: grey[200] }}>
                          <FcBusinessman size={24} />
                        </Avatar>
                      </ListItemAvatar>

                      <ListItemText
                        primary={teacher.teacherName}
                        secondary={
                          <>
                            {teacher.moveClass
                              ? teacher.moveClass.map((c, i) => (
                                  <>
                                    <Typography
                                      variant="body2"
                                      style={{ whiteSpace: "pre-wrap" }} // break line on \n and wrap text according to parent width
                                    >
                                      Chuyển lớp {c.classCode} cho:{"\n"}
                                      {c.infoNewTeachers}
                                      {i !== teacher.moveClass.length - 1 &&
                                        "\n"}
                                    </Typography>
                                  </>
                                ))
                              : null}
                          </>
                        }
                      />

                      <TertiaryButton
                        className={classes.applyBtn}
                        onClick={() => onReassign(teacher.teacherId)}
                      >
                        Áp dụng
                      </TertiaryButton>
                    </ListItem>

                    {index < suggestions.length - 1 && (
                      <Divider
                        variant="inset"
                        component="li"
                        style={{ marginRight: 8 }}
                      />
                    )}
                  </>
                ))}
              </List>
            </SimpleBar>
          )
        ) : (
          <Typography
            color="textSecondary"
            gutterBottom
            style={{ padding: 8, paddingBottom: 73 }}
          >
            Đang tìm các giảng viên phù hợp trong kế hoạch...
          </Typography>
        )
      }
      actions={
        suggestions &&
        suggestions.length === 0 && (
          <PrimaryButton className={classes.btn} onClick={handleClose}>
            Đã hiểu
          </PrimaryButton>
        )
      }
      classNames={{ content: classes.dialogContent }}
    />
  );
}

export default SuggestedTeacherListForSelectedClassDialog;
