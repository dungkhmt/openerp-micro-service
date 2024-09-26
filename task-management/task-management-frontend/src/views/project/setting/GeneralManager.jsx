import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Divider,
  IconButton,
  TextField,
  Typography,
  styled,
} from "@mui/material";
import { useState } from "react";
import { useSelector } from "react-redux";

const GeneralWrapper = styled(Box)(({ theme }) => ({
  display: "flex",
  flexDirection: "column",
  gap: theme.spacing(2),
  padding: theme.spacing(1),
  marginLeft: theme.spacing(2),
}));

const GeneralManager = () => {
  const { project } = useSelector((state) => state.project);
  const [isCopied, setIsCopied] = useState(false);
  const [name, setName] = useState(project.name);

  const handleCopyCode = () => {
    if (isCopied) return;
    navigator.clipboard.writeText(project.code);
    setIsCopied(true);
    setTimeout(() => {
      setIsCopied(false);
    }, 2000);
  };

  return (
    <GeneralWrapper>
      <Typography variant="h5">Chung</Typography>
      <Divider />
      <Box>
        <Box>
          <Typography variant="subtitle2">Mã dự án</Typography>
          <Box mt={1} display="flex" alignItems="center" gap={2}>
            <TextField
              variant="outlined"
              value={project.code}
              disabled
              size="small"
              sx={{
                "& input": {
                  p: (theme) => theme.spacing(1, 1.5),
                },
                borderColor: (theme) => theme.palette.grey[200],
              }}
            />
            {/* Copy button */}
            <IconButton
              title="copy"
              sx={{
                backgroundColor: (theme) =>
                  `${theme.palette.grey[300]} !important`,
                borderRadius: "5px",
                "&:hover": {
                  backgroundColor: (theme) =>
                    `${theme.palette.grey[400]} !important`,
                },
                "& svg": {
                  color: isCopied ? "green" : "inherit",
                },
                p: 1.5,
              }}
              onClick={handleCopyCode}
            >
              <Icon
                icon={isCopied ? "akar-icons:check" : "ci:copy"}
                fontSize="20px"
              />
            </IconButton>
          </Box>
        </Box>
        <Box mt={3}>
          <Typography variant="subtitle2">Tên dự án</Typography>
          <Box mt={1} display="flex" alignItems="center" gap={2}>
            <TextField
              variant="outlined"
              value={name}
              onChange={(e) => setName(e.target.value)}
              size="small"
              sx={{
                "& input": {
                  p: (theme) => theme.spacing(1.5),
                },
                borderColor: (theme) => theme.palette.grey[200],
              }}
            />
            <Button
              title="copy"
              variant="contained"
              size="small"
              startIcon={<Icon icon="fa-solid:exchange-alt" />}
            >
              Đổi tên
            </Button>
          </Box>
        </Box>
      </Box>
    </GeneralWrapper>
  );
};

export { GeneralManager };
