import PropTypes from "prop-types";
import CustomChip from "../../mui/chip";

const SkillChip = ({ skill, sx, ...props }) => {
  return (
    <CustomChip
      title={`${skill.description}`}
      sx={{
        borderColor: "darkgray",
        borderWidth: 1,
        borderStyle: "solid",
        backgroundColor: "transparent",
        fontWeight: 500,
        ...sx,
      }}
      label={skill.name}
      {...props}
    />
  );
};

SkillChip.propTypes = {
  skill: PropTypes.object,
  sx: PropTypes.object,
};

export { SkillChip };
