import { Box, Typography } from "@mui/material";
import { Helmet } from "react-helmet";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import { useEffect } from "react";

const Unknown = () => {
  const { ref, updateMaxHeight } = usePreventOverflow();

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  return (
    <>
      <Helmet>
        <title>Opps Occur Error | Task management</title>
      </Helmet>
      <Box
        ref={ref}
        sx={{
          mt: 6,
          display: "flex",
          alignItems: "center",
          flexDirection: "column",
          overflowY: "auto",
        }}
      >
        <Typography variant="h4" color="error.dark">
          Có lỗi xảy ra
        </Typography>
        <Typography variant="body1" color="error.light">
          Có lẽ đã xảy ra lỗi khi tải dữ liệu, chúng tôi đang cố gắng để khắc
          phục. Vui lòng thử lại sau.
        </Typography>
      </Box>
    </>
  );
};

export default Unknown;
