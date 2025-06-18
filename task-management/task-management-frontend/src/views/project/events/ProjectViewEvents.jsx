import { Box, Button, Typography, Grid, useTheme } from "@mui/material";
import { Icon } from "@iconify/react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { DialogNewEvent } from "./DialogNewEvent";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import { removeDiacritics } from "../../../utils/stringUtils.js";
import EventCard from "./EventCard";
import SearchField from "../../../components/mui/search/SearchField";
import { useDebounce } from "../../../hooks/useDebounce";
import SortButton from "../../../components/mui/sort/SortButton";

const ProjectViewEvents = () => {
  const theme = useTheme();
  const { events, fetchLoading } = useSelector((state) => state.events);
  const [createDialog, setCreateDialog] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [filteredEvents, setFilteredEvents] = useState(events);
  const debouncedSearchQuery = useDebounce(searchQuery, 1000);
  const [sortField, setSortField] = useState("createdStamp");
  const [sortDirection, setSortDirection] = useState("desc"); // Default to descending (equivalent to "latest")

  // Define sort fields for SortButton
  const sortFields = [
    { key: "createdStamp", label: "Ngày tạo" },
    { key: "startDate", label: "Ngày bắt đầu" },
    { key: "dueDate", label: "Ngày kết thúc" },
  ];

  const handleCreateEvent = () => {
    setCreateDialog(true);
  };

  const handleSort = (field, direction) => {
    setSortField(field);
    setSortDirection(direction);
  };

  useEffect(() => {
    let sortedEvents = [...events];

    // Sort events based on sortField and sortDirection
    if (sortField === "dueDate") {
      sortedEvents.sort((a, b) => {
        const dateA = new Date(a.dueDate);
        const dateB = new Date(b.dueDate);
        return sortDirection === "asc" ? dateA - dateB : dateB - dateA;
      });
    }
    if (sortField === "startDate") {
      sortedEvents.sort((a, b) => {
        const dateA = new Date(a.startDate);
        const dateB = new Date(b.startDate);
        return sortDirection === "asc" ? dateA - dateB : dateB - dateA;
      });
    }
    if (sortField === "createdStamp") {
      sortedEvents.sort((a, b) => {
        const dateA = new Date(a.createdStamp);
        const dateB = new Date(b.createdStamp);
        return sortDirection === "asc" ? dateA - dateB : dateB - dateA;
      });
    }

    // Apply search filter after sorting
    if (debouncedSearchQuery) {
      sortedEvents = sortedEvents.filter((event) =>
        removeDiacritics(event.name)
          .toLowerCase()
          .includes(debouncedSearchQuery.toLowerCase())
      );
    }

    setFilteredEvents(sortedEvents);
  }, [debouncedSearchQuery, events, sortField, sortDirection]);

  if (fetchLoading) return <CircularProgressLoading />;

  return (
    <>
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
            borderBottom: `1px solid ${theme.palette.grey[300]}`,
          }}
        >
          <Box sx={{ flexBasis: "33%", display: "flex", alignItems: "center" }}>
            <SearchField
              placeholder="Tìm kiếm sự kiện..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onClear={() => setSearchQuery("")}
              fullWidth
              iconSize={18}
              inputSx={{ height: 40, gap: 2 }}
            />
          </Box>

          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 5,
            }}
          >
            <SortButton
              sortFields={sortFields}
              onSort={handleSort}
              defaultSortField={sortField}
              defaultSortDirection={sortDirection}
            />
            <Button
              onClick={handleCreateEvent}
              color="primary"
              variant="contained"
              sx={{ textTransform: "none" }}
            >
              <Icon
                icon="fluent:add-16-filled"
                fontSize={20}
                sx={{ display: { xs: "block", sm: "none" } }}
              />
              <Box
                component="span"
                sx={{ display: { xs: "none", sm: "block" } }}
              >
                Thêm Sự Kiện
              </Box>
            </Button>
          </Box>
        </Box>
      </Box>

      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          pb: 3,
        }}
      >
        {filteredEvents?.length > 0 ? (
          <Grid container spacing={2} sx={{ mt: 0 }}>
            {filteredEvents.map((event) => (
              <Grid item xs={12} sm={6} md={4} key={event.id}>
                <EventCard event={event} />
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
              Không có sự kiện nào
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
              Hãy thêm sự kiện để bắt đầu quản lý chúng!
            </Typography>
            <Button
              onClick={handleCreateEvent}
              color="primary"
              variant="contained"
              sx={{ textTransform: "none", mb: 5 }}
              startIcon={<Icon icon="fluent:add-16-filled" />}
            >
              Thêm Sự Kiện
            </Button>
          </Box>
        )}

        <DialogNewEvent
          openDialog={createDialog}
          setOpenDialog={setCreateDialog}
        />
      </Box>
    </>
  );
};

export { ProjectViewEvents };