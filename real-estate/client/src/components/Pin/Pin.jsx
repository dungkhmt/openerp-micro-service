import { Marker, Popup } from "react-leaflet";
import "./Pin.css";
import React from "react";
import { v4 as uuidv4 } from "uuid";
import { Icon } from "leaflet";
import { Link } from "react-router-dom";
import { transferPrice, transferTypeProperty } from "../../utils/common";

const Pin = ({ post }) => {
  const uniqueId = uuidv4();
  const customIcon = new Icon({
    iconUrl: require("../../images/placeholder.png"),
    iconSize: [20, 20],
  });
  return (
    <Marker id={uniqueId} position={post?.position} icon={customIcon}>
      <Popup>
        <div className="popupContainer">
          <img src={post?.imageUrls[0]} alt="" />
          <div className="textContainer">
            <Link to={`/sell/properties/${post.postSellId}`}>
              {transferTypeProperty(post.typeProperty)}
            </Link>
            <b>{transferPrice(post.price)}</b>
            <b>{post?.acreage} m²</b>
          </div>
        </div>
      </Popup>
    </Marker>
  );
};

export default Pin;
