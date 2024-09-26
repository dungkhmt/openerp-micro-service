import { Icon } from "@iconify/react";
import PropTypes from "prop-types";
import { Box, Button, Grid, Typography, styled } from "@mui/material";
import { FilterItem } from "./FilterItem";
import { hexToRGBA } from "../../utils/hex-to-rgba";

const ItemBox = styled(Box)(({ theme }) => ({
  flex: 1,
  display: "flex",
  alignItems: "center",
  flexDirection: "column",
  justifyContent: "space-between",
  gap: theme.spacing(2),
  padding: theme.spacing(1, 3),
  borderRadius: "6px",
  background: theme.palette.background.default,
}));

const FilterGroup = ({
  group,
  hasMore,
  deleteGroup,
  updateGroup,
  filterList,
  updateCondition,
  condition,
}) => {
  const shouldShowCondition = group.id !== 0 || hasMore;

  const handleAddNestedFilter = () => {
    const newId = group.items?.length ?? 0;
    const newGroup = {
      ...group,
      items: [
        ...group.items,
        {
          id: newId,
          field: "",
          operator: "",
          value: [],
        },
      ],
    };
    updateGroup(group.id, newGroup);
  };

  const handleDeleteItem = (id) => {
    const newItems = group.items
      .filter((item) => item.id !== id)
      .map((item, index) => ({ ...item, id: index }));
    const newGroup = { ...group, items: newItems };
    updateGroup(group.id, newGroup);
  };

  const handleUpdateItem = (id, newItem) => {
    const newItems = group.items.map((item) =>
      item.id === id ? newItem : item
    );
    const newGroup = { ...group, items: newItems };
    updateGroup(group.id, newGroup);
  };

  const handleUpdateCondition = (condition) => {
    updateGroup(group.id, { ...group, condition });
  };

  return (
    <Grid container spacing={1}>
      <Grid
        item
        xs={12}
        sm={4}
        md={2}
        lg={2}
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",

          "& .MuiTypography-root": {
            fontSize: "0.8rem",
            fontWeight: 550,
          },
        }}
      >
        {group.id === 0 && hasMore && <Typography>Where</Typography>}
        {group.id !== 0 && (
          <Typography
            sx={{
              textTransform: "uppercase",
              padding: (theme) => theme.spacing(1, 2),
              border: (theme) => `1px solid ${theme.palette.divider}`,
              borderRadius: "4px",
              display: "flex",
              alignItems: "center",
              gap: "4px",
              cursor: group.id === 1 ? "pointer" : "default",
              backgroundColor: (theme) =>
                group.id === 1 ? "transparent" : theme.palette.action.selected,

              ...(group.id === 1 && {
                "&:hover": {
                  backgroundColor: (theme) =>
                    hexToRGBA(theme.palette.primary.main, 0.1),
                },
              }),
            }}
            onClick={() =>
              group.id === 1 &&
              updateCondition(condition === "AND" ? "OR" : "AND")
            }
          >
            {condition}
            <Icon icon="mingcute:down-line" />
          </Typography>
        )}
      </Grid>
      <Grid
        item
        xs={12}
        sm={shouldShowCondition ? 8 : 12}
        md={shouldShowCondition ? 10 : 12}
        lg={shouldShowCondition ? 10 : 12}
      >
        <ItemBox>
          {group.items.map((item) => (
            <FilterItem
              key={item.id}
              item={item}
              hasMore={group.items.length > 1}
              deleteItem={handleDeleteItem}
              updateItem={handleUpdateItem}
              filterList={filterList}
              condition={group.condition}
              updateCondition={handleUpdateCondition}
            />
          ))}
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              width: "100%",
              "& .MuiButton-root": {
                color: (theme) => theme.palette.text.secondary,
                fontSize: "0.765rem",

                "& .MuiButton-startIcon": {
                  mr: 1,
                  "& svg": {
                    fontSize: "1rem",
                  },
                },

                "&:hover": {
                  color: (theme) => theme.palette.text.primary,
                },
              },
            }}
          >
            <Button
              variant="text"
              startIcon={<Icon icon="ic:outline-add" />}
              onClick={handleAddNestedFilter}
            >
              Thêm nested filter
            </Button>
            <Button
              variant="text"
              onClick={() => deleteGroup(group.id)}
              sx={{
                "&:hover": {
                  color: (theme) => `${theme.palette.error.main} !important`,
                  backgroundColor: (theme) =>
                    hexToRGBA(theme.palette.error.main, 0.1),
                },
              }}
            >
              Xóa group
            </Button>
          </Box>
        </ItemBox>
      </Grid>
    </Grid>
  );
};

FilterGroup.propTypes = {
  group: PropTypes.object.isRequired,
  hasMore: PropTypes.bool.isRequired,
  deleteGroup: PropTypes.func.isRequired,
  updateGroup: PropTypes.func.isRequired,
  filterList: PropTypes.array.isRequired,
  updateCondition: PropTypes.func.isRequired,
  condition: PropTypes.string.isRequired,
};

export { FilterGroup };
