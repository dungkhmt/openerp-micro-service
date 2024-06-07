import "./ManagerPost.css";
import React, { useEffect, useState } from "react";
import PostSellRequest from "../../services/PostSellRequest";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import CardSell from "../../components/CardSell/CardSell";
import { ActionIcon, Button, Menu, Modal, rem, Tabs } from "@mantine/core";
import { IoIosMore } from "react-icons/io";
import { MdOutlineDone } from "react-icons/md";
import { FaTrash } from "react-icons/fa";
import { IoHammerOutline } from "react-icons/io5";
import { HiOutlineLockOpen } from "react-icons/hi2";
import { toast, ToastContainer } from "react-toastify";
import AddInfoPostSell from "../../components/AddInfoPostSell/AddInfoPostSell";
import InfoPostSell from "../../components/InfoPostSell/InfoPostSell";
import ContactBox from "../../components/ContactBox/ContactBox";
import { useDispatch, useSelector } from "react-redux";
import AccountRequest from "../../services/AccountRequest";
import { set_current_account } from "../../store/account";
import CardBuy from "../../components/CardBuy/CardBuy";
import { PostBuyRequest } from "../../services/PostBuyRequest";

const ManagerPost = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  // const { pathname } = useLocation();
  // const accountId = pathname.split("/").slice(-1)[0];
  const { accountId } = useParams();

  const currentAccount = useSelector((state) => state.account.currentData);
  const isOwnerPost = currentAccount.accountId == accountId;

  const [infoOwner, setInfoOwner] = useState({});
  const sellRequest = new PostSellRequest();
  const [listPostSell, setListPostSell] = useState([]);
  const [sellSelect, setSellSelect] = useState({});

  const buyRequest = new PostBuyRequest();
  const [listPostBuy, setListPostBuy] = useState([]);
  const [sellsFIt, setSellsFIt] = useState([]);
  const [buySelect, setBuySelect] = useState({});
  const [showPost, setShowPost] = useState(false);

  const handleFix = () => {
    setShowPost(false);
  };
  const handleChangeStatusSell = (status, postSellId) => {
    sellRequest
      .update_status({
        status: status,
        postId: postSellId,
      })
      .then((response) => {
        if (response.code === 200) {
          setListPostSell((prevListPostSell) =>
            prevListPostSell.map((post) =>
              post.postSellId === postSellId
                ? { ...post, postStatus: status }
                : post,
            ),
          );
          toast.success(response.data);
        } else {
          toast.error(response.data.message);
        }
      });
  };

  const handleChangeStatusBuy = (status, postBuyId) => {
    buyRequest
      .update_status({
        status: status,
        postId: postBuyId,
      })
      .then((response) => {
        if (response.code === 200) {
          setListPostBuy((prevListPostBuy) =>
            prevListPostBuy.map((post) =>
              post.postBuyId === postBuyId
                ? { ...post, postStatus: status }
                : post,
            ),
          );
          toast.success(response.data);
        } else {
          toast.error(response.data.message);
        }
      });
  };

  const handleUpdatePost = () => {
    sellRequest.updatePost(sellSelect).then((response) => {
      const statusCode = response.code;
      if (statusCode === 200) {
        toast.success(response.data);
        setSellSelect({});
        // return response.data;
      } else {
        toast.error(response.data.message);
      }
    });
  };

  const refreshPostSells = () => {
    sellRequest.get_post_by_accountId(accountId).then((response) => {
      if (response.code === 200) {
        setListPostSell(response.data);
      }
    });
  };

  const refreshPostBuys = () => {
    buyRequest.get_post_by_account_id(accountId).then((response) => {
      if (response.code === 200) {
        setListPostBuy(response.data);
      }
    });
  };

  const match_buy = (itemBuy) => {
    buyRequest
      .matching({
        postBuyId: itemBuy.postBuyId,
      })
      .then((response) => {
        if (response.code === 200 && response.data.length > 0) {
          setSellsFIt(response.data);
        } else {
          toast.error("Không có bài viết nào phù hợp");
        }
      });
  };
  const get_account = () => {
    const accountRequest = new AccountRequest();
    accountRequest
      .get_info_account_by({
        accountId,
      })
      .then((response) => {
        if (response.code === 200) {
          setInfoOwner(response.data);
          if (isOwnerPost) {
            dispatch(set_current_account(response.data));
          }
        } else {
          toast.error(response.data.message);
          navigate("/", { replace: true });
        }
      });
  };

  useEffect(() => {
    get_account();
    Object.keys(sellSelect).length === 0 && refreshPostSells();
    Object.keys(buySelect).length === 0 && refreshPostBuys();
  }, [accountId]);
  if (listPostSell.length === 0) {
    return (
      <div>
        <h2>Bạn chưa có bài viết nào</h2>
        <Link to="/add-post-sell">Thêm bài viết mới</Link>
      </div>
    );
  } else {
    return (
      <div className="list-post-sell-container">
        <Tabs
          style={{
            width: "70%",
            margin: "10px 20px 10px 10%",
            display: "flex",
            // backgroundColor: "aqua",
            position: "relative",
          }}
          defaultValue="sell"
          color="green"
          variant="pills"
          orientation="vertical"
        >
          <Tabs.List
            style={{
              position: "sticky",
              left: "5px",
              top: "10px",
              height: "100px",
              marginRight: "5px",
            }}
          >
            <Tabs.Tab value="sell">
              Bài viết bán ({listPostSell.length})
            </Tabs.Tab>
            <Tabs.Tab value="buy">Bài viết mua ({listPostBuy.length})</Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="sell">
            {listPostSell.map((item) => (
              <div
                key={item.postSellId}
                className="manager-post-sell-container"
              >
                <CardSell key={item.postSellId} item={item} />
                <div className="feature-more">
                  <Menu shadow="sm">
                    <Menu.Target>
                      <ActionIcon variant="subtle" color="gray">
                        <IoIosMore
                          style={{ margin: "0 auto" }}
                          className="icon"
                        />
                      </ActionIcon>
                    </Menu.Target>

                    <Menu.Dropdown>
                      {item.postStatus === "OPENING" ? (
                        <Menu.Item
                          onClick={() =>
                            handleChangeStatusSell("DONE", item.postSellId)
                          }
                          leftSection={
                            <MdOutlineDone
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                        >
                          Đã bán
                        </Menu.Item>
                      ) : (
                        <Menu.Item
                          onClick={() =>
                            handleChangeStatusSell("OPENING", item.postSellId)
                          }
                          leftSection={
                            <HiOutlineLockOpen
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                        >
                          Mở bán
                        </Menu.Item>
                      )}
                      <Menu.Item
                        onClick={() =>
                          handleChangeStatusSell("CLOSED", item.postSellId)
                        }
                        leftSection={
                          <FaTrash
                            style={{ width: rem(14), height: rem(14) }}
                          />
                        }
                      >
                        Xóa bài viết
                      </Menu.Item>
                      <Menu.Item
                        onClick={() => setSellSelect(item)}
                        leftSection={
                          <IoHammerOutline
                            style={{ width: rem(14), height: rem(14) }}
                          />
                        }
                        color="red"
                      >
                        Sửa
                      </Menu.Item>
                    </Menu.Dropdown>
                  </Menu>
                </div>
              </div>
            ))}
          </Tabs.Panel>
          <Tabs.Panel value="buy">
            {listPostBuy.map((item) => (
              <div key={item.postBuyId} className="manager-post-buy-container">
                <CardBuy item={item} />
                <div className="feature-more">
                  <Menu shadow="sm">
                    <Menu.Target>
                      <ActionIcon variant="subtle" color="gray">
                        <IoIosMore
                          style={{ margin: "0 auto" }}
                          className="icon"
                        />
                      </ActionIcon>
                    </Menu.Target>

                    <Menu.Dropdown>
                      {item.postStatus === "OPENING" ? (
                        <Menu.Item
                          onClick={() =>
                            handleChangeStatusBuy("DONE", item.postSellId)
                          }
                          leftSection={
                            <MdOutlineDone
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                        >
                          Đã tìm thấy
                        </Menu.Item>
                      ) : (
                        <Menu.Item
                          onClick={() =>
                            handleChangeStatusBuy("OPENING", item.postSellId)
                          }
                          leftSection={
                            <HiOutlineLockOpen
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                        >
                          Mở
                        </Menu.Item>
                      )}
                      <Menu.Item
                        onClick={() =>
                          handleChangeStatusBuy("CLOSED", item.postSellId)
                        }
                        leftSection={
                          <FaTrash
                            style={{ width: rem(14), height: rem(14) }}
                          />
                        }
                      >
                        Xóa bài viết
                      </Menu.Item>
                      <Menu.Item
                        onClick={() => match_buy(item)}
                        leftSection={
                          <IoHammerOutline
                            style={{ width: rem(14), height: rem(14) }}
                          />
                        }
                        color="red"
                      >
                        Khớp
                      </Menu.Item>
                    </Menu.Dropdown>
                  </Menu>
                </div>
              </div>
            ))}
          </Tabs.Panel>
        </Tabs>
        <div className="information-author-post">
          <ContactBox account={infoOwner} isOwner={isOwnerPost} />
        </div>

        <Modal
          opened={Object.keys(sellSelect).length > 0}
          onClose={() => setSellSelect({})}
          size={"90%"}
          title="Thay đổi thông tin"
        >
          {showPost && Object.keys(sellSelect).length > 0 && (
            <div>
              <InfoPostSell propertyDetails={sellSelect} />
              <div>
                <Button
                  style={{
                    margin: "0 15px",
                  }}
                  onClick={handleFix}
                >
                  Chỉnh sửa
                </Button>
                <Button onClick={handleUpdatePost}>Cập nhập</Button>
              </div>
            </div>
          )}

          {!showPost && Object.keys(sellSelect).length > 0 && (
            <AddInfoPostSell
              propertyDetails={sellSelect}
              setPropertyDetails={setSellSelect}
              setShowPost={setShowPost}
            />
          )}
        </Modal>

        <Modal
          opened={sellsFIt.length > 0}
          onClose={() => setSellsFIt([])}
          size={"90%"}
          title="Những bài viết phù hợp"
        >
          {sellsFIt.map((item) => (
            <CardSell key={item.postSellId} item={item} />
          ))}
        </Modal>

        <ToastContainer
          position="top-left"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
        />
      </div>
    );
  }
};

export default ManagerPost;
