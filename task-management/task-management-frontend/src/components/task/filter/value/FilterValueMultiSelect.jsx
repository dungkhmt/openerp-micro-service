import { Icon } from "@iconify/react";
import { Typography } from "@mui/material";
import { useState } from "react";
import PropTypes from "prop-types";
import { FilterWrapperBox } from "../FilterWrapperBox";
import { MenuMultiSelect } from "../menu/MenuMultiSelect";

const FilterValueMultiSelect = (props) => {
  const { filter, item, onAddItem, onDeleteItem, onSelectAll, onDeselectAll } =
    props;
  const [valueAnchor, setValueAnchor] = useState(null);

  const handleMenuClose = () => {
    setValueAnchor(null);
  };

  return (
    <>
      <FilterWrapperBox
        sx={{
          flex: 4,
          display: "flex",
          maxWidth: "300px",
          flexWrap: "wrap",
          gap: 1,
        }}
        onClick={(e) => setValueAnchor(e.currentTarget)}
      >
        {item?.value?.length <= 0 ? (
          <Typography noWrap variant="subtitle2" component="span">
            Chọn
          </Typography>
        ) : (
          <>
            {filter?.renderValueItem ? (
              item.value.map((v) =>
                filter.renderValueItem(filter.options.find((o) => o.id === v))
              )
            ) : (
              <Typography
                noWrap
                sx={{
                  maxWidth: "200px",
                }}
                variant="subtitle2"
                component="span"
              >
                {item.value.join(", ")}
              </Typography>
            )}
          </>
        )}
        <Icon
          icon={
            valueAnchor ? "akar-icons:chevron-up" : "akar-icons:chevron-down"
          }
          style={{ marginLeft: "4px" }}
        />
      </FilterWrapperBox>
      {valueAnchor && (
        <MenuMultiSelect
          anchorEl={valueAnchor}
          onClose={handleMenuClose}
          options={filter.options}
          onAddItem={onAddItem}
          onDeleteItem={onDeleteItem}
          selectedItems={item.value}
          renderItem={filter.renderValueItem}
          onSelectAll={onSelectAll}
          onDeselectAll={onDeselectAll}
        />
      )}
    </>
  );
};

FilterValueMultiSelect.propTypes = {
  filter: PropTypes.object.isRequired,
  item: PropTypes.object,
  onAddItem: PropTypes.func.isRequired,
  onDeleteItem: PropTypes.func.isRequired,
  onSelectAll: PropTypes.func.isRequired,
  onDeselectAll: PropTypes.func.isRequired,
};

export { FilterValueMultiSelect };
