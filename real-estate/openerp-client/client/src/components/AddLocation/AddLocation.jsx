import {useEffect, useState} from 'react';
import {apiGetPublicDistrict, apiGetPublicProvinces} from "../../services/app";
import {Button, Group, Select, TextInput} from "@mantine/core";
import {useForm} from "@mantine/form";
import {validateString} from "../../utils/common";
import Map from "../Map/Map";

const AddLocation = ({propertyDetails, setPropertyDetails, nextStep}) => {

    const form = useForm({
        initialValues: {
            province: propertyDetails?.province,
            district: propertyDetails?.district,
            address: propertyDetails?.address,
        },

        validate: {
            province: (value) => validateString(value),
            district: (value) => validateString(value),
            address: (value) => validateString(value),
        },
    });

    const [provinces, setProvinces] = useState([])
    const [districts, setDistricts] = useState([])
    const [province, setProvince] = useState(propertyDetails?.province)
    const [district, setDistrict] = useState(propertyDetails?.district)
    const [addressInput, setAddressInput] = useState('')
    const [address, setAddress] = useState(propertyDetails?.address)
    const [showPosition, setShowPosition] = useState(true)

    const [reset, setReset] = useState(false)

    let activeNextPage = false;
    useEffect(() => {
        const fetchPublicProvince = async () => {
            const response = await apiGetPublicProvinces()
            if (response.status === 200) {
                console.log("run", response)
                setProvinces(response?.data.data)
            }
        }
        fetchPublicProvince()
    }, [])

    useEffect(() => {
        const fetchPublicDistrict = async () => {
            const response = await apiGetPublicDistrict(province.provinceId)
            if (response.status === 200) {
                setDistricts(response.data?.data)
            }
        }
        province && fetchPublicDistrict()
        setDistrict(null)
    }, [province])

    useEffect(() => {
        setAddress(null);
    }, [province, district, addressInput])
    // const { province, district, address } = form.values;

    useEffect(() => {
        setShowPosition(false);
    }, [addressInput]);
    const handleSubmit = () => {
        console.log('Submit')
        setPropertyDetails((prev) => ({...prev, province, district, address}))
        nextStep()
    }

    // const onChangeProvince = (value) => {
    //     setProvince(value.provinceId)
    // }
    //
    // const onChangeDistrict = (value) => {
    //
    //     setName(value.nameDistrict)
    //     // console.log(value)
    //     setDistrict(value.districtId)
    // }

    const optionsProvince = provinces?.map((item) => ({
        value: item,
        label: item.nameProvince,
        key: item.provinceId,
    }));

    const optionsDistrict = districts?.map((item) => ({
        value: item,
        label: item.nameDistrict,
        key: item.districtId
    }));

    console.log("gia tri address", address, addressInput)
    return (
        <div>
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    if (activeNextPage) {
                        handleSubmit()
                    } else {
                        activeNextPage = true;
                    }
                }}
            >
                <div
                    className="flexCenter"
                    style={{
                        justifyContent: "space-between",
                        gap: "3rem",
                        marginTop: "3rem",
                        flexDirection: "row",
                    }}
                >
                    {/* left side */}
                    {/* inputs */}

                    <div className="flexColStart" style={{flex: 1, gap: "1rem"}}>
                        <Select
                            w={"100%"}
                            withAsterisk
                            label="Province"
                            clearable
                            searchable
                            data={optionsProvince}
                            onChange={value => setProvince(value)}
                        />

                        <Select
                            w={"100%"}
                            withAsterisk
                            label="District"
                            clearable
                            searchable
                            data={optionsDistrict}
                            onChange={value => setDistrict(value)}
                            value={district}
                        />
                        <TextInput
                            w={"100%"}
                            withAsterisk
                            label="Address"
                            // value={address}
                            onChange={(event) => setAddressInput(event.currentTarget.value)}
                        />

                    </div>


                    <div style={{flex: 1}}>
                        <Map address={address} district={district?.nameDistrict} province={province?.nameProvince}/>
                    </div>
                </div>


                <Group position="center" mt={"xl"}>
                    {
                        (address === '' || address === null || address !== addressInput) ?
                            (<Button onClick={() => {
                                console.log('run position')
                                setShowPosition(false)
                                setAddress(addressInput)
                            }}>
                                Find Position
                            </Button>) :
                            (
                                <Button type="submit">Next Step</Button>
                            )
                    }
                </Group>


            </form>
        </div>
    )
}

export default AddLocation