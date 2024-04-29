import { Icon } from "@iconify/react";
import { Box, Grid, IconButton, Typography, styled } from "@mui/material";
import PropTypes from "prop-types";
import { hexToRGBA } from "../../utils/hex-to-rgba";
import { MenuSingleSelect } from "../../menu/MenuSingleSelect";
import { useState } from "react";
import { FilterWrapperBox } from "./FilterWrapperBox";
import { FilterValueText } from "./value/FilterValueText";
import { FilterValueMultiSelect } from "./value/FilterValueMultiSelect";
import { FilterValueDatetime } from "./value/FilterValueDatetime";

const Item = styled(Box)(({ theme }) => ({
  flex: 1,
  display: "flex",
  alignItems: "center",
  justifyContent: "space-between",
  gap: theme.spacing(2),
}));

const FilterItem = ({
  item,
  hasMore,
  deleteItem,
  updateItem,
  condition,
  updateCondition,
  filterList,
}) => {
  const shouldShowCondition = item.id !== 0 || hasMore;

  const [fieldAnchor, setFieldAnchor] = useState(null);
  const [operatorAnchor, setOperatorAnchor] = useState(null);

  const handleFieldMenuClose = () => {
    setFieldAnchor(null);
  };

  const handleOperatorMenuClose = () => {
    setOperatorAnchor(null);
  };

  const handleChooseField = (field) => {
    updateItem(item.id, {
      ...item,
      field: {
        id: field.id,
        label: field.label,
      },
      operator: field.defaultOperator,
      value: [],
    });
    setFieldAnchor(null);
  };

  const handleChooseOperator = (operator) => {
    updateItem(item.id, {
      ...item,
      operator,
      value: [],
    });
    setOperatorAnchor(null);
  };

  const handleTextValueChange = (value) => {
    updateItem(item.id, {
      ...item,
      value,
    });
  };

  const handleAddMultiValue = (value) => {
    updateItem(item.id, {
      ...item,
      value: [...item.value, value.id],
    });
  };

  const handleDeleteMultiValue = (value) => {
    updateItem(item.id, {
      ...item,
      value: item.value.filter((v) => v !== value.id),
    });
  };

  const handleSelectAllMultiValue = () => {
    updateItem(item.id, {
      ...item,
      value: filter.options.map((o) => o.id),
    });
  };

  const handleDeselectAllMultiValue = () => {
    updateItem(item.id, {
      ...item,
      value: [],
    });
  };

  const filter = filterList.find((filter) => filter.id === item.field?.id);

  const renderSelectValue = () => {
    switch (filter?.type) {
      case "text":
      case "number":
        return (
          <FilterValueText
            filter={filter}
            item={item}
            onValueChange={handleTextValueChange}
          />
        );
      case "multiselect":
        return (
          <FilterValueMultiSelect
            filter={filter}
            item={item}
            onAddItem={handleAddMultiValue}
            onDeleteItem={handleDeleteMultiValue}
            onSelectAll={handleSelectAllMultiValue}
            onDeselectAll={handleDeselectAllMultiValue}
          />
        );
      case "datetime":
        return (
          <FilterValueDatetime
            item={item}
            isFirstOfDay={false}
            onChange={handleTextValueChange}
          />
        );
      default:
        return null;
    }
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
        {item.id === 0 && hasMore && <Typography>Where</Typography>}
        {item.id !== 0 && (
          <Typography
            sx={{
              textTransform: "uppercase",
              padding: (theme) => theme.spacing(1, 2),
              border: (theme) => `1px solid ${theme.palette.divider}`,
              borderRadius: "4px",
              display: "flex",
              alignItems: "center",
              gap: "4px",
              cursor: item.id === 1 ? "pointer" : "default",
              backgroundColor: (theme) =>
                item.id === 1
                  ? theme.palette.background.paper
                  : theme.palette.action.selected,

              ...(item.id === 1 && {
                "&:hover": {
                  backgroundColor: (theme) =>
                    hexToRGBA(theme.palette.primary.main, 0.1),
                },
              }),
            }}
            onClick={() =>
              item.id === 1 &&
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
        <Item>
          <Box sx={{ display: "flex", flex: 1, gap: 1 }}>
            {item.field && filter ? (
              <>
                {/* field */}
                <FilterWrapperBox
                  sx={{ flex: item.operator?.isUnary ? 1 : 2 }}
                  onClick={(e) => setFieldAnchor(e.currentTarget)}
                >
                  <Typography noWrap variant="subtitle2" component="span">
                    {item.field.label}
                  </Typography>
                  <Icon
                    icon={
                      fieldAnchor
                        ? "akar-icons:chevron-up"
                        : "akar-icons:chevron-down"
                    }
                    style={{ marginLeft: "4px" }}
                  />
                </FilterWrapperBox>
                {/* operator */}
                <FilterWrapperBox
                  sx={{ flex: item.operator?.isUnary ? 2 : 1 }}
                  onClick={(e) => setOperatorAnchor(e.currentTarget)}
                >
                  <Typography noWrap variant="subtitle2" component="span">
                    {item.operator.label}
                  </Typography>
                  <Icon
                    icon={
                      operatorAnchor
                        ? "akar-icons:chevron-up"
                        : "akar-icons:chevron-down"
                    }
                    style={{ marginLeft: "4px" }}
                  />
                </FilterWrapperBox>
                {/* value */}
                {item.operator.isUnary ? null : renderSelectValue()}
              </>
            ) : (
              <FilterWrapperBox
                onClick={(e) => setFieldAnchor(e.currentTarget)}
              >
                <Typography variant="subtitle2" component="span" sx={{ mr: 4 }}>
                  Chọn một trường
                </Typography>
                <Icon
                  icon={
                    fieldAnchor
                      ? "akar-icons:chevron-up"
                      : "akar-icons:chevron-down"
                  }
                  style={{ marginLeft: "4px" }}
                />
              </FilterWrapperBox>
            )}
            {fieldAnchor && (
              <MenuSingleSelect
                anchorEl={fieldAnchor}
                options={filterList}
                onClose={handleFieldMenuClose}
                onItemClick={handleChooseField}
                selectedItem={item.field}
              />
            )}
            {operatorAnchor && filter && (
              <MenuSingleSelect
                anchorEl={operatorAnchor}
                options={filter.operators}
                onClose={handleOperatorMenuClose}
                onItemClick={handleChooseOperator}
                selectedItem={item.operator}
                searchable={false}
              />
            )}
          </Box>
          <Box>
            <IconButton
              onClick={() => deleteItem(item.id)}
              sx={{
                borderRadius: "5px",
                fontSize: "1rem",
                padding: "6px",
                "&:hover": {
                  backgroundColor: (theme) =>
                    hexToRGBA(theme.palette.error.main, 0.1),
                  color: (theme) => theme.palette.error.main,
                },
              }}
            >
              <Icon icon="mi:delete" />
            </IconButton>
          </Box>
        </Item>
      </Grid>
    </Grid>
  );
};

FilterItem.propTypes = {
  item: PropTypes.object.isRequired,
  hasMore: PropTypes.bool,
  deleteItem: PropTypes.func,
  updateItem: PropTypes.func,
  filterList: PropTypes.arrayOf(PropTypes.object),
  condition: PropTypes.string,
  updateCondition: PropTypes.func,
};

export { FilterItem };
