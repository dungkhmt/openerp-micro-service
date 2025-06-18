import {
  Box,
  Typography,
  IconButton,
  MenuItem,
  Select,
  Link,
  Tooltip,
  Button,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { Icon } from "@iconify/react";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useMemo, useState } from "react";
import {
  removeUserFromOrganization,
  fetchUsersByOrganizationId,
} from "../../../store/organization/index";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import CountBadge from "../../../components/common/badge/CountBadge";
import SearchField from "../../../components/mui/search/SearchField";
import { useDebounce } from "../../../hooks/useDebounce";
import { removeDiacritics } from "../../../utils/stringUtils.js";
import { ROLE_IDS, ROLE_LIST } from "../../../constants/roles";
import InviteUserDialog from "../../../views/organization/InviteUserDialog";
import toast from "react-hot-toast";
import OrgInvitationsTable from "../../../views/organization/OrgInvitationsTable";
import {
  fetchPendingInvitationsByOrgId,
  inviteUsers,
} from "../../../store/organization/invitation";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";

const OrganizationMembersView = () => {
  const dispatch = useDispatch();
  const {
    organizations,
    currentOrganization,
    organizationMembers,
    fetchLoading,
  } = useSelector((state) => state.organization);
  const { user } = useSelector((state) => state.myProfile);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [targetMember, setTargetMember] = useState(null);
  const [searchValue, setSearchValue] = useState("");
  const debouncedSearchQuery = useDebounce(searchValue, 1000);
  const [filteredMembers, setFilteredMembers] = useState(organizationMembers);
  const [openInviteDialog, setOpenInviteDialog] = useState(false);
  const { ref, updateHeight } = usePreventOverflow();

  const myRole = useMemo(() => {
    if (!currentOrganization) return null;
    const org = organizations.find((org) => org.id === currentOrganization.id);
    return org?.myRole || null;
  }, [organizations, currentOrganization]);

  useEffect(() => {
    if (currentOrganization) {
      dispatch(fetchUsersByOrganizationId(currentOrganization.id));
      dispatch(fetchPendingInvitationsByOrgId(currentOrganization.id));
    }
  }, [currentOrganization, dispatch]);

  useEffect(() => {
    if (!debouncedSearchQuery) {
      setFilteredMembers(organizationMembers);
    } else {
      const filtered = organizationMembers.filter(({ user }) => {
        const fullName = `${user.firstName ?? ""} ${user.lastName ?? ""}`
          .trim()
          .toLowerCase();
        const email = user.email?.toLowerCase() || "";
        return (
          removeDiacritics(fullName).includes(
            debouncedSearchQuery.toLowerCase()
          ) || email.includes(debouncedSearchQuery.toLowerCase())
        );
      });
      setFilteredMembers(filtered);
    }
  }, [debouncedSearchQuery, organizationMembers]);

  const handleDelete = async () => {
    if (targetMember) {
      await dispatch(removeUserFromOrganization(targetMember.id));
      setTargetMember(null);
      setConfirmOpen(false);
    }
  };

  const handleInvite = async (invites) => {
    try {
      await dispatch(
        inviteUsers({
          invitees: Object.values(invites),
          organizationId: currentOrganization.id,
        })
      ).unwrap();
      toast.success("Gửi lời mời thành công!");
    } catch (error) {
      toast.error("Lỗi khi gửi lời mời!");
      console.error(error);
    } finally {
      setOpenInviteDialog(false);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    // TODO
    // await dispatch(updateOrganizationRole({ userId, role: newRole }));
  };

  useEffect(() => {
    updateHeight(10);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window.innerHeight]);

  const columns = [
    {
      flex: 0.3,
      minWidth: 150,
      field: "id",
      headerName: "User",
      renderCell: ({ row }) => {
        const { firstName, lastName, id } = row.user;
        const fullName =
          firstName || lastName
            ? `${firstName ?? ""} ${lastName ?? ""}`
            : " - ";
        return (
          <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
            <UserAvatar user={row.user} />
            <Box
              sx={{
                display: "flex",
                alignItems: "flex-start",
                flexDirection: "column",
              }}
            >
              <Typography
                sx={{
                  fontWeight: 650,
                  fontSize: "0.9rem",
                  color: (theme) => theme.palette.text.secondary,
                  "&:hover": {
                    color: (theme) => theme.palette.text.primary,
                    cursor: "pointer",
                  },
                }}
              >
                {fullName}
              </Typography>
              <Typography noWrap variant="caption">
                {`@${id}`}
              </Typography>
            </Box>
          </Box>
        );
      },
      display: "flex",
    },
    {
      flex: 0.3,
      minWidth: 150,
      field: "email",
      headerName: "Email",
      renderCell: ({ row }) => {
        return (
          <Tooltip title={`Gửi mail tới: ${row.user.email}`}>
            <Typography
              noWrap
              variant="body2"
              component={Link}
              href={`mailto:${row.user.email}`}
              sx={{ textDecoration: "none", color: "text.secondary" }}
            >
              {row.user?.email}
            </Typography>
          </Tooltip>
        );
      },
      display: "flex",
    },
    {
      field: "role",
      headerName: "Role",
      flex: 0.2,
      renderCell: ({ row }) => {
        const editable = myRole === ROLE_IDS.admin || user.id === row.id;
        return editable ? (
          <Select
            value={row.roleId}
            onChange={(e) => handleRoleChange(row.id, e.target.value)}
            variant="standard"
            disableUnderline
            sx={{ textTransform: "capitalize" }}
          >
            {ROLE_LIST.map((r) => (
              <MenuItem key={r} value={r} sx={{ textTransform: "capitalize" }}>
                {r}
              </MenuItem>
            ))}
          </Select>
        ) : (
          <Typography textTransform="capitalize">{row.roleId}</Typography>
        );
      },
    },
    {
      field: "actions",
      headerName: "Actions",
      flex: 0.1,
      sortable: false,
      renderCell: ({ row }) => {
        const canDelete = myRole === ROLE_IDS.admin || user.id === row.id;
        return canDelete ? (
          <>
            <IconButton
              onClick={() => {
                setTargetMember(row);
                setConfirmOpen(true);
              }}
            >
              <Icon icon="mdi:delete-outline" />
            </IconButton>
          </>
        ) : null;
      },
    },
  ];

  return (
    <Box sx={{ pr: 3, overflowY: "auto" }} ref={ref}>
      <OrgInvitationsTable />

      <Box
        sx={{
          mb: 2,
          mt: 5,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Typography variant="h6">Thành viên</Typography>
          <CountBadge count={organizationMembers.length} />
        </Box>

        <Box sx={{ display: "flex", gap: 2 }}>
          <SearchField
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onClear={() => setSearchValue("")}
          />
          <Button
            variant="contained"
            startIcon={<Icon icon="mi:add" />}
            sx={{ textTransform: "none" }}
            onClick={() => setOpenInviteDialog(true)}
          >
            Thành viên
          </Button>
        </Box>
      </Box>

      {/* <Box ref={ref} sx={{ height: "10%" }}> */}
        <DataGrid
          rows={filteredMembers.map((member) => ({
            ...member,
            id: member.user.id,
          }))}
          columns={columns}
          getRowId={(row) => row.id}
          disableRowSelectionOnClick
          loading={fetchLoading && !currentOrganization}
        />
      {/* </Box> */}

      <InviteUserDialog
        open={openInviteDialog}
        onClose={() => setOpenInviteDialog(false)}
        onSend={handleInvite}
      />

      <ConfirmationDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Remove Member"
        content={`Are you sure you want to remove ${
          targetMember?.user?.firstName || ""
        } ${
          targetMember?.user?.lastName || ""
        } from the organization? This action cannot be undone.`}
      />
    </Box>
  );
};

export default OrganizationMembersView;
