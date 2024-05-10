import {Box, Button, MultiSelect, NumberInput, Select, Textarea, TextInput} from "@mantine/core";
import React, {useEffect, useState} from "react";
import {apiGetPublicDistrict, apiGetPublicProvinces} from "../../services/AppRequest";
import PostRequest from "../../services/PostRequest";
import {toast} from "react-toastify";

const PostBuy = () => {

    const [propertyDetails, setPropertyDetails] = useState({
        title: "",
        description: "",

        province: "",
        district: [],

        minAcreage: 0,
        fromPrice: 0,
        toPrice: 0,
        fromPricePerM2: 0,
        toPricePerM2: 0,

        typeProperty: [],
        directionsProperty: [],
        minBedroom: 0,
        minParking: 0,
        minBathroom: 0,
        minFloor: 0,
        legalDocuments: [],
    });


    const [title, setTitle] = useState(null);
    const [description, setDescription] = useState(null);
    const [provinces, setProvinces] = useState([])
    const [districts, setDistricts] = useState([])
    const [province, setProvince] = useState(null)
    const [district, setDistrict] = useState([])
    const [typeProperty, setTypeProperty] = useState([]);
    const [directionsProperty, setDirectionsProperty] = useState([]);
    const [minAcreage, setMinAcreage] = useState(0);
    const [fromPrice, setFromPrice] = useState(0);
    const [toPrice, setToPrice] = useState(9999999999);
    const [fromPricePerM2, setFromPricePerM2] = useState(0);
    const [toPricePerM2, setToPricePerM2] = useState(0)
    const [minBedroom, setMinBedroom] = useState(0);
    const [minBathroom, setMinBathroom] = useState(0);
    const [minParking, setMinParking] = useState(0);
    const [minFloor, setMinFloor] = useState(0)
    const [legalDocuments, setLegalDocuments] = useState([])

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
    const handleSubmit = (e) => {
        e.preventDefault();
        setPropertyDetails({
            title: title,
            description: description,
            province: province,
            district: district,
            minAcreage: minAcreage,
            fromPrice: fromPrice,
            toPrice: toPrice,
            fromPricePerM2: fromPricePerM2,
            toPricePerM2: toPricePerM2,
            minFloor: minFloor,
            minBedroom: minBedroom,
            minBathroom: minBathroom,
            minParking: minParking,
            legalDocuments: legalDocuments,
            directionsProperty: directionsProperty,
            typeProperty: typeProperty,
        })
    }



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
        setDistrict([]);
    }, [province])

    useEffect(() => {
        if (minAcreage > 0) {
            setFromPricePerM2(fromPrice / minAcreage);
        }
    }, [fromPrice])

    useEffect(() => {
        if (minAcreage > 0) {
            setToPricePerM2(toPrice / minAcreage);
        }
    }, [toPrice])

    useEffect(() => {
        const request = new PostRequest();
        console.log("goi api", propertyDetails);
        request.addPostBuy(propertyDetails)
            .then((response) => {
                const status = response.code;
                if (status === 200) {
                    toast.success("Đăng bài thành công")
                } else {
                    toast.error(response.data.message)
                }
            })
        // request.addPostBuy(propertyDetails);
    }, [propertyDetails]);


    return (
        <div className="postBuy-wrapper">
            <h1> Thêm thông tin bất động sản bạn muốn mua</h1>

            <Box maw="50%" mx="auto" my="md">
                <TextInput
                    // w={"100%"}
                    withAsterisk
                    label="Tiêu đề"
                    value={title}
                    onChange={(event) => setTitle(event.currentTarget.value)}
                />
                <Textarea
                    placeholder="Description"
                    label="Mô tả"
                    withAsterisk
                    value={description}
                    onChange={(event) => setDescription(event.currentTarget.value)}
                />
                <Select
                    w={"100%"}
                    withAsterisk
                    label="Tỉnh"
                    clearable
                    searchable
                    data={optionsProvince}
                    value={province}
                    onChange={(value) => setProvince(value)}
                />

                <MultiSelect
                    w={"100%"}
                    withAsterisk
                    label="Quận/ Huyện"
                    clearable
                    searchable
                    data={optionsDistrict}
                    onChange={(value) => setDistrict(value)}
                    value={district}
                />

                <NumberInput
                    withAsterisk
                    label="Diện tích tối thiểu"
                    placeholder="1000"
                    min={0}
                    thousandSeparator=","
                    suffix=" mét vuông"
                    value={minAcreage}
                    onChange={(value) => setMinAcreage(value)}
                />
                <NumberInput
                    withAsterisk
                    label="Giá tối thiểu"
                    placeholder="1000"
                    min={0}
                    thousandSeparator=","
                    suffix=" VND"
                    value={fromPrice}
                    onChange={(value) => setFromPrice(value)}
                    error={fromPrice > toPrice ? "Gía tối thiểu phải bé hơn giá tối đa" : ""}
                />
                <small>Giá bán trên 1 m2 đất đang là: ${fromPricePerM2.toLocaleString('us-US')} VND</small>

                <NumberInput
                    withAsterisk
                    label="Giá tối đa"
                    placeholder="1000"
                    min={0}
                    thousandSeparator=","
                    suffix=" VND"
                    value={toPrice}
                    onChange={(value) => setToPrice(value)}
                />
                <small>Giá bán trên 1 m2 đất đang là: ${toPricePerM2.toLocaleString('us-US')} VND</small>

                <MultiSelect
                    withAsterisk
                    label="Kiểu bất sản muốn bán"
                    data={
                        [{value: 'LAND', label: 'Đất'},
                        {value: 'HOUSE', label: 'Nhà ở'},
                        {value: 'DEPARTMENT', label: 'Chung cư'}
                    ]}
                    onChange={(value) => setTypeProperty(value)}
                />

                <MultiSelect
                    withAsterisk
                    label="Hướng nhà"
                    data={[
                        {value: 'NORTH', label: 'Bắc'},
                        {value: 'EAST_NORTH', label: 'Đông Bắc'},
                        {value: 'EAST', label: 'Đông'},
                        {value: 'EAST_SOUTH', label: 'Đông Nam'},
                        {value: 'SOUTH', label: 'Nam'},
                        {value: 'WEST_SOUTH', label: 'Tây Nam'},
                        {value: 'WEST', label: 'Tây'},
                        {value: 'WEST_NORTH', label: 'Tây Bắc'}
                    ]}
                    onChange={(value) => setDirectionsProperty(value)}
                />

                <MultiSelect
                    withAsterisk
                    label="Trạng thái sổ đỏ"
                    data={[
                        {value: 'HAVE', label: 'Đã có sổ'},
                        {value: 'WAITING', label: 'Đang chờ'},
                        {value: 'HAVE_NOT', label: 'Không có'},
                    ]}
                    value={legalDocuments}
                    onChange={(value) => setLegalDocuments(value)}
                />

                <NumberInput
                    withAsterisk
                    label="Số tầng tối thiểu"
                    min={0}
                    value={minFloor}
                    onChange={(value) => setMinFloor(value)}
                    allowDecimal={false}
                    // disabled={isLand}
                />

                <NumberInput
                    withAsterisk
                    label="Số phòng ngủ tối thiểu"
                    min={0}
                    value={minBedroom}
                    onChange={(value) => setMinBedroom(value)}
                    allowDecimal={false}
                    // disabled={isLand}
                />

                <NumberInput
                    withAsterisk
                    label="Số phòng tắm tối thiểu"
                    min={0}
                    value={minBathroom}
                    onChange={(value) => setMinBathroom(value)}
                    allowDecimal={false}
                    // disabled={isLand}
                />
                <NumberInput
                    withAsterisk
                    label="Có thể đậu ít nhất bao nhiêu xe ô tô"
                    min={0}
                    value={minParking}
                    onChange={(value) => setMinParking(value)}
                    allowDecimal={false}
                    // disabled={isLand}
                />
            </Box>

            <Button onClick={handleSubmit} className="flexCenter" style={{margin: "3rem 45%"}}>
                Đăng
            </Button>

        </div>

    )
}

export default PostBuy;