import React, { useEffect, useState } from "react";
import { Button, Checkbox, Popover, RangeSlider, Select } from "@mantine/core";
import "./FilterSell.css";
import { LuChevronsUpDown } from "react-icons/lu";
import {
  transferDirection,
  transferPrice,
  transferTypeProperty,
} from "../../utils/common";
import MultiplySelect from "../MultiplySelect/MultiplySelect";
import DistrictRequest from "../../services/DistrictRequest";
import { IconSearch, IconAdjustments } from "@tabler/icons-react";

const FilterSell = ({ setParams }) => {
  const [provinceId, setProvinceId] = useState("");
  const [districtId, setDistrictId] = useState("");
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);

  const [filterAcreage, setFilterAcreage] = useState([0, 100]);
  const [fromAcreage, setFromAcreage] = useState(0);
  const [toAcreage, setToAcreage] = useState(100);
  const [checkMaxAcreage, setCheckMaxAcreage] = useState(false);

  const [filterPrice, setFilterPrice] = useState([0, 10000000000]);
  const [fromPrice, setFromPrice] = useState(0);
  const [toPrice, setToPrice] = useState(10000000000);
  const [checkMaxPrice, setCheckMaxPrice] = useState(false);

  const [directions, setDirections] = useState([
    "NORTH",
    "SOUTH",
    "WEST",
    "EAST",
    "EAST_NORTH",
    "EAST_SOUTH",
    "WEST_SOUTH",
    "WEST_NORTH",
  ]);
  const [typeProperties, setTypeProperties] = useState([
    "HOUSE",
    "APARTMENT",
    "LAND",
  ]);

  const optionsProvince = provinces?.map((item) => ({
    value: item.provinceId,
    label: item.nameProvince,
    key: item.provinceId,
  }));

  const optionsDistrict = districts?.map((item) => ({
    value: item.districtId,
    label: item.nameDistrict,
    key: item.districtId,
  }));

  const marksPrice = [
    { value: 2500000000, label: "2,5 tỷ" },
    { value: 5000000000, label: "5 tỷ" },
    { value: 7500000000, label: "7,5 tỷ" },
  ];

  const marksAcreage = [
    { value: 20, label: "20 m²" },
    { value: 40, label: "40 m²" },
    { value: 60, label: "60 m²" },
    { value: 80, label: "80 m²" },
  ];

  useEffect(() => {
    const fetchPublicProvince = () => {
      const districtRequest = new DistrictRequest();
      districtRequest.get_province().then((response) => {
        if (response.code === 200) {
          setProvinces(response.data);
        }
      });
    };
    fetchPublicProvince();
  }, []);

  useEffect(() => {
    const fetchPublicDistrict = () => {
      const districtRequest = new DistrictRequest();
      districtRequest
        .get_districts({
          provinceId,
        })
        .then((response) => {
          if (response.code === 200) {
            setDistricts(response.data);
          }
        });
    };
    provinceId && fetchPublicDistrict();
    setDistrictId(null);
  }, [provinceId]);

  useEffect(() => {
    if (checkMaxAcreage === false) {
      setFromAcreage(filterAcreage[0]);
      setToAcreage(filterAcreage[1]);
    } else {
      setFromAcreage(100);
      setToAcreage(0);
    }
  }, [filterAcreage, checkMaxAcreage]);

  useEffect(() => {
    if (checkMaxPrice === false) {
      setFromPrice(filterPrice[0]);
      setToPrice(filterPrice[1]);
    } else {
      setFromPrice(10000000000);
      setToPrice(0);
    }
  }, [filterPrice, checkMaxPrice]);

  const handleSearch = () => {
    const params = {
      page: 1,
      size: 10,
      fromAcreage: fromAcreage,
      fromPrice: fromPrice,
      typeProperties: typeProperties,
      directions: directions,
    };

    if (provinceId !== null && provinceId !== "") {
      params.provinceId = provinceId;
    }

    if (districtId !== null && districtId !== "") {
      params.districtId = districtId;
    }

    if (toAcreage > 0 && toAcreage > fromAcreage) {
      params.toAcreage = toAcreage;
    }

    if (toPrice > 0 && toPrice > fromPrice) {
      params.toPrice = toPrice;
    }

    setParams(params);
  };
  return (
    <div className="filterContainer flexStart">
      <Select
        w={"120px"}
        withAsterisk
        placeholder="Tỉnh"
        clearable
        searchable
        data={optionsProvince}
        value={provinceId}
        onChange={(value) => setProvinceId(value)}
      />

      <Select
        w={"125px"}
        withAsterisk
        placeholder="Huyện/Thành phố"
        clearable
        searchable
        data={optionsDistrict}
        onChange={(value) => setDistrictId(value)}
        value={districtId}
      />

      <Popover
        position="bottom-start"
        withArrow
        shadow="md"
        width={"400px"}
        zIndex={1000}
      >
        <Popover.Target>
          <Button
            w={"150px"}
            style={{
              backgroundColor: "white",
              color: "rgb(153, 153, 153)",
              fontSize: "16px",
              fontWeight: "50",
              margin: "0 5px",
              border: "1px solid #F2F2F2",
            }}
            rightSection={<IconAdjustments size={14} />}
          >
            Diện Tích
          </Button>
        </Popover.Target>
        <Popover.Dropdown>
          <RangeSlider
            label={(value) => value + "m²"}
            marks={marksAcreage}
            min={0}
            max={100}
            minRange={10}
            step={10}
            value={filterAcreage}
            onChange={setFilterAcreage}
            disabled={checkMaxAcreage}
          />
          <Checkbox
            style={{
              margin: "25px auto 5px auto",
            }}
            label="Trên 100 m²"
            checked={checkMaxAcreage}
            onChange={(event) =>
              setCheckMaxAcreage(event.currentTarget.checked)
            }
          />
        </Popover.Dropdown>
      </Popover>
      <Popover
        position="bottom-start"
        withArrow
        shadow="md"
        width={"400px"}
        zIndex={1000}
        styles={{}}
      >
        <Popover.Target>
          <Button
            w={"150px"}
            style={{
              backgroundColor: "white",
              color: "rgb(153, 153, 153)",
              fontSize: "16px",
              fontWeight: "50",
              margin: "0 5px",
              border: "1px solid #F2F2F2",
            }}
            rightSection={<IconAdjustments size={14} />}
          >
            Mức Giá
          </Button>
        </Popover.Target>
        <Popover.Dropdown>
          <RangeSlider
            label={(value) => transferPrice(value)}
            marks={marksPrice}
            min={0}
            max={10000000000}
            minRange={500000000}
            step={500000000}
            value={filterPrice}
            onChange={setFilterPrice}
            disabled={checkMaxPrice}
          />
          <Checkbox
            style={{
              margin: "25px auto 5px auto",
            }}
            label="Trên 10 tỷ"
            checked={checkMaxPrice}
            onChange={(event) => setCheckMaxPrice(event.currentTarget.checked)}
          />
        </Popover.Dropdown>
      </Popover>

      <MultiplySelect
        data={[
          { value: "NORTH", label: "Bắc" },
          { value: "EAST_NORTH", label: "Đông Bắc" },
          { value: "EAST", label: "Đông" },
          { value: "EAST_SOUTH", label: "Đông Nam" },
          { value: "SOUTH", label: "Nam" },
          { value: "WEST_SOUTH", label: "Tây Nam" },
          { value: "WEST", label: "Tây" },
          { value: "WEST_NORTH", label: "Tây Bắc" },
        ]}
        value={directions}
        setValue={setDirections}
        transferContent={transferDirection}
      />

      <MultiplySelect
        data={[
          { value: "HOUSE", label: "Nhà Ở" },
          { value: "APARTMENT", label: "Chung Cư" },
          { value: "LAND", label: "Đất" },
        ]}
        value={typeProperties}
        setValue={setTypeProperties}
        transferContent={transferTypeProperty}
      />

      <Button
        w={"150px"}
        style={{
          fontSize: "16px",
          fontWeight: "50",
          margin: "0 5px",
          border: "1px solid #F2F2F2",
        }}
        leftSection={<IconSearch size={14} />}
        onClick={handleSearch}
      >
        Mức Giá
      </Button>
    </div>
  );
};

export default FilterSell;
