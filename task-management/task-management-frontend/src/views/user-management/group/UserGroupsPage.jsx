import { Box, Button, Typography, Grid, Divider } from "@mui/material";
import { Icon } from "@iconify/react";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import { removeDiacritics } from "../../../utils/stringUtils.js.js";
import SearchField from "../../../components/mui/search/SearchField";
import {
  createGroup,
  fetchGroupsByMe,
} from "../../../store/user-management/group";
import GroupDialog from "../../../components/groups/GroupDialog";
import toast from "react-hot-toast";
import { useDebounce } from "../../../hooks/useDebounce";
import GroupCard from "./GroupCard";
import { useNavigate } from "react-router";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";

const UserGroupsPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { groups, fetchLoading } = useSelector((state) => state.userGroup);
  const { currentOrganization } = useSelector((state) => state.organization);
  const [createDialog, setCreateDialog] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [filteredGroups, setFilteredGroups] = useState(groups);
  const debouncedSearchQuery = useDebounce(searchQuery, 1000);
  const { ref, updateMaxHeight } = usePreventOverflow();

  const handleCreateGroup = () => setCreateDialog(true);

  const onSubmit = async (data) => {
    try {
      const res = await dispatch(
        createGroup({
          ...data,
          organizationId: currentOrganization.id,
        })
      ).unwrap();
      toast.success("Thêm nhóm thành công!");
      navigate(`/user-management/groups/${res.id}`);
    } catch (error) {
      toast.error("Lỗi khi thêm nhóm!");
      console.error(error);
    }
  };

  useEffect(() => {
    dispatch(fetchGroupsByMe());
  }, [dispatch]);

  useEffect(() => {
    let filtered = [...groups];
    if (debouncedSearchQuery) {
      filtered = filtered.filter((group) =>
        removeDiacritics(group.name)
          .toLowerCase()
          .includes(debouncedSearchQuery.toLowerCase())
      );
    }
    setFilteredGroups(filtered);
  }, [debouncedSearchQuery, groups]);

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  if (fetchLoading) return <CircularProgressLoading />;

  return (
    <Box sx={{ p: 2 }}>
      <Box
        sx={{
          position: "sticky",
          top: 0,
          backgroundColor: "background.default",
          zIndex: 10,
          p: 0,
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            height: 50,
          }}
        >
          <Box sx={{ flexBasis: "33%", display: "flex", alignItems: "center" }}>
            <SearchField
              placeholder="Tìm kiếm nhóm..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onClear={() => setSearchQuery("")}
              fullWidth
              iconSize={18}
              inputSx={{ height: 40, gap: 2 }}
            />
          </Box>

          <Box sx={{ display: "flex", alignItems: "center", gap: 5 }}>
            <Button
              onClick={handleCreateGroup}
              color="primary"
              variant="contained"
              sx={{ textTransform: "none", gap: 1 }}
            >
              <Icon
                icon="fluent:add-12-filled"
                fontSize={16}
                sx={{ display: { xs: "block", sm: "none" } }}
              />
              <Box
                component="span"
                sx={{ display: { xs: "none", sm: "block" } }}
              >
                Nhóm
              </Box>
            </Button>
          </Box>
        </Box>
      </Box>

      <Divider sx={{ my: 1 }} />

      <Box
        ref={ref}
        sx={{
          overflowY: "auto",
          pb: 3,
        }}
      >
        {filteredGroups?.length > 0 ? (
          <Grid container spacing={2} sx={{ mt: 0 }}>
            {filteredGroups.map((group) => (
              <Grid item xs={12} sm={6} md={4} key={group.id}>
                <GroupCard group={group} />
              </Grid>
            ))}
          </Grid>
        ) : (
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              textAlign: "center",
              width: "100%",
            }}
          >
            <Typography variant="h6" sx={{ pt: 5, mb: 2 }}>
              Không có nhóm nào
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
              Hãy thêm nhóm để bắt đầu quản lý!
            </Typography>
            <Button
              onClick={handleCreateGroup}
              color="primary"
              variant="contained"
              sx={{ textTransform: "none", mb: 5 }}
              startIcon={<Icon icon="fluent:add-12-filled" />}
            >
              Nhóm
            </Button>
          </Box>
        )}

        <GroupDialog
          openDialog={createDialog}
          onClose={() => setCreateDialog(false)}
          onSubmit={onSubmit}
        />
      </Box>
    </Box>
  );
};

export default UserGroupsPage;
