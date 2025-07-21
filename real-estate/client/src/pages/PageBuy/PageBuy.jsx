import "./PageBuy.css";
import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import { PostBuyRequest } from "../../services/PostBuyRequest";
import CardBuy from "../../components/CardBuy/CardBuy";
import { Spoiler, Image, LoadingOverlay, Loader } from "@mantine/core";
import { Link, useLocation, useParams } from "react-router-dom";
const PageBuy = () => {
  const location = useLocation();
  const queryParameters = new URLSearchParams(location.search);
  const provinceId = queryParameters.get("provinceId");
  const [listPost, setListPost] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [countPost, setCountPost] = useState([]);
  const changeItemBuy = (item) => {
    setListPost((prevListPost) =>
      prevListPost.map((post) =>
        post.postBuyId === item.postBuyId ? item : post,
      ),
    );
  };

  const getListPost = (params) => {
    setLoading(true);
    if (params.page <= totalPages) {
      const postRequest = new PostBuyRequest();
      postRequest
        .get_page(params)
        .then((response) => {
          if (response.code === 200) {
            if (params.page === 1) {
              setListPost([...response.data]);
            } else {
              setListPost((prevPosts) => [...prevPosts, ...response.data]);
            }
            setTotalPages(response.metadata.totalPages);
          } else {
            // setListPost([])
            toast.error(response.data.message);
          }
        })
        .then(() => {
          // Thêm delay 2 giây trước khi đặt setLoading(false)
          setTimeout(() => {
            setLoading(false);
          }, 2000);
        });
    } else {
      setLoading(false);
    }
  };

  const getCountPost = () => {
    const buyRequest = new PostBuyRequest();
    buyRequest.countPost().then((response) => {
      if (response.code === 200) {
        let totalPosts = 0;
        response.data.forEach((item) => (totalPosts += item.totalPost));
        setCountPost([
          {
            provinceId: "",
            nameProvince: "Tất cả",
            totalPost: totalPosts,
          },
          ...response.data,
        ]);
      }
    });
  };

  const handleScroll = () => {
    const scrollHeight = document.documentElement.scrollHeight;
    const scrollTop = document.documentElement.scrollTop;
    const clientHeight = document.documentElement.clientHeight;

    // console.log(scrollHeight, scrollTop, clientHeight)
    if (scrollTop + clientHeight >= scrollHeight && loading === false) {
      if (provinceId !== "") {
        getListPost({
          page: page + 1,
          size: 5,
          provinceId: provinceId,
        });
      } else {
        getListPost({
          page: page + 1,
          size: 5,
        });
      }

      setPage((prevPage) => prevPage + 1); // Lấy thêm 10 bài viết
    }
  };

  useEffect(() => {
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [handleScroll]);

  useEffect(() => {
    getCountPost();
    setPage(1);
    setTotalPages(1);
    if (provinceId !== "" && provinceId !== undefined) {
      getListPost({
        page: 1,
        size: 5,
        provinceId: provinceId,
      });
    } else {
      getListPost({
        page: 1,
        size: 5,
      });
    }
    // setTimeout(() => {
    //   if (provinceId !== "") {
    //     getListPost({
    //       page: 1,
    //       size: 5,
    //       provinceId: provinceId,
    //     });
    //   } else {
    //     getListPost({
    //       page: 1,
    //       size: 5,
    //     });
    //   }
    // }, 200);
  }, [provinceId]);

  return (
    <div className="page-buy-container">
      <div className="list-card-buy-container">
        {listPost?.map((item) => (
          <div key={item.postBuyId} className="card-buy-container">
            <Spoiler maxHeight={350} showLabel="Đọc thêm..." hideLabel="Ẩn...">
              <CardBuy
                key={item.postBuyId}
                item={item}
                changeItem={changeItemBuy}
              />
            </Spoiler>
          </div>
        ))}

        {loading ? (
          <Loader color="blue" size="xl" />
        ) : (
          <h2 color={"#f2f2f2"}>Đã hết bài viết</h2>
        )}
      </div>

      {countPost.length > 0 && (
        <div
          style={{
            position: "fixed",
            // height: "300px",
            // minHeight: "150px",
            width: "150px",
            top: "85px",
            right: "50px",
            border: "1px solid #F2F2F2",
            backgroundColor: "#f2f2f2",
          }}
          // className="flexColStart"
        >
          <h3>Khu Vực</h3>
          <Spoiler
            maxHeight={150}
            showLabel="Mở rộng"
            hideLabel="Thu gọn"
            transitionDuration={1}
          >
            {countPost.map((item, index) => (
              <div key={index} className="province">
                <Link to={"/buy/properties?provinceId=" + item.provinceId}>
                  {item.nameProvince} ({item.totalPost})
                </Link>
              </div>
            ))}
          </Spoiler>
        </div>
      )}
    </div>
  );
};

export default PageBuy;
