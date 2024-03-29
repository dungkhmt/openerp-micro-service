import React, {useState} from 'react';
import {storage} from './FireBaseConfig';
import {v4} from 'uuid';
import {ref, uploadBytesResumable, getDownloadURL, deleteObject} from "firebase/storage";
import {AiOutlineCloudUpload} from "react-icons/ai";
import "./UploadImage.css"
import {Button, Group} from "@mantine/core";
import {ImBin} from "react-icons/im";

const ImageUploader = ({
                           propertyDetails,
                           setPropertyDetails,
                           nextStep,
                           prevStep,
                       }) => {

    const [imageUrls, setImageUrls] = useState(propertyDetails?.imageUrls)
    const handleNext = () => {
        setPropertyDetails((prev) => ({...prev, images: imageUrls}));
        nextStep();
    };
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
            alert("Chỉ được phép tải lên tối đa 4 tệp.");
            e.target.value = null;
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
    return (
        <div className="uploadContainer">
            <div className="uploadWrapper flexCenter">

                <label className="flexColStart uploadZone" htmlFor="file">
                    <AiOutlineCloudUpload style={{marginLeft: "40%"}} size={50} color="grey" className="flexColCenter"/>
                </label>
                <input id="file" type="file" hidden onChange={handleUpload} accept="/image/*" multiple/>

                <div className="flexColEnd uploadView">
                    {imageUrls?.map((image, index) => {
                        return (
                            <div className="imageContainer" key={index + 1}>
                                <img src={image} alt=""/>
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
            <Group position="center" mt={"xl"}>
                <Button variant="default" onClick={prevStep}>
                    Back
                </Button>
                <Button onClick={handleNext} disabled={!imageUrls}>
                    Next
                </Button>
            </Group>
        </div>

    );
};

export default ImageUploader;