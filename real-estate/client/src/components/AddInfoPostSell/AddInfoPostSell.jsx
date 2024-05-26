import {Box, Button, Grid, NumberInput, Select, TextInput} from "@mantine/core";
import Map from "../Map/Map";
import {AiOutlineCloudUpload} from "react-icons/ai";
import {ImBin} from "react-icons/im";
import React, {useEffect, useState} from "react";
import "./AddInfoPostSell.css";
import {deleteObject, getDownloadURL, ref, uploadBytesResumable} from "firebase/storage";
import {storage} from "../UploadImage/FireBaseConfig";
import {v4} from "uuid";
import {apiGetPublicDistrict, apiGetPublicProvinces} from "../../services/AppRequest";
import {CKEditor} from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import {toast, ToastContainer} from "react-toastify";
import {transferPrice} from "../../utils/common";

const AddInfoPostSell = ({propertyDetails, setPropertyDetails, setShowPost}) => {

    const [provinces, setProvinces] = useState([])
    const [districts, setDistricts] = useState([])
    const [provinceId, setProvinceId] = useState(propertyDetails?.provinceId)
    const [nameProvince, setNameProvince] = useState(propertyDetails?.nameProvince)
    const [districtId, setDistrictId] = useState(propertyDetails?.districtId)
    const [nameDistrict, setNameDistrict] = useState(propertyDetails?.nameDistrict)
    const [address, setAddress] = useState(propertyDetails.address)

    const [position, setPosition] = useState(propertyDetails?.position || [])
    const [imageUrls, setImageUrls] = useState(propertyDetails?.imageUrls || [])

    // thong tin chi tiet
    const [title, setTitle] = useState(propertyDetails?.title || null);
    const [description, setDescription] = useState(propertyDetails?.description || null);
    const [typeProperty, setTypeProperty] = useState(propertyDetails?.typeProperty || 'APARTMENT');
    const [directionsProperty, setDirectionsProperty] = useState(propertyDetails?.directionsProperty || null);
    const [acreage, setAcreage] = useState(propertyDetails?.acreage || 0);
    const [bedroom, setBedroom] = useState(propertyDetails?.bedroom || 0);
    const [bathroom, setBathroom] = useState(propertyDetails?.bathroom || 0);
    const [parking, setParking] = useState(propertyDetails?.parking) || 0;
    const [floor, setFloor] = useState(propertyDetails?.floor || 0)
    const [legalDocument, setLegalDocument] = useState(propertyDetails?.legalDocument || null)
    const [horizontal, setHorizontal] = useState(propertyDetails?.horizontal || 0);
    const [vertical, setVertical] = useState(propertyDetails?.vertical || 0)
    const [price, setPrice] = useState(propertyDetails?.price || 0)
    const [pricePerM2, setPricePerM2] = useState(propertyDetails?.pricePerM2 || 0)

    const optionsProvince = provinces?.map((item) => ({
        value: item.provinceId,
        label: item.nameProvince,
        key: item.provinceId,
    }));

    const optionsDistrict = districts?.map((item) => ({
        value: item.districtId,
        label: item.nameDistrict,
        key: item.districtId
    }));

    const uploadImage = (image) => {
        return new Promise((resolve, reject) => {
            const storageRef = ref(storage, `images/${v4()}`);
            const uploadTask = uploadBytesResumable(storageRef, image);
            uploadTask.on(
                'state_changed',
                (snapshot) => {
                    // Xử lý tiến trình tải lên
                },
                (error) => {
                    // Xử lý lỗi
                    console.log(error);
                    reject(error);
                },
                () => {
                    // Xử lý khi tải lên thành công
                    getDownloadURL(uploadTask.snapshot.ref)
                        .then((url) => {
                            console.log(url);
                            resolve(url);
                        })
                        .catch(error => reject(error));
                }
            );
        });
    }
    const handleUpload = (e) => {
        const files = e.target.files;
        if (files.length <= 4) {
            Promise.all(Array.from(files).map(file => uploadImage(file)))
                .then(urls => {
                    console.log(urls);
                    setImageUrls(urls);
                })
                .catch(error => console.log(error));
        } else {
            toast.error("Đăng tối đa 4 ảnh")
            e.target.value = null;
        }
    };

    const handleNext = (e) => {
        if (provinceId === null || districtId === null
            || address === null || imageUrls.length < 1
            || title.length < 20 || title.length > 50
            || acreage === 0 || price === 0
            || typeProperty === null || legalDocument === null
            || directionsProperty === null
            || horizontal === 0 || vertical === 0
        ) {
            toast.error("Yêu cầu điền thông tin đầy đủ!")
        } else {
            const data = {
                ...propertyDetails,
                provinceId: provinceId,
                nameProvince: nameProvince,
                districtId: districtId,
                nameDistrict: nameDistrict,
                address: address,

                position: position,
                imageUrls: imageUrls,
                title: title,
                description: description,
                acreage: acreage,
                price: price,
                pricePerM2: pricePerM2,
                typeProperty: typeProperty,
                legalDocument: legalDocument,
                directionsProperty: directionsProperty,
                horizontal: horizontal,
                vertical: vertical,
                floor: floor,
                bathroom: bathroom,
                bedroom: bedroom,
                parking: parking,
            }
            setPropertyDetails(data)
            setShowPost(true);
        }
    };

    const handleDeleteImage = async (image) => {
        try {
            const imageRef = ref(storage, image);
            await deleteObject(imageRef);
            const imagesCopy = imageUrls.filter((item) => item !== image);
            setImageUrls(imagesCopy);
            console.log("Ảnh đã được xóa thành công!");
        } catch (error) {
            console.log("Lỗi khi xóa ảnh:", error);
        }
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
            const response = await apiGetPublicDistrict(provinceId)
            if (response.status === 200) {
                const districts = response.data?.data;
                setDistricts(districts);
                const districtExists = districts.some(districtItem => {
                    if (districtItem.districtId === districtId) {
                        return true;
                    }
                    return false;
                });

                if (!districtExists) {
                    setDistrictId(null);
                    setNameDistrict(null);
                    setAddress('');
                }
            }
        }
        provinceId && fetchPublicDistrict()
    }, [provinceId])

    useEffect(() => {
        if (acreage > 0) {
            setPricePerM2(price / acreage);
        }
    }, [price])

    useEffect(() => {
        if (typeProperty === 'APARTMENT') {
            setFloor(1)
            setParking(1)
        }
        if (typeProperty === 'LAND') {
            setFloor(0);
            setBathroom(0)
            setBedroom(0)
            setParking(0)
        }
    }, [typeProperty]);

    return (
        <div>
            {/*  vi tri dia ly  */}
            <div
                className="flexCenter addLocation"
                style={{
                    justifyContent: "space-between",
                    gap: "3rem",
                    marginTop: "3rem",
                    flexdirectionsProperty: "row",
                }}
            >
                <div className="flexColStart" style={{flex: 1, gap: "1rem"}}>
                    <Select
                        w={"60%"}
                        withAsterisk
                        label="Tỉnh"
                        clearable
                        searchable
                        data={optionsProvince}
                        value={provinceId}
                        onChange={(value, option) => {
                            setProvinceId(option?.value)
                            setNameProvince(option?.label)
                        }}
                    />

                    <Select
                        w={"60%"}
                        withAsterisk
                        label="Huyện"
                        clearable
                        searchable
                        data={optionsDistrict}
                        onChange={(value, option) => {
                            setDistrictId(option?.value)
                            setNameDistrict(option?.label)
                        }}
                        value={districtId}
                    />
                    <TextInput
                        w={"60%"}
                        withAsterisk
                        clearable
                        label="Địa Chỉ"
                        value={address.toString()}
                        onChange={(event) => setAddress(event.currentTarget.value)}
                        error={address?.length > 30 ? "Nhập tối đa 30 ký tự" : ""}
                    />

                </div>

                <div style={{flex: 1}}>
                    <Map address={address} district={nameDistrict} province={nameProvince} position={position}
                         setPosition={setPosition}/>
                </div>
            </div>

            {/* them anh */}
            <div className="uploadWrapper flexCenter">

                <label className="flexColStart uploadZone" htmlFor="file">
                    <AiOutlineCloudUpload style={{marginLeft: "43%"}} size={50} color="grey"
                                          className="flexColCenter"/>
                </label>
                <input id="file" type="file" hidden onChange={handleUpload} accept="/image/*" multiple/>

                <div className="flexColEnd uploadView">
                    {imageUrls?.map((image, index) => {
                            return (
                                <div className="imageContainer" key={index + 1}>
                                    <img src={image} alt="" className="imageUpload"/>
                                    <span
                                        title='Xóa'
                                        onClick={() => handleDeleteImage(image)}
                                        className='deleteImage'
                                    >
                                                    <ImBin/>
                                </span>
                                </div>
                            )
                        }
                    )
                    }
                </div>
            </div>

            {/* tiêu đề và mô tả*/}
            <Box maw="50%" mx="auto" my="md">
                <TextInput
                    // w={"100%"}
                    withAsterisk
                    label="Tiêu đề"
                    value={title}
                    onChange={(event) => setTitle(event.currentTarget.value)}
                    style={{paddingBottom: "30px"}}
                    error={(title?.length > 50 || title?.length < 20) ? "Nhập 20 đến 50 ký tự" : ""}
                />

                <span>Mô tả</span>
                <CKEditor
                    style={{height: "300px"}}
                    editor={ClassicEditor}
                    data={description}
                    onReady={editor => {
                        // You can store the "editor" and use when it is needed.
                        console.log('Editor is ready to use!', editor);
                    }}
                    onChange={(event, editor) => {
                        // console.log( event );
                        setDescription(editor.getData())
                    }}
                    onBlur={(event, editor) => {
                        console.log('Blur.', editor);
                    }}
                    onFocus={(event, editor) => {
                        console.log('Focus.', editor);
                    }}
                />


                <NumberInput
                    withAsterisk
                    label="Diện tích"
                    min={0}
                    thousandSeparator=","
                    suffix=" m²"
                    value={acreage}
                    onChange={(value) => setAcreage(value)}
                />
                <NumberInput
                    withAsterisk
                    label="Giá"
                    min={0}
                    thousandSeparator=","
                    suffix=" VND"
                    value={price}
                    onChange={(value) => setPrice(value)}
                />
                <small style={{color: "red"}}>Giá bán là: {transferPrice(pricePerM2)}/m²</small>

                <Select
                    withAsterisk
                    label="Kiểu bất sản muốn bán"
                    data={[
                        {value: 'LAND', label: 'Đất'},
                        {value: 'HOUSE', label: 'Nhà Ở'},
                        {value: 'APARTMENT', label: 'Chung Cư'}
                    ]}
                    defaultValue='APARTMENT'
                    onChange={(value) => setTypeProperty(value)}
                />


                <Grid>
                    <Grid.Col span={6}>
                        <Select
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
                            value={directionsProperty}
                            onChange={(value) => setDirectionsProperty(value)}
                        />
                    </Grid.Col>

                    <Grid.Col span={6}>
                        <Select
                            withAsterisk
                            label="Trạng thái sổ đỏ"
                            data={[
                                {value: 'HAVE', label: 'Đã có sổ'},
                                {value: 'WAIT', label: 'Đang chờ'},
                                {value: 'HAVE_NOT', label: 'Không có'},
                            ]}
                            value={legalDocument}
                            onChange={(value) => setLegalDocument(value)}
                        />
                    </Grid.Col>

                    <Grid.Col span={6}>
                        <NumberInput
                            withAsterisk
                            label="Chiều dài"
                            min={0}
                            suffix=" mét"
                            value={horizontal}
                            onChange={(value) => setHorizontal(value)}
                            decimalScale={1}
                        />
                    </Grid.Col>

                    <Grid.Col span={6}>
                        <NumberInput
                            withAsterisk
                            label="Chiều rộng"
                            min={0}
                            suffix=" mét"
                            value={vertical}
                            onChange={(value) => setVertical(value)}
                            decimalScale={1}
                        />
                    </Grid.Col>
                </Grid>

                {typeProperty === 'APARTMENT' && (
                    <Grid>
                        <Grid.Col span={6}>
                            <NumberInput
                                withAsterisk
                                label="Số phòng ngủ"
                                min={0}
                                value={bedroom}
                                onChange={(value) => setBedroom(value)}
                                allowDecimal={false}
                            />
                        </Grid.Col>

                        <Grid.Col span={6}>
                            <NumberInput
                                withAsterisk
                                label="Số phòng tắm"
                                min={0}
                                value={bathroom}
                                onChange={(value) => setBathroom(value)}
                                allowDecimal={false}
                            />
                        </Grid.Col>

                    </Grid>
                )}

                {typeProperty === 'HOUSE' && (
                    <Grid>
                        <Grid.Col span={6}>
                            <NumberInput
                                withAsterisk
                                label="Số tầng"
                                min={0}
                                value={floor}
                                onChange={(value) => setFloor(value)}
                                allowDecimal={false}
                            />
                        </Grid.Col>


                        <Grid.Col span={6}>
                            <NumberInput
                                withAsterisk
                                label="Số phòng ngủ"
                                min={0}
                                value={bedroom}
                                onChange={(value) => setBedroom(value)}
                                allowDecimal={false}
                            />
                        </Grid.Col>


                        <Grid.Col span={6}>
                            <NumberInput
                                withAsterisk
                                label="Số phòng tắm"
                                min={0}
                                value={bathroom}
                                onChange={(value) => setBathroom(value)}
                                allowDecimal={false}
                            />
                        </Grid.Col>


                        <Grid.Col span={6}>
                            <NumberInput
                                withAsterisk
                                label="Có thể đậu bao nhiêu xe ô tô"
                                min={0}
                                value={parking}
                                onChange={(value) => setParking(value)}
                                allowDecimal={false}
                            />
                        </Grid.Col>
                    </Grid>
                )}

            </Box>

            <Button onClick={handleNext} className="flexCenter" style={{margin: "3rem 45%"}}>
                Xem Trước
            </Button>

            <ToastContainer
                position="top-left"
                autoClose={3000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
            />
        </div>
    )
}

export default AddInfoPostSell;