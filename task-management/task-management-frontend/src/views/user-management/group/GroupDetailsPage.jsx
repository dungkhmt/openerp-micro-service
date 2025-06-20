import { Box, CircularProgress, IconButton, Typography } from "@mui/material";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";
import GroupHeader from "./GroupHeader";
import TeamMembersTable from "./TeamMembersTable";
import { Icon } from "@iconify/react";
import {
  fetchGroupById,
  fetchUsersByGroupId,
} from "../../../store/user-management/group";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";

const GroupDetailsPage = () => {
  const { groupId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { ref, updateMaxHeight } = usePreventOverflow();
  const { currentGroup, groupMembers, fetchLoading, errors } = useSelector(
    (state) => state.userGroup
  );

  useEffect(() => {
    if (groupId) {
      dispatch(fetchGroupById(groupId));
      dispatch(fetchUsersByGroupId(groupId));
    }
  }, [dispatch, groupId]);

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  if (fetchLoading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", mt: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (errors.length > 0) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography color="error">
          Error: {errors[errors.length - 1].message || "Failed to load group"}
        </Typography>
      </Box>
    );
  }

  if (!currentGroup || !groupMembers) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography>Group not found</Typography>
      </Box>
    );
  }

  return (
    <>
      <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
        <IconButton onClick={() => navigate(-1)} sx={{ mr: 1 }}>
          <Icon icon="mdi:arrow-left" />
        </IconButton>
        <Typography variant="h6">Chi tiết nhóm</Typography>
      </Box>
      <Box ref={ref} sx={{ p: 3, overflowY: "auto" }}>
        <GroupHeader group={currentGroup} members={groupMembers} />
        <TeamMembersTable members={groupMembers} />
      </Box>
    </>
  );
};

export default GroupDetailsPage;
