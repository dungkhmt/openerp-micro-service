import {Box, Button, NumberInput, Select, TextInput} from "@mantine/core";
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

const AddInfoPostSell = ({propertyDetails, setPropertyDetails, setShowPost}) => {

    const [provinces, setProvinces] = useState([])
    const [districts, setDistricts] = useState([])
    const [province, setProvince] = useState(propertyDetails?.province || null)
    const [district, setDistrict] = useState(propertyDetails?.district || null)
    const [address, setAddress] = useState(propertyDetails?.address || null)
    const [position, setPosition] = useState(propertyDetails?.position || [])


    // them anh
    const [imageUrls, setImageUrls] = useState(propertyDetails?.imageUrls || [])

    // thong tin chi tiet
    const [title, setTitle] = useState(propertyDetails?.title || null);
    const [description, setDescription] = useState(propertyDetails?.description || null);
    const [typeProperty, setTypeProperty] = useState(propertyDetails?.typeProperty || null);
    const [directionsProperty, setDirectionsProperty] = useState(propertyDetails?.directionsProperty || null);
    const [acreage, setAcreage] = useState(propertyDetails?.acreage || 0);
    const [bedroom, setBedroom] = useState(propertyDetails?.bedroom || 0);
    const [bathroom, setBathroom] = useState(propertyDetails?.bathroom || 0);
    const [parking, setParking] = useState(propertyDetails?.parking) || 0;
    const [floor, setFloor] = useState(propertyDetails?.floor || 0)
    const [legalDocuments, setLegalDocuments] = useState(propertyDetails?.legalDocuments || null)
    const [horizontal, setHorizontal] = useState(propertyDetails?.horizontal || 0);
    const [vertical, setVertical] = useState(propertyDetails?.vertical || 0)
    const [price, setPrice] = useState(propertyDetails?.price || 0)
    const [pricePerM2, setPricePerM2] = useState(propertyDetails?.pricePerM2 || 0)
    const [isLand, setIsLand] = useState(false)

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
        e.preventDefault();

        setPropertyDetails({
            province: province,
            district: district,
            address: address,
            position: position,
            imageUrls: imageUrls,
            title: title,
            description: description,
            acreage: acreage,
            price: price,
            pricePerM2: pricePerM2,
            typeProperty: typeProperty,
            legalDocuments: legalDocuments,
            directionsProperty: directionsProperty,
            horizontal: horizontal,
            vertical: vertical,
            floor: floor,
            bathroom: bathroom,
            bedroom: bedroom,
            parking: parking,
        })
        setShowPost(true);
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
    // phan logic vitri
    // lay danh sach cac tinh
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
        setAddress("");
    }, [province])

    useEffect(() => {
        if (acreage > 0) {
            setPricePerM2(price / acreage);
        }
    }, [price])

    useEffect(() => {
        if (typeProperty === 'LAND') {
            setFloor(0);
            setBathroom(0)
            setBedroom(0)
            setParking(0)
            setIsLand(true);
        } else {
            setIsLand(false)
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
                    <TextInput
                        w={"100%"}
                        withAsterisk
                        clearable
                        label="Address"
                        value={address}
                        onChange={(event) => setAddress(event.currentTarget.value)}
                        error={address?.length > 30 ? "Nhập tối đa 30 ký tự" : ""}
                    />

                </div>


                <div style={{flex: 1}}>
                    <Map address={address} district={district} province={province} position={position}
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
                    error={title?.length > 50 ? "Nhập tối đa 50 ký tự" : ""}
                />
                {/*<Textarea*/}
                {/*    placeholder="Description"*/}
                {/*    label="Mô tả"*/}
                {/*    withAsterisk*/}
                {/*    value={description}*/}
                {/*    onChange={(event) => setDescription(event.currentTarget.value)}*/}
                {/*/>*/}

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
                    placeholder="1000"
                    min={0}
                    thousandSeparator=","
                    suffix="m2"
                    value={acreage}
                    onChange={(value) => setAcreage(value)}
                />
                <NumberInput
                    withAsterisk
                    label="Giá"
                    placeholder="1000"
                    min={0}
                    thousandSeparator=","
                    suffix="VND"
                    value={price}
                    onChange={(value) => setPrice(value)}
                />
                <small>Giá bán trên m<sup>2</sup> đất đang là: ${pricePerM2.toLocaleString('us-US')} VND</small>

                <Select
                    withAsterisk
                    label="Kiểu bất sản muốn bán"
                    data={[
                        {value: 'LAND', label: 'Đất'},
                        {value: 'HOUSE', label: 'Nhà ở'},
                        {value: 'APARTMENT', label: 'Chung cư'}
                    ]}
                    onChange={(value) => setTypeProperty(value)}
                />

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
                    onChange={(value) => setDirectionsProperty(value)}
                />

                <Select
                    withAsterisk
                    label="Trạng thái sổ đỏ"
                    data={[
                        {value: 'HAVE', label: 'Đã có sổ'},
                        {value: 'WAIT', label: 'Đang chờ'},
                        {value: 'HAVE_NOT', label: 'Không có'},
                    ]}
                    value={legalDocuments}
                    onChange={(value) => setLegalDocuments(value)}
                />

                <NumberInput
                    withAsterisk
                    label="Chiều dài"
                    min={0}
                    suffix=" mét"
                    value={horizontal}
                    onChange={(value) => setHorizontal(value)}
                    decimalScale={1}
                />

                <NumberInput
                    withAsterisk
                    label="Chiều rộng"
                    min={0}
                    suffix=" mét"
                    value={vertical}
                    onChange={(value) => setVertical(value)}
                    decimalScale={1}
                />

                <NumberInput
                    withAsterisk
                    label="Số tầng"
                    min={0}
                    value={floor}
                    onChange={(value) => setFloor(value)}
                    allowDecimal={false}
                    disabled={isLand}
                />

                <NumberInput
                    withAsterisk
                    label="Số phòng ngủ"
                    min={0}
                    value={bedroom}
                    onChange={(value) => setBedroom(value)}
                    allowDecimal={false}
                    disabled={isLand}
                />

                <NumberInput
                    withAsterisk
                    label="Số phòng tắm"
                    min={0}
                    value={bathroom}
                    onChange={(value) => setBathroom(value)}
                    allowDecimal={false}
                    disabled={isLand}
                />
                <NumberInput
                    withAsterisk
                    label="Có thể đậu bao nhiêu xe ô tô"
                    min={0}
                    value={parking}
                    onChange={(value) => setParking(value)}
                    allowDecimal={false}
                    disabled={isLand}
                />


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