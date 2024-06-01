import { Stack, Typography, styled } from "@mui/material";
import Balance from "../balance/Balance";

const StackStyled = styled(Stack)(({ theme }) => ({
  width: "fit-content",
}));

export default function GrowthCell({
  growth,
  evolution,
  backgroundColor,
  ...others
}) {
  return (
    <StackStyled
      spacing={0}
      justifyContent="center"
      alignItems="center"
      {...others}
    >
      <Typography
        sx={{ typography: { xs: "contentSBold", sm: "contentMRegular" } }}
      >
        {growth}
      </Typography>
      <Balance value={evolution.toFixed(0)} background={backgroundColor} />
    </StackStyled>
  );
}
