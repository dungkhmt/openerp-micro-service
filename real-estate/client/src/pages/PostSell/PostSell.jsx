import React, {useState} from "react";
import {Button, Notification} from "@mantine/core";
import "./PostSell.css"
import AddInfoPostSell from "../../components/AddInfoPostSell/AddInfoPostSell";
import InfoPostSell from "../../components/PostBuyDetail/InfoPostSell";
import PostRequest from "../../services/PostRequest";
import {useNavigate} from "react-router-dom";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
const PostSell = () => {
    const navigate = useNavigate();
    const [propertyDetails, setPropertyDetails] = useState({
        position: [21, 105],
        province: "",
        district: "",
        address: "",

        imageUrls: [],

        title: "",
        description: "",

        acreage: 0,
        price: 0,
        pricePerM2: 0,
        typeProperty: "",
        directionsProperty: "",
        bedroom: 0,
        parking: 0,
        bathroom: 0,
        floor: 0,
        legalDocuments: "",
        horizontal: 0,
        vertical: 0,

    });

    const [showPost, setShowPost] = useState(false);
    const [messageNotifi, setMessageNotifi] = useState("");

    console.log("gia tri truyen", propertyDetails)

    const handleFix = () => {
        toast.success("Theem thanh cong")
        setShowPost(false);
    }

    const handleDonePose = () => {
        const postRequest = new PostRequest();
        postRequest.addPostSell(propertyDetails)
            .then(response => {
                const statusCode = response.code;
                if (statusCode === 200) {
                    toast.success("Đăng bán thành công")
                    return response.data;
                } else {
                    toast.error(response.data.message)
                }
            })
            .then(response => {
                navigate("/", {replace: true});
            })
        ;
    }

    return (
        <div className="postSell-wrapper">
            <h1> Thêm thông tin bất động sản bạn muốn bán </h1>
            {showPost ?
                (
                    <div>
                        <InfoPostSell propertyDetails={propertyDetails}/>
                        <div>
                            <Button onClick={handleFix}>
                                Chỉnh sửa
                            </Button>
                            <Button onClick={handleDonePose}>
                                Đăng bài
                            </Button>
                        </div>
                    </div>

                ) :
                <AddInfoPostSell propertyDetails={propertyDetails}
                                 setPropertyDetails={setPropertyDetails}
                                 setShowPost={setShowPost}/>
            }

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

export default PostSell;