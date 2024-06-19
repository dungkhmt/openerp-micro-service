import "./InfoPostSell.css";
import ImageSlider from "../ImageSlider/ImageSlider";
import {
  MdLocationPin,
  MdOutlineHomeWork,
  MdOutlineSquareFoot,
} from "react-icons/md";
import MarkerMap from "../MarkerMap/MarkerMap";
import { FaBed } from "react-icons/fa";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import React, { useContext, useEffect, useState } from "react";
import {
  capitalizeFirstLetterOfEachWord,
  transferDirection,
  transferLegalDocument,
  transferPrice,
  transferTypeProperty,
} from "../../utils/common";
import { ActionIcon, Grid, SimpleGrid } from "@mantine/core";
import { TbCoin } from "react-icons/tb";
import { GrDirections } from "react-icons/gr";
import { IoDocumentOutline } from "react-icons/io5";
import { PiArrowsHorizontal, PiBathtub } from "react-icons/pi";
import { BsArrowsVertical } from "react-icons/bs";
import { FaCarSide } from "react-icons/fa6";
import { IconHeart } from "@tabler/icons-react";
import LikeRequest from "../../services/LikeRequest";
import { toast } from "react-toastify";
import { AccountContext } from "../../context/AccountContext";

const InfoPostSell = ({ item }) => {
  console.log(item);
  const [likeId, setLikeId] = useState(0);
  const { account } = useContext(AccountContext);

  const like = () => {
    if (item?.postSellId > 0 && Object.keys(account).length > 0) {
      const likeRequest = new LikeRequest();
      likeRequest
        .createLike({
          postId: item.postSellId,
          typePost: "SELL",
        })
        .then((response) => {
          if (response.code === 200) {
            setLikeId(response.data);
          }
        });
    } else {
      toast.error("Hãy đăng nhập để thích bài viết");
    }
  };
  const deleteLike = () => {
    const likeRequest = new LikeRequest();
    likeRequest.deleteLike({ likeId }).then((response) => {
      if (response.code === 200) {
        setLikeId(0);
      }
    });
  };
  useEffect(() => {
    const getLikeId = () => {
      const likeRequest = new LikeRequest();
      likeRequest
        .getLikeId({
          postId: item.postSellId,
          typePost: "SELL",
        })
        .then((response) => {
          setLikeId(response.data);
        });
    };
    if (Object.keys(account).length > 0 && item?.postSellId > 0) {
      getLikeId();
    }
  }, []);

  return (
    <div className="singlePage">
      <div className="details">
        <ImageSlider images={item?.imageUrls} />

        <div className="wrapper">
          <h1
            style={{
              fontWeight: "600",
            }}
          >
            {item?.title.toUpperCase()}
          </h1>
          <div
            style={{
              fontSize: "14px",
              color: "#2C2C2C",
              marginTop: "8px",
              borderBottom: "1px solid #F2F2F2",
            }}
          >
            <MdLocationPin className="icon" />
            {capitalizeFirstLetterOfEachWord(
              item?.address +
                ", " +
                item?.nameDistrict +
                ", " +
                item?.nameProvince,
            )}
          </div>
          <Grid
            w={"100%"}
            style={{
              padding: "15px 0 0 0",
              fontSize: "14px",
              // borderBottom: "1px solid #F2F2F2",
            }}
          >
            <Grid.Col span={3}>
              <div
                style={{
                  color: "#999",
                }}
              >
                Mức giá
              </div>
              <div
                style={{
                  color: "#E03C31",
                }}
              >
                {transferPrice(item?.price)} ~{transferPrice(item?.pricePerM2)}
                /m²
              </div>
            </Grid.Col>
            <Grid.Col span={3}>
              <div
                style={{
                  color: "#999",
                }}
              >
                Diện tích
              </div>
              <div
                style={{
                  color: "#E03C31",
                }}
              >
                {item?.acreage} m²
              </div>
            </Grid.Col>
            <Grid.Col span={3}>
              <div
                style={{
                  color: "#999",
                }}
              >
                Kiểu bất động sản
              </div>
              <div
                style={{
                  color: "#E03C31",
                }}
              >
                {transferTypeProperty(item?.typeProperty)}
              </div>
            </Grid.Col>
            <Grid.Col span={3}>
              <div
                style={{
                  color: "#999",
                }}
              >
                Thích tin
              </div>
              <div>
                {likeId > 0 ? (
                  <ActionIcon
                    variant="filled"
                    color="red"
                    size="xs"
                    onClick={deleteLike}
                  >
                    <IconHeart />
                  </ActionIcon>
                ) : (
                  <ActionIcon
                    variant="outline"
                    color="red"
                    size="xs"
                    onClick={like}
                  >
                    <IconHeart />
                  </ActionIcon>
                )}
              </div>
            </Grid.Col>
          </Grid>

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
                  <div>{item?.acreage} m²</div>
                </SimpleGrid>
                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                  <div className="flexStart">
                    <TbCoin className="icon" />
                    <span>Mức giá</span>
                  </div>
                  <div>{transferPrice(item?.price)}</div>
                </SimpleGrid>
                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                  <div className="flexStart">
                    <GrDirections className="icon" />
                    <span>Hướng</span>
                  </div>
                  <div>{transferDirection(item?.directionProperty)}</div>
                </SimpleGrid>
                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                  <div className="flexStart">
                    <BsArrowsVertical className="icon" />
                    <span>Chiều dài</span>
                  </div>
                  <div>{item?.horizontal} m</div>
                </SimpleGrid>
                <SimpleGrid cols={2} className="border-top-bottom flexStart">
                  <div className="flexStart">
                    <PiArrowsHorizontal className="icon" />
                    <span>Chiều rộng</span>
                  </div>
                  <div>{item?.vertical} m</div>
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
                  <div>{transferLegalDocument(item?.legalDocument)}</div>
                </SimpleGrid>
              </Grid.Col>
              {item?.typeProperty === "APARTMENT" && (
                <Grid.Col span={6}>
                  <SimpleGrid cols={2} className="border-top-bottom flexStart">
                    <div className="flexStart">
                      <PiBathtub className="icon" />
                      <span>Phòng tắm</span>
                    </div>
                    <div>{item?.bathroom} phòng</div>
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
                    <div>{item?.bedroom} phòng</div>
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
                    <div>{item?.floor} tầng</div>
                  </SimpleGrid>
                  <SimpleGrid cols={2} className="border-top-bottom flexStart">
                    <div className="flexStart">
                      <PiBathtub className="icon" />
                      <span>Phòng tắm</span>
                    </div>
                    <div>{item?.bathroom} phòng</div>
                  </SimpleGrid>
                  <SimpleGrid cols={2} className="border-top-bottom flexStart">
                    <div className="flexStart">
                      <FaBed className="icon" />
                      <span>Phòng ngủ</span>
                    </div>
                    <div>{item?.bedroom} phòng</div>
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
                    <div>{item?.parking} chỗ</div>
                  </SimpleGrid>
                </Grid.Col>
              )}
            </Grid>
          </div>

          <h3>Thông tin khác</h3>
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
        </div>

        <div
          style={{
            width: "80%",
            height: "400px",
            margin: "10px auto",
            zIndex: "3",
          }}
        >
          <MarkerMap posts={[item]} />
        </div>
      </div>
    </div>
  );
};

export default InfoPostSell;
