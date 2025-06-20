import { Box, Button, Divider, Stack, Typography } from "@mui/material";
import { Icon } from "@iconify/react";
import { useCallback, useEffect } from "react";
import { useDispatch } from "react-redux";
import { useNavigate, useParams } from "react-router";
import { useSelector } from "react-redux";
import { CircularProgressLoading } from "../../components/common/loading/CircularProgressLoading";
import {
  validateToken,
  acceptInvitation,
  declineInvitation,
} from "../../store/organization/invitation";
import toast from "react-hot-toast";
import NotFound from "../errors/NotFound";
import Unknown from "../errors/Unknown";
import Forbidden from "../errors/Forbidden";
import InvitationExpired from "../errors/invitation/InvitationExpired";
import InvitationAlreadyHandled from "../errors/invitation/InvitationAlreadyHandled";
import {
  fetchLastOrganizationByMe,
  fetchOrganizationsByMe,
} from "../../store/organization";

const InvitationDetail = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { invitationToken } = useParams();
  const {
    currentInvitation: invite,
    fetchLoading,
    errors,
  } = useSelector((state) => state.invitation);

  const validate = useCallback(async () => {
    try {
      await dispatch(validateToken(invitationToken)).unwrap();
    } catch (error) {
      console.error(error);
    }
  }, [dispatch, invitationToken]);

  const fetchOrgs = async () => {
    try {
      await dispatch(fetchLastOrganizationByMe()).unwrap();
      await dispatch(fetchOrganizationsByMe()).unwrap();
    } catch (err) {
      console.error("Failed to fetch organizations", err);
    }
  };

  const handleAccept = async () => {
    try {
      await dispatch(acceptInvitation(invitationToken)).unwrap();
      await fetchOrgs();
      toast.success("Chấp nhận lời mời thành công");
      navigate("/dashboard");
    } catch (e) {
      console.error(e);
      toast.error("Chấp nhận lời mời thất bại");
    }
  };

  const handleDecline = async () => {
    try {
      await dispatch(declineInvitation(invitationToken)).unwrap();
      toast.success("Từ chối lời mời thành công");
      navigate("/dashboard");
    } catch (e) {
      console.error(e);
      toast.error("Từ chối lời mời thất bại");
    }
  };

  const handleIgnore = () => {
    navigate("/");
  };

  useEffect(() => {
    validate();
  }, [validate]);

  if (errors.length > 0) {
    const error = errors[0];
    const status = error?.status || error?.response?.status || 500;
    switch (status) {
      case 403: // ACCESS_DENIED
        return <Forbidden />;

      case 404: // INVALID_INVITATION_TOKEN
        return <NotFound />;

      case 409: // INVITATION_ALREADY_HANDLED
        return <InvitationAlreadyHandled />;

      case 410: // INVITATION_EXPIRED
        return <InvitationExpired />;

      default:
        return <Unknown />;
    }
  }

  if (fetchLoading || !invite) return <CircularProgressLoading />;

  return (
    <Box
      maxWidth={700}
      margin="auto"
      mt={10}
      px={4}
      py={6}
      borderRadius={4}
      bgcolor="background.default"
      boxShadow={3}
    >
      <Typography variant="h6" gutterBottom>
        Bạn đã được mời tham gia tổ chức{" "}
        <strong
          style={{
            display: "inline-block",
            maxWidth: "100%",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
            verticalAlign: "bottom",
          }}
        >
          {invite.organization.name}
        </strong>
      </Typography>

      <Typography variant="body2" gutterBottom>
        Tổ chức là nơi bạn có thể tạo dự án, cộng tác với người khác và quản lý
        công việc hiệu quả hơn.
      </Typography>

      <Divider sx={{ my: 3 }} />

      <Stack spacing={2} alignItems="center">
        {[
          {
            label: "Chấp nhận",
            onClick: handleAccept,
            icon: "icon-park-outline:check-one",
            color: "primary",
          },
          {
            label: "Từ chối",
            onClick: handleDecline,
            icon: "lets-icons:cancel",
            color: "error",
          },
          {
            label: "Bỏ qua",
            onClick: handleIgnore,
            icon: "material-symbols:cancel-outline",
            color: "secondary",
          },
        ].map(({ label, onClick, icon, color }) => (
          <Button
            key={label}
            onClick={onClick}
            variant="text"
            startIcon={<Icon icon={icon} />}
            fullWidth={false}
            sx={{
              width: "100%",
              maxWidth: 250,
              textTransform: "none",
              color: `${color}.background`,
              backgroundColor: `${color}.main`,
              "&:hover": { backgroundColor: `${color}.dark` },
            }}
          >
            {label}
          </Button>
        ))}
      </Stack>
    </Box>
  );
};

export default InvitationDetail;
