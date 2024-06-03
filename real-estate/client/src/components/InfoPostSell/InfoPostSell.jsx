import "./InfoPostSell.css"
import ImageSlider from "../ImageSlider/ImageSlider";
import {MdOutlineHomeWork, MdOutlineSquareFoot} from "react-icons/md";
import MarkerMap from "../MarkerMap/MarkerMap";
import {FaBed} from "react-icons/fa";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import {CKEditor} from "@ckeditor/ckeditor5-react";
import React from "react";
import {
    capitalizeFirstLetterOfEachWord,
    transferDirection,
    transferLegalDocument,
    transferPrice,
    transferTypeProperty
} from "../../utils/common";
import {Grid, SimpleGrid} from "@mantine/core";
import {TbCoin} from "react-icons/tb";
import {GrDirections} from "react-icons/gr";
import {IoDocumentOutline} from "react-icons/io5";
import {PiArrowsHorizontal, PiBathtub} from "react-icons/pi";
import {BsArrowsVertical} from "react-icons/bs";
import {FaCarSide} from "react-icons/fa6";

const InfoPostSell = ({propertyDetails}) => {
    console.log("ben show", propertyDetails)

    return (
        <div className="singlePage">
            <div className="details">
                <ImageSlider images={propertyDetails?.imageUrls}/>

                <div className="wrapper">
                    <h1
                        style={{
                            fontWeight: "600"
                        }}
                    >
                        {propertyDetails?.title.toUpperCase()}
                    </h1>
                    <div
                        style={{
                            fontSize: "14px", color: "#2C2C2C", marginTop: "8px",
                            borderBottom: "1px solid #F2F2F2",
                        }}
                    >
                        {capitalizeFirstLetterOfEachWord(
                            propertyDetails?.address + ", " +
                            propertyDetails?.nameDistrict + ", " +
                            propertyDetails?.nameProvince
                        )}
                    </div>
                    <Grid w={"50%"}
                          style={{
                              padding: "15px 0",
                              fontSize: "14px",
                              borderBottom: "1px solid #F2F2F2",
                          }}
                    >
                        <Grid.Col span={4}>
                            <div
                                style={{
                                    color: "#999"
                                }}
                            >
                                Mức giá
                            </div>
                            <div
                                style={{
                                    color: "#E03C31"
                                }}
                            >
                                {transferPrice(propertyDetails?.price)} ~{transferPrice(propertyDetails?.pricePerM2)}/m²
                            </div>
                        </Grid.Col>
                        <Grid.Col span={4}>
                            <div
                                style={{
                                    color: "#999"
                                }}
                            >
                                Diện tích
                            </div>
                            <div
                                style={{
                                    color: "#E03C31"
                                }}
                            >
                                {propertyDetails?.acreage} m²
                            </div>
                        </Grid.Col>
                        <Grid.Col span={4}>
                            <div
                                style={{
                                    color: "#999"
                                }}
                            >
                                Kiểu bất động sản
                            </div>
                            <div>
                                {transferTypeProperty(propertyDetails?.typeProperty)}
                            </div>
                        </Grid.Col>
                    </Grid>

                    <h3>Thông tin mô tả</h3>
                    <CKEditor
                        style={{height: "300px"}}
                        editor={ClassicEditor}
                        data={propertyDetails?.description}
                        disabled={true}
                        onReady={editor => {
                            // You can store the "editor" and use when it is needed.
                            // editor.editing.view.change((writer) => {
                            //     writer.setStyle(
                            //         'width',
                            //         "200px",
                            //         editor.editing.view.document.getRoot()
                            //     )
                            // })

                            // console.log('Editor is ready to use!', editor);
                        }}
                        onChange={(event, editor) => {
                            // console.log( event );
                            // setDescription(editor.getData())
                        }}
                        onBlur={(event, editor) => {
                            console.log('Blur.', editor);
                        }}
                        onFocus={(event, editor) => {
                            console.log('Focus.', editor);
                        }}
                    />

                    <div
                        style={{
                            margin: "15px 0",
                            padding: "5px 0",
                            borderTop: "1px solid #F2F2F2"
                        }}
                    >
                        <h3
                            style={{
                                padding: "15px 0",
                            }}
                        >
                            Đặc điểm bất động sản
                        </h3>

                        <Grid w={"100%"}>
                            <Grid.Col
                                span={6}
                                // style={{
                                //     borderBottom: "1px solid #F2F2F2"
                                // }}
                            >
                                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                    <div className="flexStart">
                                        <MdOutlineSquareFoot className="icon"/>
                                        <span>Diện tích</span>
                                    </div>
                                    <div>
                                        {propertyDetails?.acreage} m²
                                    </div>
                                </SimpleGrid>
                                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                    <div className="flexStart">
                                        <TbCoin className="icon"/>
                                        <span>Mức giá</span>
                                    </div>
                                    <div>
                                        {transferPrice(propertyDetails?.price)}
                                    </div>
                                </SimpleGrid>
                                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                    <div className="flexStart">
                                        <GrDirections className="icon"/>
                                        <span>Hướng</span>
                                    </div>
                                    <div>
                                        {transferDirection(propertyDetails?.directionProperty)}
                                    </div>
                                </SimpleGrid>
                                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                    <div className="flexStart">
                                        <BsArrowsVertical className="icon"/>
                                        <span>Chiều dài</span>
                                    </div>
                                    <div>
                                        {propertyDetails?.horizontal} m
                                    </div>
                                </SimpleGrid>
                                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                    <div className="flexStart">
                                        <PiArrowsHorizontal className="icon"/>
                                        <span>Chiều rộng</span>
                                    </div>
                                    <div>
                                        {propertyDetails?.vertical} m
                                    </div>
                                </SimpleGrid>
                                <SimpleGrid cols={2} className="border-top-bottom flexStart"
                                            style={{
                                                borderBottom: "1px solid #F2F2F2"
                                            }}
                                >
                                    <div className="flexStart">
                                        <IoDocumentOutline className="icon"/>
                                        <span>Pháp lý</span>
                                    </div>
                                    <div>
                                        {transferLegalDocument(propertyDetails?.legalDocument)}
                                    </div>
                                </SimpleGrid>
                            </Grid.Col>
                            {
                                propertyDetails?.typeProperty === "APARTMENT" && (
                                    <Grid.Col span={6}>
                                        <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                            <div className="flexStart">
                                                <PiBathtub className="icon"/>
                                                <span>Phòng tắm</span>
                                            </div>
                                            <div>
                                                {propertyDetails?.bathroom} phòng
                                            </div>
                                        </SimpleGrid>
                                        <SimpleGrid cols={2} className="border-top-bottom flexStart"
                                                    style={{
                                                        borderBottom: "1px solid #F2F2F2"
                                                    }}
                                        >
                                            <div className="flexStart">
                                                <FaBed className="icon"/>
                                                <span>Phòng ngủ</span>
                                            </div>
                                            <div>
                                                {propertyDetails?.bedroom} phòng
                                            </div>
                                        </SimpleGrid>
                                    </Grid.Col>

                                )
                            }

                            {
                                propertyDetails?.typeProperty === "HOUSE" && (
                                    <Grid.Col span={6}>
                                        <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                            <div className="flexStart">
                                                <MdOutlineHomeWork className="icon"/>
                                                <span>Số tầng</span>
                                            </div>
                                            <div>
                                                {propertyDetails?.floor} tầng
                                            </div>
                                        </SimpleGrid>
                                        <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                            <div className="flexStart">
                                                <PiBathtub className="icon"/>
                                                <span>Phòng tắm</span>
                                            </div>
                                            <div>
                                                {propertyDetails?.bathroom} phòng
                                            </div>
                                        </SimpleGrid>
                                        <SimpleGrid cols={2} className="border-top-bottom flexStart">
                                            <div className="flexStart">
                                                <FaBed className="icon"/>
                                                <span>Phòng ngủ</span>
                                            </div>
                                            <div>
                                                {propertyDetails?.bedroom} phòng
                                            </div>
                                        </SimpleGrid>
                                        <SimpleGrid cols={2} className="border-top-bottom flexStart"
                                                    style={{
                                                        borderBottom: "1px solid #F2F2F2"
                                                    }}
                                        >
                                            <div className="flexStart">
                                                <FaCarSide className="icon"/>
                                                <span>Chỗ đậu xe</span>
                                            </div>
                                            <div>
                                                {propertyDetails?.parking} chỗ
                                            </div>
                                        </SimpleGrid>
                                    </Grid.Col>
                                )
                            }
                        </Grid>


                    </div>
                </div>

                <div
                    style={{
                        width: '80%',
                        height: "400px",
                        margin: "10px auto"
                    }}
                >
                    <MarkerMap posts={[propertyDetails,]}/>
                </div>
            </div>
        </div>
    );
}

export default InfoPostSell;