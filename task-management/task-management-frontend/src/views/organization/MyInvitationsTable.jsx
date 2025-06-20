import {
  Box,
  Typography,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Button,
  Stack,
  IconButton,
} from "@mui/material";
import { useSelector } from "react-redux";
import { INVITATION_STATUS_IDS } from "../../constants/statuses";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import UserInfo from "../../components/common/avatar/UserInfo";
import { getInvitationExpirationText } from "../../utils/date.util";
import { TaskStatus } from "../../components/task/status";
import { CircularProgressLoading } from "../../components/common/loading/CircularProgressLoading";
import toast from "react-hot-toast";
import { Icon } from "@iconify/react";
import { useNavigate } from "react-router";
import {
  acceptInvitation,
  declineInvitation,
  fetchPendingInvitationsByMe,
} from "../../store/organization/invitation";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import {
  fetchLastOrganizationByMe,
  fetchOrganizationsByMe,
} from "../../store/organization";

const MyInvitationsPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { myInvitations: invitations, fetchLoading } = useSelector(
    (state) => state.invitation
  );
  const { ref, updateMaxHeight } = usePreventOverflow();

  const fetchOrgs = async () => {
    try {
      await dispatch(fetchLastOrganizationByMe()).unwrap();
      await dispatch(fetchOrganizationsByMe()).unwrap();
    } catch (err) {
      console.error("Failed to fetch organizations", err);
    }
  };

  const handleAccept = async (invite) => {
    try {
      await dispatch(acceptInvitation(invite.token)).unwrap();
      await fetchOrgs();
      toast.success("Chấp nhận lời mời thành công");
    } catch (e) {
      console.error(e);
      toast.error("Chấp nhận lời mời thất bại");
    }
  };

  const handleDecline = async (invite) => {
    try {
      await dispatch(declineInvitation(invite.token)).unwrap();
      toast.success("Từ chối lời mời thành công");
    } catch (e) {
      console.error(e);
      toast.error("Từ chối lời mời thất bại");
    }
  };

  useEffect(() => {
    dispatch(fetchPendingInvitationsByMe());
  }, [dispatch]);

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  if (fetchLoading || !invitations) return <CircularProgressLoading />;

  if (invitations.length === 0) {
    return (
      <>
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <IconButton onClick={() => navigate(-1)} sx={{ ml: 20 }}>
            <Icon icon="mdi:arrow-left" />
          </IconButton>
        </Box>
        <Box
          sx={{
            maxWidth: "1000px",
            maxHeight: "90vh",
            overflow: "auto",
            margin: "0 auto",
            mt: 5,
          }}
        >
          <Typography variant="h5" gutterBottom>
            Lời mời tham gia tổ chức
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Không có lời mời đang chờ nào.
          </Typography>
        </Box>
      </>
    );
  }

  return (
    <>
      <Box sx={{ display: "flex", alignItems: "center" }}>
        <IconButton onClick={() => navigate(-1)} sx={{ ml: 20 }}>
          <Icon icon="mdi:arrow-left" />
        </IconButton>
      </Box>
      <Box
        sx={{
          maxWidth: "1000px",
          maxHeight: "90vh",
          margin: "0 auto",
          mt: 5,
        }}
      >
        <Box
          display="flex"
          justifyContent="space-between"
          alignItems="center"
          sx={{ mb: 2 }}
        >
          <Typography variant="h5" gutterBottom>
            Lời mời tham gia tổ chức
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {invitations[0]?.email}
          </Typography>
        </Box>
        <Box ref={ref} sx={{ overflowY: "auto", mb: 2 }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Organization / Role</TableCell>
                <TableCell>Invited By</TableCell>
                <TableCell>Status / Date</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {invitations.map((invite) => {
                const inviter = invite.inviter;
                const org = invite.organization;
                const isPending =
                  invite.statusId === INVITATION_STATUS_IDS.pending;

                return (
                  <TableRow key={invite.id}>
                    <TableCell>
                      <Typography fontWeight={600}>{org.name}</Typography>
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ textTransform: "capitalize" }}
                      >
                        {invite.roleId}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Stack direction="row" alignItems="center" spacing={1}>
                        <UserInfo user={inviter} />
                      </Stack>
                    </TableCell>
                    <TableCell
                      sx={{
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "center",
                        gap: 1,
                      }}
                    >
                      <TaskStatus status={invite.status} />
                      <Typography variant="caption">
                        {getInvitationExpirationText(
                          invite.expirationTime,
                          invite.statusId
                        )}
                      </Typography>
                    </TableCell>
                    <TableCell align="right">
                      {isPending && (
                        <Stack
                          direction="row"
                          spacing={1}
                          justifyContent="flex-end"
                        >
                          <Button
                            variant="contained"
                            color="primary"
                            size="small"
                            onClick={() => handleAccept(invite)}
                          >
                            Accept
                          </Button>
                          <Button
                            variant="outlined"
                            color="error"
                            size="small"
                            onClick={() => handleDecline(invite)}
                          >
                            Decline
                          </Button>
                        </Stack>
                      )}
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </Box>
      </Box>
    </>
  );
};

export default MyInvitationsPage;
