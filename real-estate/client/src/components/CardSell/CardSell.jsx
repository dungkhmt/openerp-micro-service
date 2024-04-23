import "./CardSell.css"
import React from "react";
import {Link} from "react-router-dom";
import {LuWarehouse} from "react-icons/lu";
import {FaShower} from "react-icons/fa";
import {AiTwotoneCar} from "react-icons/ai";
import {MdLocationPin, MdMeetingRoom} from "react-icons/md";
import {transferDirection, transferLegalDocument, transferTypeProperty} from "../../utils/common";
import {GoLaw} from "react-icons/go";
import {ImCompass2} from "react-icons/im";
import "./CardSell.css"
const CardSell = ({item}) => {

    function capitalizeFirstLetterOfEachWord(string) {
        return string.split(' ').map(word => {
            return word.charAt(0).toUpperCase() + word.slice(1);
        }).join(' ');
    }


    return (
        <div className="card">
            <div className="imageCard flexColCenter">
                <Link to={`/sell/properties/${item.postSellId}`} style={{height: "100%" ,width: "100%"}}>
                    <img src={item.imageUrls[0]} alt="" className="flexColCenter"
                        style={{height: "100%",width: "100%", objectFit:"fill", borderRadius: "10px"}}
                    />
                </Link>
            </div>
            <div className="textCard flexColStart"
                 // style={{width: "75%"}}
            >
                <Link to={`/sell/properties/${item.postSellId}`} className="flexStart head">
                    <h2 className="titleCard">{item?.title}</h2>
                </Link>

                <div className="flexStart">
                    Kiểu tài sản: {transferTypeProperty(item?.typeProperty)}
                </div>
                <div className="flexStart facilities">
                    <span className="price">
                        Giá: {item?.price.toLocaleString('us-US')} VND
                    </span>

                    <span className="price">
                        {item?.pricePerM2.toLocaleString('us-US')} VND/m<sup>2</sup>
                    </span>
                </div>

                <div className="flexStart features">
                    <span className="feature">
                        Diện tích: {item?.acreage}m<sup>2</sup>
                    </span>
                    <span className="feature">
                        Chiều dài: {item?.horizontal}m
                    </span>
                    <span className="feature">
                        Chiều rộng: {item?.vertical}m
                    </span>
                </div>
                <div className="flexStart addressCard">
                    <MdLocationPin size={25}/>
                    <span style={{fontWeight: "bold"}}>
                        {capitalizeFirstLetterOfEachWord(item?.address)}{" "}
                        {capitalizeFirstLetterOfEachWord(item?.district)}{" "}
                        {capitalizeFirstLetterOfEachWord(item?.province)}
                    </span>
                </div>


                <div className="flexStart facilities">
                    <div className="flexStart facility">
                        <GoLaw size={20} color="#1F3E72"/>
                        <span style={{}}> {transferLegalDocument(item?.legalDocuments)}</span>
                    </div>
                    <div className="flexStart facility">
                        <ImCompass2 size={20} color="#1F3E72"/>
                        <span style={{}}> Hướng {transferDirection(item?.directionsProperty)}</span>
                    </div>
                </div>

                {/* facilities */}
                {item?.typeProperty !== "Đất" &&
                    (<div className="flexStart features">
                        <div className="flexStart feature">
                            <LuWarehouse size={20} color="#1F3E72"/>
                            <span>{item?.floor} Tầng</span>
                        </div>
                        {/* bathrooms */}
                        <div className="flexStart feature">
                            <FaShower size={20} color="#1F3E72"/>
                            <span>{item?.bathroom} Phòng Tắm</span>
                        </div>

                        {/* parkings */}
                        <div className="flexStart feature">
                            <AiTwotoneCar size={20} color="#1F3E72"/>
                            <span>{item?.parking} Đỗ ô tô</span>
                        </div>

                        {/* rooms */}
                        <div className="flexStart feature">
                            <MdMeetingRoom size={20} color="#1F3E72"/>
                            <span>{item?.bedroom} Phòng ngủ</span>
                        </div>
                    </div>)
                }

            </div>
        </div>
    )
}

export default CardSell;