import { CKEditor } from "@ckeditor/ckeditor5-react";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import { ActionIcon, Avatar, Divider, Grid, SimpleGrid } from "@mantine/core";
import {
  MdOutlineHomeWork,
  MdOutlineMail,
  MdOutlineSquareFoot,
} from "react-icons/md";
import { TbCoin } from "react-icons/tb";
import {
  transferColorPostStatus,
  transferDirections,
  transferLegalDocuments,
  transferPostStatus,
  transferPrice,
  transferTime,
} from "../../utils/common";
import { GrDirections } from "react-icons/gr";
import { PiArrowsHorizontal, PiBathtub } from "react-icons/pi";
import { BsArrowsVertical } from "react-icons/bs";
import { IoDocumentOutline } from "react-icons/io5";
import { FaBed } from "react-icons/fa";
import { FaCarSide, FaPhoneVolume } from "react-icons/fa6";
import React, { useContext, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { IconHeart } from "@tabler/icons-react";
import LikeRequest from "../../services/LikeRequest";
import { AccountContext } from "../../context/AccountContext";
import { toast } from "react-toastify";

const CardBuy = ({ item, changeItem }) => {
  const [isLike, setIsLike] = useState();
  const { account } = useContext(AccountContext);

  const like = () => {
    if (item?.postBuyId > 0 && Object.keys(account).length > 0) {
      const likeRequest = new LikeRequest();
      likeRequest
        .createLike({
          postId: item.postBuyId,
          typePost: "BUY",
        })
        .then((response) => {
          if (response.code === 200) {
            setIsLike(true);
            changeItem({ ...item, likeId: response.data });
          }
        });
    } else {
      toast.error("Hãy đăng nhập để thích bài viết");
    }
  };
  const deleteLike = () => {
    const likeRequest = new LikeRequest();
    likeRequest.deleteLike({ likeId: item.likeId }).then((response) => {
      if (response.code === 200) {
        setIsLike(false);
        changeItem({ ...item, likeId: 0 });
      }
    });
  };

  useEffect(() => {
    setIsLike(item?.likeId > 0);
  }, [item?.likeId]);
  return (
    <div
      style={{
        width: "100%",
        margin: "0 auto",
        padding: "0 20px 10px 20px",
        position: "relative",
      }}
    >
      <div className="infoAuthor">
        <Grid w={"100%"} style={{ margin: "10px 0" }}>
          <Grid.Col span={"content"}>
            <Link to={"/manager-post/" + item.authorId}>
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
                target="_blank"
                className="flexStart"
                style={{
                  // backgroundColor: "#007C80",
                  textAlign: "center",
                  alignItems: "center",
                }}
                rel="noreferrer"
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
          marginBottom: "10px",
          letterSpacing: "1px",
          // color: "yellow",
        }}
      >
        {item?.title.toUpperCase()}
      </h2>
      {/*<h3>Thông tin mô tả</h3>*/}

      <Divider label={"Đặc điểm bất động sản"} my="md" />

      <h2>Tỉnh/ Thành phố: {item.nameProvince}</h2>
      <h3>Quận/ Huyện: {item.nameDistricts.join(", ")}</h3>
      <div
        style={{
          margin: "15px 0",
          padding: "5px 0",
          // borderTop: "1px solid #F2F2F2",
        }}
      >
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
      <Divider label={"Yêu cầu khác"} my="md" />

      <CKEditor
        style={{ height: "300px" }}
        editor={ClassicEditor}
        data={item?.description}
        disabled={true}
        onReady={(editor) => {}}
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

      <div
        className="postStatus"
        style={{ backgroundColor: transferColorPostStatus(item.postStatus) }}
      >
        {transferPostStatus(item.postStatus)}
      </div>
      <div
        style={{
          position: "absolute",
          top: "0",
          right: "0",
        }}
      >
        {isLike ? (
          <ActionIcon
            variant="filled"
            color="red"
            size="xs"
            onClick={deleteLike}
          >
            <IconHeart />
          </ActionIcon>
        ) : (
          <ActionIcon variant="outline" color="red" size="xs" onClick={like}>
            <IconHeart />
          </ActionIcon>
        )}
      </div>
    </div>
  );
};

export default CardBuy;
