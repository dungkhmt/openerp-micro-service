import "./CardSell.css";
import React, { useContext, useState } from "react";
import { Link } from "react-router-dom";
import { MdLocationPin, MdOutlineMail } from "react-icons/md";
import {
  transferColorPostStatus,
  transferDirection,
  transferLegalDocument,
  transferPostStatus,
  transferPrice,
  transferTime,
} from "../../utils/common";
import { GoLaw } from "react-icons/go";
import { RxHeight, RxWidth } from "react-icons/rx";
import { FaPhoneVolume } from "react-icons/fa6";
import { ActionIcon, Avatar, Grid } from "@mantine/core";
import { GrDirections } from "react-icons/gr";
import { IconHeart } from "@tabler/icons-react";
import SaveRequest from "../../services/SaveRequest";
import { AccountContext } from "../../context/AccountContext";
import { toast } from "react-toastify";

const CardSell = ({ item, changeItem }) => {
  function capitalizeFirstLetterOfEachWord(string) {
    return string
      ?.split(" ")
      .map((word) => {
        return word.charAt(0).toUpperCase() + word.slice(1);
      })
      .join(" ");
  }

  const [isSave, setIsSave] = useState(item?.saveId > 0);
  const { account } = useContext(AccountContext);

  const save = () => {
    if (item?.postSellId > 0 && Object.keys(account).length > 0) {
      const saveRequest = new SaveRequest();
      saveRequest
        .createSave({
          postId: item.postSellId,
          typePost: "SELL",
        })
        .then((response) => {
          if (response.code === 200) {
            setIsSave(true);
            changeItem({ ...item, saveId: response.data });
          }
        });
    } else {
      toast.error("Hãy đăng nhập để thích bài viết");
    }
  };

  const deleteSave = () => {
    const saveRequest = new SaveRequest();
    saveRequest.deleteSave({ saveId: item.saveId }).then((response) => {
      if (response.code === 200) {
        setIsSave(false);
        changeItem({ ...item, saveId: 0 });
      }
    });
  };
  return (
    <div className="card">
      <div className="infoPost">
        <div className="imageCard flexColCenter">
          <Link
            to={`/sell/properties/${item.postSellId}`}
            style={{ height: "100%", width: "100%" }}
          >
            <img
              src={item.imageUrls[0]}
              alt=""
              className="flexColCenter"
              style={{
                height: "100%",
                width: "100%",
                objectFit: "fill",
                borderRadius: "10px",
              }}
            />
          </Link>
        </div>
        <div
          className="textCard flexColStart"
          // style={{width: "75%"}}
        >
          <Link
            to={`/sell/properties/${item.postSellId}`}
            className="flexStart head"
          >
            <h3 className="titleCard">{item?.title.toUpperCase()}</h3>
          </Link>

          <div className="flexStart facilities" style={{ fontSize: "20px" }}>
            <span className="price">{transferPrice(item?.price)}</span>
            <span className="price">{item?.acreage}m²</span>
          </div>

          <div className="flexStart features">
            <span className="">{transferPrice(item?.pricePerM2)}/m²</span>
          </div>
          <div className="flexStart addressCard">
            <MdLocationPin size={20} />
            <span style={{ fontWeight: "bold" }}>
              {capitalizeFirstLetterOfEachWord(item?.address)}{" "}
              {capitalizeFirstLetterOfEachWord(item?.nameDistrict)}{" "}
              {capitalizeFirstLetterOfEachWord(item?.nameProvince)}
            </span>
          </div>

          <div className="flexStart facilities">
            <div className="flexStart facility">
              <RxHeight size={20} color="#1F3E72" />
              <span style={{}}>{item?.horizontal}m</span>
            </div>
            <div className="flexStart facility">
              <RxWidth size={20} color="#1F3E72" />
              <span style={{}}>{item?.vertical}m</span>
            </div>
          </div>
          <div className="flexStart facilities">
            <div className="flexStart facility">
              <GoLaw size={20} color="#1F3E72" />
              <span style={{}}>
                {" "}
                {transferLegalDocument(item?.legalDocument)}
              </span>
            </div>
            <div className="flexStart facility">
              <GrDirections size={20} color="#1F3E72" />
              <span style={{}}>
                {" "}
                {transferDirection(item?.directionProperty)}
              </span>
            </div>
          </div>
        </div>
      </div>
      {item.avatarAuthor !== undefined && (
        <div className="infoAuthor flexCenter">
          <Grid w={"100%"}>
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
                <div
                  className="flexStart"
                  style={{
                    textAlign: "center",
                    alignItems: "center",
                  }}
                >
                  <MdOutlineMail className="icon" />
                  <span>{item.emailAuthor}</span>
                </div>
              )}
            </Grid.Col>
          </Grid>
        </div>
      )}
      <div
        className="postStatus"
        style={{ backgroundColor: transferColorPostStatus(item.postStatus) }}
      >
        {transferPostStatus(item.postStatus)} Bán
      </div>
      <div
        style={{
          position: "absolute",
          top: "0",
          right: "0",
        }}
      >
        {isSave ? (
          <ActionIcon
            variant="filled"
            color="red"
            size="xs"
            onClick={deleteSave}
          >
            <IconHeart />
          </ActionIcon>
        ) : (
          <ActionIcon variant="outline" color="red" size="xs" onClick={save}>
            <IconHeart />
          </ActionIcon>
        )}
      </div>
    </div>
  );
};

export default CardSell;
