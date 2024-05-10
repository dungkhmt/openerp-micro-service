import React, {useEffect, useRef, useState} from "react";
import {Button, Checkbox, Drawer, NumberInput, Select} from "@mantine/core";
import {apiGetPublicDistrict, apiGetPublicProvinces} from "../../services/AppRequest";
import "./FilterSell.css"
import {useDisclosure} from "@mantine/hooks";

const FilterSell = ({setParams}) => {
    const [opened, {open, close}] = useDisclosure(false);

    const typeProperties = useRef(["LAND", "HOUSE", "APARTMENT"]);
    const directions = useRef(["NORTH", "SOUTH", "WEST", "EAST", "EAST_NORTH",
        "EAST_SOUTH", "WEST_SOUTH", "WEST_NORTH"]);
    const legalDocuments = useRef(["HAVE", "WAIT", "HAVE_NOT"]);
    const [province, setProvince] = useState("")
    const [district, setDistrict] = useState("")
    const [provinces, setProvinces] = useState([])
    const [districts, setDistricts] = useState([])

    const minAcreage = useRef(0)
    const fromPrice = useRef(0)
    const toPrice = useRef(99999999999)

    const minFloor = useRef(0);
    const minBathroom = useRef(0)
    const minBedroom = useRef(0)
    const minParking = useRef(0)

    const optionsProvince = provinces?.map((item) => ({
        value: item.nameProvince,
        label: item.nameProvince,
        key: item.provinceId,
    }));

    const optionsDistrict = districts?.map((item) => ({
        value: item.nameDistrict,
        label: item.nameDistrict,
        key: item.districtId
    }));

    useEffect(() => {
        const fetchPublicProvince = async () => {
            const response = await apiGetPublicProvinces()
            if (response.status === 200) {
                setProvinces(response?.data.data)
            }
        }
        fetchPublicProvince()
    }, [])

    useEffect(() => {
        const fetchPublicDistrict = async () => {
            const response = await apiGetPublicDistrict(province)
            if (response.status === 200) {
                setDistricts(response.data?.data)
            }
        }
        province && fetchPublicDistrict()
        setDistrict(null);
    }, [province])

    const handleTypeProperties = (value) => {
        typeProperties.current = value;
    };

    const handleDirections = (value) => {
        directions.current = value;
    }

    const handleLegalDocuments = (value) => {
        legalDocuments.current = value;
    }
    const handleSearch = () => {
        console.log(typeProperties.current)
        setParams({
            page: 1,
            size: 10,
            province: province,
            district: district,
            minAcreage: minAcreage.current,
            fromPrice: fromPrice.current,
            toPrice: toPrice.current,
            minFloor: minFloor.current,
            minBedroom: minBedroom.current,
            minBathroom: minBathroom.current,
            minParking: minParking.current,
            typeProperties: typeProperties.current,
            legalDocuments: legalDocuments.current,
            directions: directions.current,
        })
    }
    return (
        <div className="filterContainer">
            <Button onClick={open}>Open drawer</Button>

            <Drawer
                opened={opened}
                onClose={close}
                overlayProps={{ backgroundOpacity: 0.5, blur: 4 }}
            >
                <Select
                    w={"100%"}
                    withAsterisk
                    label="Province"
                    clearable
                    searchable
                    data={optionsProvince}
                    value={province}
                    onChange={(value) => setProvince(value)}
                />

                <Select
                    w={"100%"}
                    withAsterisk
                    label="District"
                    clearable
                    searchable
                    data={optionsDistrict}
                    onChange={(value) => setDistrict(value)}
                    value={district}
                />

                <NumberInput
                    defaultValue="0"
                    withAsterisk
                    label="Diện tích tối thiểu"
                    min={0}
                    thousandSeparator=","
                    suffix=" mét vuông"
                    onChange={(value) => minAcreage.current = value}
                />
                <NumberInput
                    defaultValue="0"
                    withAsterisk
                    label="Giá tối thiểu"
                    min={0}
                    thousandSeparator=","
                    suffix=" VND"
                    // value={fromPrice}
                    onChange={(value) => fromPrice.current = value}
                    error={fromPrice > toPrice ? "Gía tối thiểu phải bé hơn giá tối đa" : ""}
                />

                <NumberInput
                    defaultValue="99999999999"
                    withAsterisk
                    label="Giá tối đa"
                    placeholder="1000"
                    min={0}
                    thousandSeparator=","
                    suffix=" VND"
                    // value={toPrice}
                    onChange={(value) => toPrice.current = value}
                />
                {/*<small>Giá bán trên 1 m2 đất đang là: ${toPricePerM2.toLocaleString('us-US')} VND</small>*/}

                <Checkbox.Group
                    defaultValue={["LAND", "HOUSE", "APARTMENT"]}
                    label="Chọn kiểu bất động sản"
                    onChange={handleTypeProperties}
                    withAsterisk
                >
                    <Checkbox value="LAND" label="Đất" style={{marginTop: "5px"}}/>
                    <Checkbox value="HOUSE" label="Nhà ở" style={{marginTop: "5px"}}/>
                    <Checkbox value="APARTMENT" label="Chung cư" style={{marginTop: "5px"}}/>
                </Checkbox.Group>

                <Checkbox.Group
                    defaultValue={["NORTH", "EAST_NORTH", "EAST", "EAST_SOUTH",
                        "SOUTH", "WEST_SOUTH", "WEST", "WEST_NORTH"]}
                    label="Chọn hướng"
                    onChange={handleDirections}
                    withAsterisk
                >
                    <Checkbox value="NORTH" label="Phía Bắc" style={{marginTop: "5px"}}/>
                    <Checkbox value="EAST_NORTH" label="Phía Đông Bắc" style={{marginTop: "5px"}}/>
                    <Checkbox value="EAST" label="Phía Đông" style={{marginTop: "5px"}}/>
                    <Checkbox value="EAST_SOUTH" label="Phía Đông Nam" style={{marginTop: "5px"}}/>
                    <Checkbox value="SOUTH" label="Phía Nam" style={{marginTop: "5px"}}/>
                    <Checkbox value="WEST_SOUTH" label="Phía Tây Nam" style={{marginTop: "5px"}}/>
                    <Checkbox value="WEST" label="Phía Tây" style={{marginTop: "5px"}}/>
                    <Checkbox value="WEST_NORTH" label="Phía Tây Bắc" style={{marginTop: "5px"}}/>
                </Checkbox.Group>


                <Checkbox.Group
                    defaultValue={["HAVE", "WAIT", "HAVE_NOT"]}
                    label="Chọn trạng thái sổ đỏ"
                    onChange={handleLegalDocuments}
                    withAsterisk
                >
                    <Checkbox value="HAVE" label="Đã có sổ đỏ" style={{marginTop: "5px"}}/>
                    <Checkbox value="WAIT" label="Đang chờ sổ đỏ" style={{marginTop: "5px"}}/>
                    <Checkbox value="HAVE_NOT" label="Không có sổ đỏ" style={{marginTop: "5px"}}/>
                </Checkbox.Group>


                <NumberInput
                    withAsterisk
                    label="Số tầng tối thiểu"
                    min={0}
                    defaultValue={0}
                    // value={minFloor}
                    onChange={(value) => minFloor.current = value}
                    allowDecimal={false}
                    // disabled={isLand}
                />

                <NumberInput
                    withAsterisk
                    label="Số phòng ngủ tối thiểu"
                    min={0}
                    defaultValue={0}
                    // value={minBedroom}
                    onChange={(value) => minBedroom.current = value}
                    allowDecimal={false}
                    // disabled={isLand}
                />

                <NumberInput
                    withAsterisk
                    label="Số phòng tắm tối thiểu"
                    min={0}
                    defaultValue={0}

                    // value={minBathroom}
                    onChange={(value) => minBathroom.current = value}
                    allowDecimal={false}
                    // disabled={isLand}
                />
                <NumberInput
                    withAsterisk
                    label="Có thể đậu ít nhất bao nhiêu xe ô tô"
                    min={0}
                    defaultValue={0}

                    // value={minParking}
                    onChange={(value) => minParking.current = value}
                    allowDecimal={false}
                    // disabled={isLand}
                />

                <Button onClick={handleSearch}>
                    Áp Dụng
                </Button>

            </Drawer>
        </div>
    )
}

export default FilterSell;