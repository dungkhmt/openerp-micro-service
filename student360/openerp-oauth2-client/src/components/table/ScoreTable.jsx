import {
  Box,
  Card,
  CardHeader,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  styled,
} from "@mui/material";
import GrowthCell from "../cell/GrowthCell";

// Components stated
const TableContainerStyled = styled(TableContainer)(({ theme }) => ({
  maxHeight: theme.spacing(66),
  minHeight: theme.spacing(44),
  overFlowX: "auto",
}));

const CardStyled = styled(Card)(({ theme }) => ({
  backgroundColor: theme.palette.grey[100],
  padding: theme.spacing(3),
  borderRadius: theme.spacing(1),
  flexGrow: 1,
}));

const CardHeaderStyled = styled(CardHeader)(({ theme }) => ({
  padding: 0,
}));

const HeaderWrapper = styled(Box)(({ theme }) => ({
  display: "flex",
  justifyContent: "space-between",
  alignItems: "center",
}));

const LeftTitle = styled(Box)(({ theme }) => ({
  ...theme.typography.h5,
  color: theme.palette.grey[900],
  flex: 1,
  textAlign: "left",
  [theme.breakpoints.down("sm")]: {
    ...theme.typography.contentMBold,
  },
}));

const RightTitle = styled(Box)(({ theme }) => ({
  ...theme.typography.contentMRegular,
  color: theme.palette.grey[900],
  flex: 1,
  textAlign: "right",
}));

const TableRowStyled = styled(TableRow)(({ theme }) => ({
  height: "84px",
}));

const TableCellStyled = styled(TableCell)(({ theme }) => ({
  paddingLeft: theme.spacing(2.5),
}));

const BoxStyled = styled(Box)(({ theme }) => ({
  borderRadius: theme.spacing(0, 0, 3, 3),
  position: "relative",
  "&::after": {
    content: '""',
    position: "absolute",
    left: 0,
    bottom: 0,
    height: theme.spacing(50 / 8),
    width: "100%",
    background:
      "linear-gradient(180deg, rgba(255, 255, 255, 0.00) 0%, #FFF 100%)",
  },
}));

const ScoreTable = ({ title, subtitle, headTable, data }) => {
  if (!data || typeof data !== "object") {
    return <div>No data available</div>;
  }

  return (
    <CardStyled>
      <CardHeaderStyled
        disableTypography
        title={
          <HeaderWrapper>
            <LeftTitle>{title}</LeftTitle>
            <RightTitle>{subtitle}</RightTitle>
          </HeaderWrapper>
        }
      />

      <BoxStyled>
        <TableContainerStyled>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                {headTable.map((header, index) => (
                  // Table Header
                  <TableCell
                    sx={{
                      backgroundColor: "grey.100",
                      // width: "30px",
                      typography: {
                        xs: "contentSBold",
                        sm: "contentSBold",
                      },

                      whiteSpace: "nowrap",
                    }}
                    key={`${header}-${index}`}
                  >
                    {header}
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {data
                .sort((b, a) => a.semester - b.semester)
                ?.map((item, index) => (
                  <TableRowStyled
                    key={`${item}-${index}`}
                    sx={{
                      "&:nth-of-type(odd)": {
                        bgcolor: "grey.200",
                      },
                      "&:hover": {
                        bgcolor: "grey.400",
                      },
                    }}
                  >
                    <TableCellStyled align={"left"}>
                      {item.semester}
                    </TableCellStyled>
                    <TableCellStyled align={"left"}>
                      <GrowthCell
                        marginLeft={"-12px"}
                        growth={item?.numStudentsWithGradeA}
                        evolution={item?.growthGradeA || 0}
                      />
                    </TableCellStyled>
                    <TableCellStyled align={"left"}>
                      <GrowthCell
                        marginLeft={"-12px"}
                        growth={item?.numStudentsWithGradeB}
                        evolution={item?.growthGradeB}
                      />
                    </TableCellStyled>
                    <TableCellStyled align={"left"}>
                      <GrowthCell
                        marginLeft={"-12px"}
                        growth={item?.numStudentsWithGradeC}
                        evolution={item?.growthGradeC}
                      />
                    </TableCellStyled>
                    <TableCellStyled align={"left"}>
                      <GrowthCell
                        marginLeft={"-12px"}
                        growth={item?.numStudentsWithGradeD}
                        evolution={item?.growthGradeD}
                      />
                    </TableCellStyled>
                    <TableCellStyled align={"left"}>
                      <GrowthCell
                        marginLeft={"-24px"}
                        growth={item?.numStudentsWithGradeF}
                        evolution={item?.growthGradeF}
                      />
                    </TableCellStyled>
                  </TableRowStyled>
                ))}
            </TableBody>
          </Table>
        </TableContainerStyled>
      </BoxStyled>
    </CardStyled>
  );
};

export default ScoreTable;
