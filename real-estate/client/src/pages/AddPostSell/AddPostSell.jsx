import React, { useState } from "react";
import { Button, Notification } from "@mantine/core";
import "./AddPostSell.css";
import AddInfoPostSell from "../../components/AddInfoPostSell/AddInfoPostSell";
import InfoPostSell from "../../components/InfoPostSell/InfoPostSell";
import { useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import PostSellRequest from "../../services/PostSellRequest";
const AddPostSell = () => {
  const navigate = useNavigate();
  const [propertyDetails, setPropertyDetails] = useState({
    position: [21, 105],

    provinceId: "",
    nameProvince: "",
    districtId: "",
    nameDistrict: "",
    address: "",

    imageUrls: [],

    title: "",
    description: "",

    acreage: 0,
    price: 0,
    pricePerM2: 0,
    typeProperty: "",
    directionProperty: "",
    bedroom: 0,
    parking: 0,
    bathroom: 0,
    floor: 0,
    legalDocument: "",
    horizontal: 0,
    vertical: 0,
  });

  const [showPost, setShowPost] = useState(false);

  const handleFix = () => {
    toast.success("Thêm thành công");
    setShowPost(false);
  };

  const handleDonePost = () => {
    const postRequest = new PostSellRequest();
    postRequest
      .addPostSell(propertyDetails)
      .then((response) => {
        const statusCode = response.code;
        if (statusCode === 200) {
          toast.success(response.data);
          // return response.data;
        } else {
          toast.error(response.data.message);
        }
      })
      .then((response) => {
        navigate("/", { replace: true });
      });
  };

  return (
    <div className="postSell-wrapper">
      <h1> Thêm thông tin bất động sản bạn muốn bán </h1>
      {showPost ? (
        <div>
          <InfoPostSell propertyDetails={propertyDetails} />
          <div>
            <Button onClick={handleFix}>Chỉnh sửa</Button>
            <Button onClick={handleDonePost}>Đăng bài</Button>
          </div>
        </div>
      ) : (
        <AddInfoPostSell
          propertyDetails={propertyDetails}
          setPropertyDetails={setPropertyDetails}
          setShowPost={setShowPost}
        />
      )}

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
  );
};

export default AddPostSell;