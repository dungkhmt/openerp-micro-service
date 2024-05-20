import "./InfoPostSell.css"
import ImageSlider from "../ImageSlider/ImageSlider";
import {MdLocationPin, MdMeetingRoom} from "react-icons/md";
import {LuWarehouse} from "react-icons/lu";
import { GoLaw } from "react-icons/go";
import { ImCompass2 } from "react-icons/im";
import MarkerMap from "../MarkerMap/MarkerMap";
import {FaShower} from "react-icons/fa";
import {AiTwotoneCar} from "react-icons/ai";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import {CKEditor} from "@ckeditor/ckeditor5-react";
import React from "react";
import {transferDirection, transferLegalDocument, transferPrice, transferTypeProperty} from "../../utils/common";

const InfoPostSell = ({propertyDetails}) => {

    return (
    <div className="singlePage">
        <div className="details">
            <div className="wrapper">
                <ImageSlider images={propertyDetails?.imageUrls}/>
                <div className="flexCenter property-details">
                    <div className="flexColStart left" style={{display: "flex", width: "100%", height: "100%"}}>
                        <div className="flexStart head">
                            <h2 className="primaryText">{propertyDetails?.title}</h2>
                        </div>

                        <div className="flexStart row-event">Kiểu tài sản: {transferTypeProperty(propertyDetails?.typeProperty)}</div>
                        <div className="flexStart facilities">
                            <span className="">
                                Giá: {transferPrice(propertyDetails?.price)}
                            </span>

                            <span className="">
                                {transferPrice(propertyDetails.pricePerM2)}/m²
                            </span>
                        </div>

                        <div className="flexStart row-event facilities">
                            <span style={{}}>
                                Diện tích: {propertyDetails?.acreage}m²
                            </span>
                            <span style={{}}>
                                Chiều dài: {propertyDetails?.horizontal}m
                            </span>
                            <span style={{}}>
                                Chiều rộng: {propertyDetails?.vertical}m
                            </span>
                        </div>
                        <div className="flexStart">
                            <MdLocationPin size={25}/>
                            <span style={{fontWeight: "bold"}}>
                                {propertyDetails?.address}{" "}
                                {propertyDetails?.district}{" "}
                                {propertyDetails?.province}
                            </span>
                        </div>


                        <div className="flexStart facilities row-event">
                            <div className="flexStart facility">
                                <GoLaw size={20} color="#1F3E72"/>
                                <span style={{}}> {transferLegalDocument(propertyDetails?.legalDocuments)}</span>
                            </div>
                            <div className="flexStart facility">
                                <ImCompass2 size={20} color="#1F3E72"/>
                                <span style={{}}> Hướng {transferDirection(propertyDetails?.directionsProperty)}</span>
                            </div>
                        </div>

                        {/* facilities */}
                        {propertyDetails?.typeProperty !== "Đất" &&
                            (<div className="flexStart facilities">
                                <div className="flexStart facility">
                                    <LuWarehouse size={20} color="#1F3E72"/>
                                    <span>{propertyDetails?.floor} Floor</span>
                                </div>
                                {/* bathrooms */}
                                <div className="flexStart facility">
                                    <FaShower size={20} color="#1F3E72"/>
                                    <span>{propertyDetails?.bathroom} Bathroom</span>
                                </div>

                                {/* parkings */}
                                <div className="flexStart facility">
                                    <AiTwotoneCar size={20} color="#1F3E72"/>
                                    <span>{propertyDetails?.parking} Parking</span>
                                </div>

                                {/* rooms */}
                                <div className="flexStart facility">
                                    <MdMeetingRoom size={20} color="#1F3E72"/>
                                    <span>{propertyDetails?.bedroom} Room</span>
                                </div>
                            </div>)
                        }

                    </div>

                    <div className="map">
                        <MarkerMap posts={[propertyDetails,]}/>
                    </div>
                </div>

                <div>
                    <h2>Mô tả bất động sản</h2>
                    <CKEditor
                        style={{height:"300px"}}
                        editor={ ClassicEditor }
                        data = {propertyDetails?.description}
                        disabled={true}
                        onReady={ editor => {
                            // You can store the "editor" and use when it is needed.
                            // editor.editing.view.change((writer) => {
                            //     writer.setStyle(
                            //         'width',
                            //         "200px",
                            //         editor.editing.view.document.getRoot()
                            //     )
                            // })

                            console.log( 'Editor is ready to use!', editor );
                        } }
                        onChange={ ( event, editor ) => {
                            // console.log( event );
                            // setDescription(editor.getData())
                        } }
                        onBlur={ ( event, editor ) => {
                            console.log( 'Blur.', editor );
                        } }
                        onFocus={ ( event, editor ) => {
                            console.log( 'Focus.', editor );
                        } }
                    />
                </div>
            </div>
        </div>
    </div>);
}

export default InfoPostSell;