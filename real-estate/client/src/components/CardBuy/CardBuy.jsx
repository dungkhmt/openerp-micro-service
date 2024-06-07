import { CKEditor } from "@ckeditor/ckeditor5-react";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import { Avatar, Grid, SimpleGrid } from "@mantine/core";
import {
  MdOutlineHomeWork,
  MdOutlineMail,
  MdOutlineSquareFoot,
} from "react-icons/md";
import { TbCoin } from "react-icons/tb";
import {
  transferDirections,
  transferLegalDocuments,
  transferPrice,
  transferTime,
} from "../../utils/common";
import { GrDirections } from "react-icons/gr";
import { PiArrowsHorizontal, PiBathtub } from "react-icons/pi";
import { BsArrowsVertical } from "react-icons/bs";
import { IoDocumentOutline } from "react-icons/io5";
import { FaBed } from "react-icons/fa";
import { FaCarSide, FaPhoneVolume } from "react-icons/fa6";
import React from "react";
import { Link } from "react-router-dom";

const CardBuy = ({ item }) => {
  return (
    <div
      style={{
        width: "90%",
        margin: "0 auto",
      }}
    >
      <div className="infoAuthor">
        <Grid w={"100%"}>
          <Grid.Col span={"content"}>
            <Link to="/">
              <Avatar src={item.avatarAuthor} size="lg" />
            </Link>
          </Grid.Col>
          <Grid.Col span={4}>
            <div>{item.nameAuthor}</div>
            <div style={{ color: "#999", frontSize: "12px" }}>
              {transferTime(item.createdAt)}
            </div>
          </Grid.Col>
          <Grid.Col span={6}>
            {item.phoneAuthor !== null && (
              <div
                className="flexStart"
                style={{
                  // backgroundColor: "#007C80",
                  textAlign: "center",
                  alignItems: "center",
                }}
              >
                <FaPhoneVolume className="icon" />
                <span>{item.phoneAuthor}</span>
              </div>
            )}
            {item.emailAuthor !== null && (
              <a
                href={"mailto:" + item.emailAuthor}
                className="flexStart"
                style={{
                  // backgroundColor: "#007C80",
                  textAlign: "center",
                  alignItems: "center",
                }}
              >
                <MdOutlineMail className="icon" />
                <span>{item.emailAuthor}</span>
              </a>
            )}
          </Grid.Col>
        </Grid>
      </div>

      <h2
        style={{
          fontWeight: "600",
          marginBottom: "5px",
        }}
      >
        {item?.title.toUpperCase()}
      </h2>
      <h3>Thông tin mô tả</h3>
      <CKEditor
        style={{ height: "300px" }}
        editor={ClassicEditor}
        data={item?.description}
        disabled={true}
        onReady={(editor) => {
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
          console.log("Blur.", editor);
        }}
        onFocus={(event, editor) => {
          console.log("Focus.", editor);
        }}
      />

      <h2>Tỉnh: {item.nameProvince}</h2>
      <h3>Huyện: {item.nameDistricts.join(", ")}</h3>
      <div
        style={{
          margin: "15px 0",
          padding: "5px 0",
          borderTop: "1px solid #F2F2F2",
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
                <MdOutlineSquareFoot className="icon" />
                <span>Diện tích</span>
              </div>
              <div>
                {item?.maxAcreage === 0
                  ? "Trên " + item?.minAcreage
                  : item?.minAcreage + " - " + item?.maxAcreage}
                m²
              </div>
            </SimpleGrid>
            <SimpleGrid cols={2} className="border-top-bottom flexStart">
              <div className="flexStart">
                <TbCoin className="icon" />
                <span>Mức giá</span>
              </div>
              <div>
                {item?.maxPrice === 0
                  ? "Trên " + transferPrice(item?.minPrice)
                  : transferPrice(item?.minPrice) +
                    " - " +
                    transferPrice(item?.maxPrice)}
              </div>
            </SimpleGrid>
            <SimpleGrid cols={2} className="border-top-bottom flexStart">
              <div className="flexStart">
                <GrDirections className="icon" />
                <span>Hướng</span>
              </div>
              <div
                style={{
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  width: "100%",
                }}
                title={transferDirections(item?.directionProperties).join(", ")}
              >
                {transferDirections(item?.directionProperties).join(", ")}
              </div>
            </SimpleGrid>
            <SimpleGrid cols={2} className="border-top-bottom flexStart">
              <div className="flexStart">
                <BsArrowsVertical className="icon" />
                <span>Chiều dài</span>
              </div>
              <div>
                {item?.minHorizontal > 0
                  ? item?.minHorizontal + "m"
                  : "Không yêu cầu"}
              </div>
            </SimpleGrid>
            <SimpleGrid cols={2} className="border-top-bottom flexStart">
              <div className="flexStart">
                <PiArrowsHorizontal className="icon" />
                <span>Chiều rộng</span>
              </div>
              <div>
                {item?.minVertical > 0
                  ? item?.minVertical + "m"
                  : "Không yêu cầu"}
              </div>
            </SimpleGrid>

            <SimpleGrid
              cols={2}
              className="border-top-bottom flexStart"
              style={{
                borderBottom: "1px solid #F2F2F2",
              }}
            >
              <div className="flexStart">
                <IoDocumentOutline className="icon" />
                <span>Pháp lý</span>
              </div>
              <div
                style={{
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  width: "100%",
                }}
                title={transferLegalDocuments(item?.legalDocuments).join(", ")}
              >
                {transferLegalDocuments(item?.legalDocuments).join(", ")}
              </div>
            </SimpleGrid>
          </Grid.Col>
          {item?.typeProperty === "APARTMENT" && (
            <Grid.Col span={6}>
              <SimpleGrid cols={2} className="border-top-bottom flexStart">
                <div className="flexStart">
                  <PiBathtub className="icon" />
                  <span>Phòng tắm</span>
                </div>
                <div>
                  {item?.minBathroom > 0
                    ? item?.minBathroom + " phòng"
                    : "Không yêu cầu"}
                </div>
              </SimpleGrid>
              <SimpleGrid
                cols={2}
                className="border-top-bottom flexStart"
                style={{
                  borderBottom: "1px solid #F2F2F2",
                }}
              >
                <div className="flexStart">
                  <FaBed className="icon" />
                  <span>Phòng ngủ</span>
                </div>
                <div>
                  {item?.minBedroom > 0
                    ? item?.minBedroom + " phòng"
                    : "Không yêu cầu"}
                </div>
              </SimpleGrid>
            </Grid.Col>
          )}

          {item?.typeProperty === "HOUSE" && (
            <Grid.Col span={6}>
              <SimpleGrid cols={2} className="border-top-bottom flexStart">
                <div className="flexStart">
                  <MdOutlineHomeWork className="icon" />
                  <span>Số tầng</span>
                </div>
                <div>
                  {item?.minFloor > 0
                    ? item?.minFloor + " tầng"
                    : "Không yêu cầu"}
                </div>
              </SimpleGrid>
              <SimpleGrid cols={2} className="border-top-bottom flexStart">
                <div className="flexStart">
                  <PiBathtub className="icon" />
                  <span>Phòng tắm</span>
                </div>
                <div>
                  {item?.minBathroom > 0
                    ? item?.minBathroom + " phòng"
                    : "Không yêu cầu"}
                </div>
              </SimpleGrid>
              <SimpleGrid cols={2} className="border-top-bottom flexStart">
                <div className="flexStart">
                  <FaBed className="icon" />
                  <span>Phòng ngủ</span>
                </div>
                <div>
                  {item?.minBedroom > 0
                    ? item?.minBedroom + " phòng"
                    : "Không yêu cầu"}
                </div>
              </SimpleGrid>
              <SimpleGrid
                cols={2}
                className="border-top-bottom flexStart"
                style={{
                  borderBottom: "1px solid #F2F2F2",
                }}
              >
                <div className="flexStart">
                  <FaCarSide className="icon" />
                  <span>Chỗ đậu xe</span>
                </div>
                <div>
                  {item?.minParking > 0
                    ? item?.minParking + " chỗ"
                    : "Không yêu cầu"}
                </div>
              </SimpleGrid>
            </Grid.Col>
          )}
        </Grid>
      </div>
    </div>
  );
};

export default CardBuy;
