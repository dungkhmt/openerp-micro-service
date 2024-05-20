import "./CardSell.css"
import React from "react";
import {Link} from "react-router-dom";
import {MdLocationPin} from "react-icons/md";
import {transferDirection, transferLegalDocument, transferTime, transferTypeProperty} from "../../utils/common";
import {GoLaw} from "react-icons/go";
import { RxHeight, RxWidth } from "react-icons/rx";
import {ImCompass2} from "react-icons/im";
import { FaPhoneVolume } from "react-icons/fa6";
import {transferPrice} from "../../utils/common";
import {Avatar, Grid} from "@mantine/core";
import {Phone} from "lucide-react";

const CardSell = ({item}) => {

    function capitalizeFirstLetterOfEachWord(string) {
        return string.split(' ').map(word => {
            return word.charAt(0).toUpperCase() + word.slice(1);
        }).join(' ');
    }


    return (
        <div className="card">
            <div className="infoPost">
                <div className="imageCard flexColCenter">
                    <Link to={`/sell/properties/${item.postSellId}`} style={{height: "100%", width: "100%"}}>
                        <img src={item.imageUrls[0]} alt="" className="flexColCenter"
                             style={{height: "100%", width: "100%", objectFit: "fill", borderRadius: "10px"}}
                        />
                    </Link>
                </div>
                <div className="textCard flexColStart"
                    // style={{width: "75%"}}
                >
                    <Link to={`/sell/properties/${item.postSellId}`} className="flexStart head">
                        <h2 className="titleCard">{item?.title.toUpperCase()}</h2>
                    </Link>

                    {/*<div className="flexStart">*/}
                    {/*    Kiểu tài sản: {transferTypeProperty(item?.typeProperty)}*/}
                    {/*</div>*/}
                    <div
                        className="flexStart facilities"
                        style={{fontSize: "20px"}}
                    >
                        <span className="price">
                            {transferPrice(item?.price)}
                        </span>
                        <span className="price">
                            {item?.acreage}m²
                        </span>

                    </div>

                    <div className="flexStart features">
                        <span className="">
                            {transferPrice(item?.pricePerM2)}/m²
                        </span>

                    </div>
                    <div className="flexStart addressCard">
                        <MdLocationPin size={20}/>
                        <span style={{fontWeight: "bold"}}>
                        {capitalizeFirstLetterOfEachWord(item?.address)}{" "}
                        {capitalizeFirstLetterOfEachWord(item?.district)}{" "}
                        {capitalizeFirstLetterOfEachWord(item?.province)}
                    </span>
                    </div>

                    <div className="flexStart facilities">
                        <div className="flexStart facility">
                            <RxHeight size={20} color="#1F3E72"/>
                            <span style={{}}>{item?.horizontal}m</span>
                        </div>
                        <div className="flexStart facility">
                            <RxWidth size={20} color="#1F3E72"/>
                            <span style={{}}>{item?.vertical}m</span>
                        </div>
                    </div>
                    <div className="flexStart facilities">
                        <div className="flexStart facility">
                            <GoLaw size={20} color="#1F3E72"/>
                            <span style={{}}> {transferLegalDocument(item?.legalDocuments)}</span>
                        </div>
                        <div className="flexStart facility">
                            <ImCompass2 size={20} color="#1F3E72"/>
                            <span style={{}}> {transferDirection(item?.directionsProperty)}</span>
                        </div>
                    </div>

                    {/* facilities */}
                    {/*{item?.typeProperty !== "Đất" &&*/}
                    {/*    (<div className="flexStart features">*/}
                    {/*        <div className="flexStart feature">*/}
                    {/*            <LuWarehouse size={20} color="#1F3E72"/>*/}
                    {/*            <span>{item?.floor} Tầng</span>*/}
                    {/*        </div>*/}
                    {/*        /!* bathrooms *!/*/}
                    {/*        <div className="flexStart feature">*/}
                    {/*            <FaShower size={20} color="#1F3E72"/>*/}
                    {/*            <span>{item?.bathroom} Phòng Tắm</span>*/}
                    {/*        </div>*/}

                    {/*        /!* parkings *!/*/}
                    {/*        <div className="flexStart feature">*/}
                    {/*            <AiTwotoneCar size={20} color="#1F3E72"/>*/}
                    {/*            <span>{item?.parking} Đỗ ô tô</span>*/}
                    {/*        </div>*/}

                    {/*        /!* rooms *!/*/}
                    {/*        <div className="flexStart feature">*/}
                    {/*            <MdMeetingRoom size={20} color="#1F3E72"/>*/}
                    {/*            <span>{item?.bedroom} Phòng ngủ</span>*/}
                    {/*        </div>*/}
                    {/*    </div>)*/}
                    {/*}*/}

                </div>
            </div>
            <div className="infoAuthor">
                <Grid w={"100%"}>
                    <Grid.Col span={"content"}>
                        <Link to="/">
                            <Avatar src={item.avatarAuthor} size="lg"/>
                        </Link>
                    </Grid.Col>
                    <Grid.Col span={6}>
                        <div>{item.nameAuthor}</div>
                        <div
                            style={{color: "#999", frontSize: "12px"}}
                        >{transferTime(item.createdAt)}
                        </div>
                    </Grid.Col>
                    <Grid.Col span={2}>
                        <div className="flexStart" style={{backgroundColor: "#007C80", textAlign: "center", alignItems: "center", height: "30px", margin: "5px"}}>
                            <FaPhoneVolume size={20} color="#1F3E72" style={{marginLeft: "5px"}}/>
                            <span style={{}}>{item.phoneAuthor}</span>
                        </div>
                    </Grid.Col>
                </Grid>
            </div>
        </div>
    )
}

export default CardSell;