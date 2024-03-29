import { Box, Typography } from "@mui/material";
import { useState } from "react";
import toast from "react-hot-toast";
import { CustomMDEditor } from "../../../components/editor/md-editor/CustomMDEditor";
import { LoadingButton } from "../../../components/mui/button/LoadingButton";
import { useTaskContext } from "../../../hooks/useTaskContext";
import { TaskService } from "../../../services/api/task.service";
import { TitleWrapper } from "../TaskViewLeft";

const CommentSection = () => {
  const { isUpdate, setIsUpdate, task } = useTaskContext();
  const [loading, setLoading] = useState(false);
  const [comment, setComment] = useState("");

  const onComment = async () => {
    try {
      setLoading(true);
      await TaskService.updateTask(task.id, {
        note: comment,
      });
      setIsUpdate(!isUpdate);
    } catch (e) {
      toast.error("Có lỗi xảy ra khi thêm bình luận");
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <TitleWrapper>
        <Typography sx={{ fontWeight: 600, fontSize: "1rem" }}>
          Bình luận
        </Typography>
      </TitleWrapper>
      <CustomMDEditor
        value={comment}
        onChange={setComment}
        setValue={setComment}
      />
      <Box display={"flex"} justifyContent={"flex-end"} mt={2}>
        <LoadingButton
          variant="contained"
          color="primary"
          onClick={onComment}
          disabled={comment === ""}
          loading={loading}
        >
          Bình luận
        </LoadingButton>
      </Box>
    </>
  );
};

export default CommentSection;
