import { useState } from "react";
import {
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Select,
  MenuItem,
  IconButton,
  Menu,
  Typography,
  Box,
  Stack,
} from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import { Icon } from "@iconify/react";
import { TaskStatus } from "../../components/task/status";
import { ROLE_LIST } from "../../constants/roles";
import { INVITATION_STATUS_IDS } from "../../constants/statuses";
import { format } from "date-fns";
import UserInfo from "../../components/common/avatar/UserInfo";
import CountBadge from "../../components/common/badge/CountBadge";
import { CircularProgressLoading } from "../../components/common/loading/CircularProgressLoading";
import { getInvitationExpirationText } from "../../utils/date.util";

const OrgInvitationsTable = () => {
  const { orgInvitations: invitations, fetchLoading } = useSelector(
    (state) => state.invitation
  );
  const [anchorEl, setAnchorEl] = useState(null);
  const [menuRow, setMenuRow] = useState(null);

  const openMenu = (event, row) => {
    setAnchorEl(event.currentTarget);
    setMenuRow(row);
  };

  const closeMenu = () => {
    setAnchorEl(null);
    setMenuRow(null);
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(menuRow.inviteLink || "");
    closeMenu();
  };

  const handleRemove = () => {
    // dispatch(removeInvitation(menuRow.id));
    closeMenu();
  };

  const handleResend = () => {
    // dispatch(resendInvitation(menuRow.id));
    closeMenu();
  };

  const handleRoleChange = (id, role) => {
    // TODO
  };

  if (fetchLoading || !invitations) return <CircularProgressLoading />;

  return (
    <>
      {/* {invitations?.length > 0 && ( */}
        <Box sx={{ mt: 5 }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <Typography variant="h6">Lời mời tham gia</Typography>
            <CountBadge count={invitations.length} />
          </Box>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Email</TableCell>
                <TableCell>Role</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Invited By</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {invitations?.map((row) => {
                const inviter = row.inviter;
                const invitedAt = row.createdStamp
                  ? format(new Date(row.createdStamp), "dd MMM, yyyy")
                  : "-";

                return (
                  <TableRow key={row.id}>
                    <TableCell>
                      <Typography varient="body1">{row.email}</Typography>
                      <Typography variant="caption">
                        Mời ngày {invitedAt}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Select
                        size="small"
                        value={row.roleId}
                        variant="standard"
                        disableUnderline
                        onChange={(e) =>
                          handleRoleChange(row.id, e.target.value)
                        }
                        sx={{ textTransform: "capitalize" }}
                      >
                        {ROLE_LIST.map((role) => (
                          <MenuItem
                            key={role}
                            value={role}
                            sx={{ textTransform: "capitalize" }}
                          >
                            {role}
                          </MenuItem>
                        ))}
                      </Select>
                    </TableCell>
                    <TableCell
                      sx={{
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "center",
                        gap: 1,
                      }}
                    >
                      <TaskStatus status={row.status} />
                      <Typography variant="caption">
                        {getInvitationExpirationText(
                          row.expirationTime,
                          row.statusId
                        )}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Stack direction="row" alignItems="center" spacing={1}>
                        <UserInfo user={inviter} />
                      </Stack>
                    </TableCell>
                    <TableCell align="right">
                      <IconButton onClick={(e) => openMenu(e, row)}>
                        <Icon icon="mdi:dots-vertical" width={20} />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>

          {/* Action Menu */}
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={closeMenu}
          >
            <MenuItem onClick={handleCopy} sx={{ gap: 2 }}>
              <Icon icon="flowbite:link-outline" width={18} />
              Copy Link
            </MenuItem>
            <MenuItem onClick={handleRemove} sx={{ gap: 2 }}>
              <Icon icon="material-symbols:delete-outline" width={18} />
              Remove Invite
            </MenuItem>
            {(menuRow?.statusId === INVITATION_STATUS_IDS.expired ||
              menuRow?.statusId === INVITATION_STATUS_IDS.declined) && (
              <MenuItem onClick={handleResend} sx={{ gap: 2 }}>
                <Icon icon="mdi:email-resend-outline" width={18} />
                Resend
              </MenuItem>
            )}
          </Menu>
        </Box>
      {/* )} */}
    </>
  );
};

export default OrgInvitationsTable;
