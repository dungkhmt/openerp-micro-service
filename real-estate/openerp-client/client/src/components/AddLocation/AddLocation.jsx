import {useEffect, useState} from 'react';
import {apiGetPublicDistrict, apiGetPublicProvinces} from "../../services/AppRequest";
import {Button, Group, Select, TextInput} from "@mantine/core";
import {useForm} from "@mantine/form";
import {validateString} from "../../utils/common";
import Map from "../Map/Map";

const AddLocation = ({propertyDetails, setPropertyDetails, nextStep}) => {

    // const form = useForm({
    //     initialValues: {
    //         province: propertyDetails?.province,
    //         district: propertyDetails?.district,
    //         address: propertyDetails?.address,
    //     },
    //
    //     validate: {
    //         province: (value) => validateString(value),
    //         district: (value) => validateString(value),
    //         address: (value) => validateString(value),
    //     },
    // });

    const [provinces, setProvinces] = useState([])
    const [districts, setDistricts] = useState([])
    const [province, setProvince] = useState(propertyDetails?.province)
    const [district, setDistrict] = useState(propertyDetails?.district)
    const [addressInput, setAddressInput] = useState('')
    const [address, setAddress] = useState(propertyDetails?.address)
    const [position, setPosition] = useState(propertyDetails?.position)

    // const [reset, setReset] = useState(false)

    // let activeNextPage = false;
    useEffect(() => {
        const fetchPublicProvince = async () => {
            const response = await apiGetPublicProvinces()
            if (response.status === 200) {
                setProvinces(response?.data.data)
            }
        }
        fetchPublicProvince()
    }, [])

    const onChangeProvince = (value) => {
        setProvince(value)
    }

    const onChangeDistrict = (value) => {
        setDistrict(value)
    }

    useEffect(() => {
        const fetchPublicDistrict = async () => {
            const response = await apiGetPublicDistrict(province)
            if (response.status === 200) {
                setDistricts(response.data?.data)
            }
        }
        province && fetchPublicDistrict()
        console.log("set district ve nyll");
        setDistrict(null);
    }, [province])

    useEffect(() => {
        setAddress(null);
    }, [province, district, addressInput])

    const handleSubmit = () => {
        // console.log('Submit')
        setPropertyDetails((prev) => ({...prev, province, district, address, position}))
        nextStep()
    }

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

    // console.log("gia tri address", address, addressInput)
    console.log("da luu", propertyDetails)
    // console.log("district", district)
    // console.log("gia tri province", province)
    return (
        <div>
            {/*<form*/}
            {/*    onSubmit={(e) => {*/}
            {/*        e.preventDefault();*/}
            {/*        handleSubmit()*/}

            {/*        // if (activeNextPage) {*/}
            {/*        //     handleSubmit()*/}
            {/*        // } else {*/}
            {/*        //     activeNextPage = true;*/}
            {/*        // }*/}
            {/*    }}*/}
            {/*>*/}
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
                            // withAsterisk
                            label="Province"
                            clearable
                            searchable
                            data={optionsProvince}
                            value={province? province : null}
                            onChange={onChangeProvince}
                        />

                        <Select
                            w={"100%"}
                            withAsterisk
                            label="District"
                            clearable
                            searchable
                            data={optionsDistrict}
                            onChange={onChangeDistrict}
                            value={district? district : null}
                        />
                        <TextInput
                            w={"100%"}
                            withAsterisk
                            label="Address"
                            value={address}
                            onChange={(event) => setAddressInput(event.currentTarget.value)}
                        />

                    </div>


                    <div style={{flex: 1}}>
                        <Map address={address} district={district} province={province} position={position} setPosition={setPosition}/>
                    </div>
                </div>

            <Group position="center" mt={"xl"}>
            {
                (address === '' || address === null || address !== addressInput) ?
                    (<Button onClick={() => {
                        setAddress(addressInput)
                    }}>
                        Find Position
                    </Button>) :
                    (
                        <Button onClick={handleSubmit}>Next Step</Button>
                    )
            }
            </Group>

        </div>
    )
}

export default AddLocation