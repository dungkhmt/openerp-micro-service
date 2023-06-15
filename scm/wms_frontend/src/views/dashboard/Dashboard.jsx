import { Box, Stack, Typography } from "@mui/material";
import { blue } from "@mui/material/colors";
import AreaChart from "../../components/chart/AreaChart";
import BarChart from "../../components/chart/BarChart";
import ColumnChart from "../../components/chart/ColumnChart";
import LineChart from "../../components/chart/LineChart";
import MapChart from "../../components/chart/MapChart";
import PieChart from "../../components/chart/PieChart";
import {
  useGetImportedProduct,
  useGetNewFacilityMonthly,
} from "../../controllers/query/dashboard-query";
import { getRandomIntInclusive } from "../../layout/notification/Notification";
const raw = [
  {
    name: "Bến Tre",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Đồng Nai",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Cà Mau",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Sóc trăng",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Bắc Giang",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Cần Thơ",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Vĩnh Long",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Trà Vinh",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "An Giang",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
  {
    name: "Đồng Tháp",
    sales: getRandomIntInclusive(10000000, 100000000),
  },
];
const raw2 = [
  {
    day: "01/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "02/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "03/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "04/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "05/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "06/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "07/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
  {
    day: "08/12",
    "Hưng Thịnh - Bến Tre": getRandomIntInclusive(1000000, 100000000),
    "Vy Yên Phú - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
    "Thảo Vy - Đồng Nai": getRandomIntInclusive(1000000, 100000000),
  },
];
const DashBoard = () => {
  const { isLoading: loadNewFacility, data: facility } =
    useGetNewFacilityMonthly({
      year: 2023,
    });
  const { isLoading: loadImportedProduct, data: importedProducts } =
    useGetImportedProduct({
      month: 5,
      year: 2023,
    });
  const data = {
    categories: raw.map((el) => el.name),
    series: [
      {
        data: raw.slice(0, 7).map((el) => el.sales),
      },
    ],
  };
  return (
    <Box>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="row"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography sx={{ textTransform: "uppercase", fontWeight: 600 }}>
              Số lượng nhà phân phối mở mới
            </Typography>
          </Stack>
          <BarChart
            loading={loadNewFacility}
            categories={facility ? facility?.map((el) => el) : []}
            series={[
              {
                name: "Number of facilities",
                data: facility?.map((el) => el),
                color: blue[500],
              },
            ]}
          />
        </Box>
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Tỉ trọng nhập kho"}
            </Typography>
            <PieChart loading={loadImportedProduct} series={importedProducts} />
          </Stack>
        </Box>
      </Stack>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Tỉ trọng nhập kho"}
            </Typography>
            <ColumnChart
              series={data.series}
              categories={data.categories.slice(0, 7)}
            />
          </Stack>
        </Box>
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Tỉ trọng nhập kho"}
            </Typography>
            <AreaChart />
          </Stack>
        </Box>
      </Stack>
      <Stack direction="row">
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Tỉ trọng nhập kho"}
            </Typography>
            <LineChart
              categories={raw2.map((el) => el.day)}
              series={[
                {
                  name: "Hưng Thịnh - Bến Tre",
                  data: raw2.map((el) => el["Hưng Thịnh - Bến Tre"]),
                },
                {
                  name: "Vy Yên Phú - Đồng Nai",
                  data: raw2.map((el) => el["Vy Yên Phú - Đồng Nai"]),
                },
                {
                  name: "Thảo Vy - Đồng Nai",
                  data: raw2.map((el) => el["Thảo Vy - Đồng Nai"]),
                },
              ]}
            />
          </Stack>
        </Box>
        <Box sx={{ p: 2, width: "50%" }}>
          <Stack
            direction="column"
            spacing={2}
            sx={{ alignItems: "center", mb: 2 }}
          >
            <Typography
              sx={{ textTransform: "uppercase", fontWeight: 600, mb: 1 }}
            >
              {"Tỉ trọng nhập kho"}
            </Typography>
          </Stack>
          <MapChart />
        </Box>
      </Stack>
    </Box>
  );
};
export default DashBoard;
