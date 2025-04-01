import PropTypes from "prop-types";
import CustomChip from "../../mui/chip";
import { Tooltip } from "@mui/material";

const SkillChip = ({ skill, sx, ...props }) => {
  return (
    <Tooltip title={skill.description}>
      <span>
        <CustomChip
          sx={{
            borderColor: "darkgray",
            borderWidth: 1,
            borderStyle: "solid",
            backgroundColor: "transparent",
            fontWeight: 500,
            textTransform: "capitalize",
            ...sx,
          }}
          label={skill.name}
          {...props}
        />
      </span>
    </Tooltip>
  );
};

SkillChip.propTypes = {
  skill: PropTypes.object,
  sx: PropTypes.object,
};

export { SkillChip };
