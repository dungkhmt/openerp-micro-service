import {
  Box,
  Button,
  Card,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import MemberRow from "./MemberRow";
import AddMemberPopover from "./AddMemberPopover";
import { Icon } from "@iconify/react";
import { removeDiacritics } from "../../../utils/stringUtils.js";
import { useEffect, useState } from "react";
import SortButton from "../../../components/mui/sort/SortButton";
import ExpandableSearch from "../../../components/mui/search/ExpandableSearch";
import { useSelector } from "react-redux";
import CountBadge from "../../../components/common/badge/CountBadge";

const TeamMembersTable = () => {
  const { groupMembers: members } = useSelector((state) => state.userGroup);
  const [anchorEl, setAnchorEl] = useState(null);
  const [filteredMembers, setFilteredMembers] = useState(members);
  const [sortField, setSortField] = useState(null);
  const [sortDirection, setSortDirection] = useState("asc");

  const sortFields = [
    { key: "fullName", label: "Họ Tên" },
    { key: "totalTasks", label: "Tổng số nhiệm vụ" },
    { key: "completedTasks", label: "Hoàn thành" },
    { key: "inProgressTasks", label: "Đang tiến hành" },
    { key: "uncompletedTasks", label: "Chưa hoàn thành" },
  ];

  const handleSort = (field, direction) => {
    setSortField(field);
    setSortDirection(direction);
  };

  const handleSearch = (searchTerm) => {
    if (!searchTerm) {
      setFilteredMembers(members);
    } else {
      const filtered = members.filter(({ user }) => {
        const fullName = `${user.firstName ?? ""} ${user.lastName ?? ""}`
          .trim()
          .toLowerCase();
        const email = user.email?.toLowerCase() || "";
        return (
          removeDiacritics(fullName).includes(searchTerm.toLowerCase()) ||
          email.includes(searchTerm.toLowerCase())
        );
      });
      setFilteredMembers(filtered);
    }
  };

  useEffect(() => {
    if (sortField) {
      const updatedMembers = [...filteredMembers].sort((a, b) => {
        let valueA, valueB;
        if (sortField === "fullName") {
          valueA = `${a.user.firstName || ""} ${a.user.lastName || ""}`.trim();
          valueB = `${b.user.firstName || ""} ${b.user.lastName || ""}`.trim();
        } else {
          valueA = a[sortField] || 0;
          valueB = b[sortField] || 0;
        }
        if (sortField === "fullName") {
          return sortDirection === "asc"
            ? valueA.localeCompare(valueB)
            : valueB.localeCompare(valueA);
        } else {
          return sortDirection === "asc" ? valueA - valueB : valueB - valueA;
        }
      });
      setFilteredMembers(updatedMembers);
    }
  }, [sortField, sortDirection, filteredMembers]);

  return (
    <Card
      sx={{
        my: 5,
        borderRadius: 3,
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
      }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          py: 2,
          px: 4,
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1, my: 2 }}>
          <Typography variant="h6">Thành viên</Typography>
          <CountBadge count={members.length} />
        </Box>
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <ExpandableSearch
            placeholder="Tìm kiếm thành viên..."
            onSearchChange={handleSearch}
            width="250px"
          />
          <SortButton sortFields={sortFields} onSort={handleSort} />
          <Button
            variant="contained"
            startIcon={<Icon icon="mi:add" />}
            onClick={(e) => setAnchorEl(e.currentTarget)}
            sx={{ textTransform: "none" }}
          >
            Thành viên
          </Button>
        </Box>
      </Box>

      <Table>
        <TableHead sx={{ borderTop: "1px solid", borderColor: "grey.300" }}>
          <TableRow>
            <TableCell sx={{ fontWeight: "bold" }}>
              {sortFields[0]?.label}
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              {sortFields[1]?.label}
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              {sortFields[2]?.label}
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              {sortFields[3]?.label}
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              {sortFields[4]?.label}
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Hành động
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {filteredMembers.length > 0 ? (
            filteredMembers.map((member) => (
              <MemberRow key={member.userId} member={member} />
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={6} align="center">
                <Typography variant="body2" color="text.secondary">
                  No members found.
                </Typography>
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <AddMemberPopover anchorEl={anchorEl} setAnchorEl={setAnchorEl} />
    </Card>
  );
};

export default TeamMembersTable;
