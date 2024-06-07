import "./CardSell.css";
import React from "react";
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
import { ImCompass2 } from "react-icons/im";
import { FaPhoneVolume } from "react-icons/fa6";
import { Avatar, Grid } from "@mantine/core";

const CardSell = ({ item }) => {
  function capitalizeFirstLetterOfEachWord(string) {
    return string
      ?.split(" ")
      .map((word) => {
        return word.charAt(0).toUpperCase() + word.slice(1);
      })
      .join(" ");
  }

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
              <ImCompass2 size={20} color="#1F3E72" />
              <span style={{}}>
                {" "}
                {transferDirection(item?.directionProperty)}
              </span>
            </div>
          </div>
        </div>
      </div>
      {item.avatarAuthor !== undefined && (
        <div className="infoAuthor">
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
                    // backgroundColor: "#007C80",
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
    </div>
  );
};

export default CardSell;
