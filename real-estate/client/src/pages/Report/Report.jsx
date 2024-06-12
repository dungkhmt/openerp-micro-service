import React, { useEffect, useState } from "react";
import DistrictRequest from "../../services/DistrictRequest";
import { Accordion, Divider, Grid, Group, Select } from "@mantine/core";
import DashboardRequest from "../../services/DashboardRequest";
import { transferTimeToDate } from "../../utils/common";
import { BarChart, LineChart } from "@mantine/charts";
import "@mantine/charts/styles.css";
import { v4 as uuidv4 } from "uuid";

const uniqueId = uuidv4();

const Report = () => {
  const [topLand, setTopLand] = useState([]);
  const [topHouse, setTopHouse] = useState([]);
  const [topApartment, setTopApartment] = useState([]);
  const [priceDistricts, setPriceDistricts] = useState(new Map());
  const [provinces, setProvinces] = useState([]);

  const [provinceId, setProvinceId] = useState("01");

  const formattedData = new Map();

  function findPriceMax(values) {
    let maxPrice = 0;

    values.forEach((item) => {
      maxPrice = Math.max(maxPrice, item.house, item.apartment, item.land);
    });
    if (maxPrice <= 100) {
      return maxPrice;
    } else {
      return maxPrice - (maxPrice % 50) + 50;
    }
  }

  const transferPriceMedium = (value) => {
    if (value === null) return null;
    return (value / 1000000).toFixed(2);
  };

  useEffect(() => {
    const districtRequest = new DistrictRequest();
    districtRequest.get_province().then((response) => {
      if (response.code === 200) {
        setProvinces(response.data);
        // provinceOptions = response.data;
      }
    });
  }, []);

  useEffect(() => {
    if (provinceId !== "") {
      const districtRequest = new DistrictRequest();
      const dashboardRequest = new DashboardRequest();

      dashboardRequest
        .get_top({
          provinceId,
          typeProperty: "LAND",
        })
        .then((response) => {
          if (response.code === 200) {
            setTopLand(response.data);
          }
        });

      dashboardRequest
        .get_top({
          provinceId,
          typeProperty: "HOUSE",
        })
        .then((response) => {
          if (response.code === 200) {
            setTopHouse(response.data);
          }
        });

      dashboardRequest
        .get_top({
          provinceId,
          typeProperty: "APARTMENT",
        })
        .then((response) => {
          if (response.code === 200) {
            setTopApartment(response.data);
          }
        });

      const now = Date.now();
      const fromTime = now - (now % 604800000) - 604800000 * 10;
      districtRequest
        .get_price_districts({
          provinceId,
          fromTime,
        })
        .then((response) => {
          if (response.code === 200) {
            const data = response.data;
            Object.values(data).forEach((districtList) => {
              districtList.forEach((item) => {
                if (!formattedData.has(item.nameDistrict)) {
                  formattedData.set(item.nameDistrict, []);
                }
                formattedData.get(item.nameDistrict).push({
                  date: transferTimeToDate(item.startTime),
                  // totalPost: item.totalPost,
                  house: transferPriceMedium(item.mediumHouse),
                  apartment: transferPriceMedium(item.mediumApartment),
                  land: transferPriceMedium(item.mediumLand),
                });
              });
            });
            setPriceDistricts(formattedData);
          }
        });
    }
  }, [provinceId]);

  const topLandData = topLand?.map((item) => {
    return {
      name: item.nameDistrict,
      price: (item.mediumPrice / 1000000).toFixed(0),
    };
  });

  const topHouseData = topHouse?.map((item) => {
    return {
      name: item.nameDistrict,
      price: (item.mediumPrice / 1000000).toFixed(0),
    };
  });

  const topApartmentData = topApartment?.map((item) => {
    return {
      name: item.nameDistrict,
      price: (item.mediumPrice / 1000000).toFixed(0),
    };
  });

  const optionsProvince = provinces?.map((item) => ({
    value: item.provinceId,
    label: item.nameProvince,
    key: item.provinceId,
  }));

  return (
    <div
      className="flexColCenter"
      style={{
        marginTop: "20px",
      }}
    >
      <Select
        w={"120px"}
        placeholder="Tỉnh"
        searchable
        data={optionsProvince}
        value={provinceId}
        onChange={(value, option) => {
          setProvinceId(option?.value);
        }}
      />

      <Grid w={"95%"} style={{ margin: "20px" }} justify="space-around">
        {topLandData.length > 0 && (
          <Grid.Col span={{ base: 12, md: 4 }}>
            <h2>Top 5 giá đất</h2>
            <BarChart
              h={300}
              data={topLandData}
              dataKey="name"
              series={[{ name: "price", color: "violet.6" }]}
              tickLine="y"
              yAxisLabel="triệu/m²"
              withBarValueLabel
              barChartProps={{
                barSize: 30,
              }}
            />
          </Grid.Col>
        )}
        {topApartmentData.length > 0 && (
          <Grid.Col span={{ base: 12, md: 4 }}>
            <h2>Top 5 giá chung cư</h2>
            <BarChart
              h={300}
              data={topApartmentData}
              dataKey="name"
              series={[{ name: "price", color: "violet.6" }]}
              tickLine="y"
              yAxisLabel="triệu/m²"
              withBarValueLabel
              barChartProps={{
                barSize: 30,
              }}
            />
          </Grid.Col>
        )}
        {topHouseData.length > 0 && (
          <Grid.Col span={{ base: 12, md: 4 }}>
            <h2>Top 5 giá nhà</h2>
            <BarChart
              h={300}
              data={topHouseData}
              dataKey="name"
              series={[{ name: "price", color: "violet.6" }]}
              tickLine="y"
              yAxisLabel="triệu/m²"
              withBarValueLabel
              barChartProps={{
                barSize: 30,
              }}
            />
          </Grid.Col>
        )}
      </Grid>

      {Array.from(priceDistricts.entries()).map(
        ([nameDistrict, values], index) => {
          const priceMax = findPriceMax(values);

          if (priceMax > 0)
            return (
              <Accordion
                key={index}
                style={{
                  width: "70%",
                  margin: "10px auto",
                }}
              >
                <Accordion.Item value={nameDistrict}>
                  <Accordion.Control>{nameDistrict}</Accordion.Control>
                  <Accordion.Panel>
                    <LineChart
                      style={{ width: "100%" }}
                      id={uniqueId}
                      h={300}
                      data={values}
                      dataKey="date"
                      series={[
                        { name: "house", label: "Nhà ở", color: "red" },
                        {
                          name: "apartment",
                          label: "Chung cư",
                          color: "green.6",
                        },
                        { name: "land", label: "Đất", color: "yellow" },
                        // {name: 'totalPost', label: 'Tổng số bài viết', color: 'green.6'},
                      ]}
                      curveType="linear"
                      yAxisProps={{ domain: [0, priceMax] }}
                      connectNulls
                      withLegend
                      legendProps={{ verticalAlign: "bottom" }}
                      tickLine="xy"
                      gridAxis="x"
                      xAxisLabel="Ngày"
                      yAxisLabel="triệu/m²"
                    />
                  </Accordion.Panel>
                </Accordion.Item>
              </Accordion>
            );
        },
      )}
    </div>
  );
};

export default Report;
