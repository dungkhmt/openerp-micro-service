import React, { useEffect, useState } from "react";
import {
  Button,
  Grid,
  Notification,
  Select,
  Stepper,
  TextInput,
  Group,
  rem,
  ActionIcon,
  NumberInput,
  Box,
} from "@mantine/core";
import "./AddPostSell.css";
import InfoPostSell from "../../components/InfoPostSell/InfoPostSell";
import { useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import PostSellRequest from "../../services/PostSellRequest";
import Map from "../../components/Map/Map";
import DistrictRequest from "../../services/DistrictRequest";
import { AiOutlineCloudUpload } from "react-icons/ai";
import { IoClose } from "react-icons/io5";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import { transferPrice, uploadImage } from "../../utils/common";
const AddPostSell = () => {
  const navigate = useNavigate();

  const [isLoadingUpload, setIsLoadingUpload] = useState(false);

  const [post, setPost] = useState({});

  const [active, setActive] = useState(0);
  const [highestStepVisited, setHighestStepVisited] = useState(active);

  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [provinceId, setProvinceId] = useState();
  const [nameProvince, setNameProvince] = useState();
  const [districtId, setDistrictId] = useState();
  const [nameDistrict, setNameDistrict] = useState();
  const [address, setAddress] = useState("");
  const [position, setPosition] = useState([21.0, 105]);

  const [imagesVirtual, setImagesVirtual] = useState({
    files: [],
    images: [],
  });

  const [title, setTitle] = useState();
  const [description, setDescription] = useState();
  const [typeProperty, setTypeProperty] = useState("LAND");
  const [directionProperty, setDirectionProperty] = useState(null);
  const [acreage, setAcreage] = useState(0);
  const [bedroom, setBedroom] = useState(0);
  const [bathroom, setBathroom] = useState(0);
  const [parking, setParking] = useState(0);
  const [floor, setFloor] = useState(0);
  const [legalDocument, setLegalDocument] = useState();
  const [horizontal, setHorizontal] = useState(0);
  const [vertical, setVertical] = useState(0);
  const [price, setPrice] = useState(0);
  const [pricePerM2, setPricePerM2] = useState(0);

  const handleUpload = (e) => {
    const files = Array.from(e.target.files);
    if (files.length <= 4) {
      const images = files.map((file) => {
        return URL.createObjectURL(file);
      });
      setImagesVirtual({
        files: files,
        images: images,
      });
    } else {
      toast.error("Đăng tối đa 4 ảnh");
      e.target.value = null;
    }
  };

  const handleDeleteImage = (index) => {
    setImagesVirtual((prevState) => {
      let files = [...prevState.files]; // Tạo bản sao của mảng files
      files.splice(index, 1); // Xóa phần tử tại vị trí index

      const urlVirtual = files.map((file) => {
        return URL.createObjectURL(file);
      });

      return {
        files: files,
        images: urlVirtual,
      };
    });
  };

  const optionsProvince = provinces?.map((item) => ({
    value: item.provinceId,
    label: item.nameProvince,
    key: item.provinceId,
  }));

  const optionsDistrict = districts?.map((item) => ({
    value: item.districtId,
    label: item.nameDistrict,
    key: item.districtId,
  }));

  const handleStepChange = (nextStep) => {
    const isOutOfBounds = nextStep > 3 || nextStep < 0;

    if (isOutOfBounds) {
      return;
    }

    setActive(nextStep);
    setHighestStepVisited((hSC) => Math.max(hSC, nextStep));
  };

  // Allow the user to freely go back and forth between visited steps.
  const shouldAllowSelectStep = (step) =>
    highestStepVisited >= step && active !== step;
  const nextStep1 = () => {
    if (
      provinceId !== undefined &&
      districtId !== undefined &&
      address.length > 0 &&
      address.length < 30
    ) {
      handleStepChange(active + 1);
    } else {
      toast.info("Điền đầy đủ thông tin");
    }
  };

  const nextStep2 = () => {
    if (imagesVirtual.images.length > 0) {
      handleStepChange(active + 1);
    } else {
      toast.info("Tải từ 1 đến 4 ảnh");
    }
  };

  const nextStep3 = () => {
    if (
      title?.length >= 20 &&
      title?.length <= 70 &&
      description?.length > 0 &&
      acreage > 0 &&
      price > 0 &&
      typeProperty !== undefined &&
      directionProperty !== undefined &&
      legalDocument !== undefined &&
      horizontal > 0 &&
      vertical > 0
    ) {
      setPost({
        provinceId: provinceId,
        nameProvince: nameProvince,
        districtId: districtId,
        nameDistrict: nameDistrict,
        address: address,

        position: position,
        imageUrls: imagesVirtual.images,
        title: title,
        description: description,
        acreage: acreage,
        price: price,
        pricePerM2: pricePerM2,
        typeProperty: typeProperty,
        legalDocument: legalDocument,
        directionProperty: directionProperty,
        horizontal: horizontal,
        vertical: vertical,
        floor: floor,
        bathroom: bathroom,
        bedroom: bedroom,
        parking: parking,
      });

      handleStepChange(active + 1);
    } else {
      toast.info("Điền đầy đủ thông tin");
    }
  };

  const nextStep4 = () => {
    setIsLoadingUpload(true);
    Promise.all(imagesVirtual.files.map((file) => uploadImage(file)))
      .then((urls) => {
        console.log(urls);
        const data = { ...post, imageUrls: urls };
        handleDonePost(data);
      })
      .catch((error) => console.log(error))
      .finally(() => setIsLoadingUpload(false));
  };

  const handleDonePost = (data) => {
    const postRequest = new PostSellRequest();
    postRequest
      .addPostSell(data)
      .then((response) => {
        const statusCode = response.code;
        if (statusCode === 200) {
          toast.success(response.data);
          // return response.data;
        } else {
          toast.error(response.data.message);
        }
      })
      .then((response) => {
        navigate("/", { replace: true });
      });
  };

  useEffect(() => {
    const fetchPublicProvince = () => {
      const districtRequest = new DistrictRequest();
      districtRequest.get_province().then((response) => {
        if (response.code === 200) {
          setProvinces(response.data);
        }
      });
    };
    fetchPublicProvince();
  }, []);

  useEffect(() => {
    const fetchPublicDistrict = () => {
      const districtRequest = new DistrictRequest();
      districtRequest
        .get_districts({
          provinceId,
        })
        .then((response) => {
          if (response.code === 200) {
            const districts = response.data;
            setDistricts(districts);
            const districtExists = districts.some((districtItem) => {
              if (districtItem.districtId === districtId) {
                return true;
              }
              return false;
            });

            if (!districtExists) {
              setDistrictId(null);
              setNameDistrict(null);
              setAddress("");
            }
          }
        });
    };
    provinceId && fetchPublicDistrict();
  }, [provinceId]);

  useEffect(() => {
    if (acreage > 0) {
      setPricePerM2(price / acreage);
    }
  }, [price]);

  useEffect(() => {
    if (typeProperty === "APARTMENT") {
      setFloor(1);
      setParking(1);
    }
    if (typeProperty === "LAND") {
      setFloor(0);
      setBathroom(0);
      setBedroom(0);
      setParking(0);
    }
  }, [typeProperty]);
  return (
    <div className="postSell-wrapper">
      <Stepper
        active={active}
        onStepClick={setActive}
        // allowNextStepsSelect={false}
      >
        <Stepper.Step
          label="Khu vực"
          allowStepSelect={shouldAllowSelectStep(0)}
        >
          <Grid>
            <Grid.Col
              span={{ base: 12, md: 6 }}
              // style={{ backgroundColor: "blue" }}
            >
              <div
                className="flexColCenter flexCenter"
                style={{
                  width: "100%",
                  height: "100%",
                }}
              >
                <Select
                  w={"60%"}
                  withAsterisk
                  placeholder="Tỉnh / Thành phố"
                  clearable
                  searchable
                  data={optionsProvince}
                  value={provinceId}
                  onChange={(value, option) => {
                    setProvinceId(option?.value);
                    setNameProvince(option?.label);
                  }}
                />

                <Select
                  w={"60%"}
                  withAsterisk
                  placeholder="Quận / Huyện"
                  clearable
                  searchable
                  data={optionsDistrict}
                  onChange={(value, option) => {
                    setDistrictId(option?.value);
                    setNameDistrict(option?.label);
                  }}
                  value={districtId}
                />
                <TextInput
                  w={"60%"}
                  withAsterisk
                  clearable
                  placeholder="Địa Chỉ"
                  value={address}
                  onChange={(event) => setAddress(event.currentTarget.value)}
                  error={address?.length > 30 ? "Nhập tối đa 30 ký tự" : ""}
                />
              </div>
            </Grid.Col>

            <Grid.Col span={{ base: 12, md: 6 }}>
              <div style={{ width: "100%", height: "100%" }}>
                <Map
                  address={address}
                  district={nameDistrict}
                  province={nameProvince}
                  position={position}
                  setPosition={setPosition}
                />
              </div>
            </Grid.Col>
          </Grid>

          <Group
            w={"100%"}
            style={{
              margin: "10px 0",
            }}
            justify="center"
            mt={"xl"}
          >
            <Button onClick={nextStep1}>Sau</Button>
          </Group>
        </Stepper.Step>

        <Stepper.Step label="Ảnh" allowStepSelect={shouldAllowSelectStep(1)}>
          <div className="uploadWrapper flexCenter">
            <label className="flexColStart uploadZone" htmlFor="file">
              <AiOutlineCloudUpload
                style={{ margin: "0 auto" }}
                size={50}
                color="grey"
                className="flexColCenter"
              />
            </label>
            <input
              id="file"
              type="file"
              hidden
              onChange={handleUpload}
              accept="/image/*"
              multiple
            />

            <div className="flexColEnd uploadView">
              <Grid
                w={"100%"}
                style={{
                  width: "100%",
                  height: "100%",
                }}
              >
                {imagesVirtual?.images.map((image, index) => {
                  return (
                    <Grid.Col
                      span={6}
                      key={index}
                      style={{ minHeight: rem(200) }}
                    >
                      <div className="imageContainer">
                        <img src={image} alt="" className="imageUpload" />
                        <ActionIcon
                          size={20}
                          radius="xl"
                          variant="filled"
                          onClick={() => handleDeleteImage(index)}
                          className="deleteImage"
                        >
                          <IoClose
                            style={{ width: rem(18), height: rem(18) }}
                            stroke={1.5}
                          />
                        </ActionIcon>
                      </div>
                    </Grid.Col>
                  );
                })}
              </Grid>
            </div>
          </div>

          <Group
            w={"100%"}
            style={{
              margin: "10px 0",
            }}
            justify="center"
            mt={"xl"}
          >
            <Button
              variant="default"
              onClick={() => handleStepChange(active - 1)}
            >
              Trước
            </Button>
            <Button onClick={nextStep2}>Sau</Button>
          </Group>
        </Stepper.Step>

        <Stepper.Step
          label="Thông tin chi tiết"
          allowStepSelect={shouldAllowSelectStep(2)}
        >
          <Box maw="50%" mx="auto" my="md">
            <TextInput
              // w={"100%"}
              withAsterisk
              label="Tiêu đề"
              value={title}
              onChange={(event) => setTitle(event.currentTarget.value)}
              // style={{ paddingBottom: "10px" }}
              error={
                title?.length > 70 || title?.length < 20
                  ? "Nhập 20 đến 70 ký tự"
                  : ""
              }
            />

            <NumberInput
              withAsterisk
              label="Diện tích"
              min={0}
              thousandSeparator=","
              suffix=" m²"
              value={acreage}
              onChange={(value) => setAcreage(value)}
            />
            <NumberInput
              withAsterisk
              label="Giá"
              min={0}
              thousandSeparator=","
              suffix=" VND"
              value={price}
              onChange={(value) => setPrice(value)}
            />
            <small style={{ color: "red" }}>
              Giá bán là: {transferPrice(pricePerM2)}/m²
            </small>

            <Select
              withAsterisk
              label="Kiểu bất sản muốn bán"
              data={[
                { value: "LAND", label: "Đất" },
                { value: "HOUSE", label: "Nhà Ở" },
                { value: "APARTMENT", label: "Chung Cư" },
              ]}
              value={typeProperty}
              onChange={(value) => setTypeProperty(value)}
            />

            <Grid gutter="md">
              <Grid.Col span={6}>
                <Select
                  withAsterisk
                  label="Hướng nhà"
                  data={[
                    { value: "NORTH", label: "Bắc" },
                    { value: "EAST_NORTH", label: "Đông Bắc" },
                    { value: "EAST", label: "Đông" },
                    { value: "EAST_SOUTH", label: "Đông Nam" },
                    { value: "SOUTH", label: "Nam" },
                    { value: "WEST_SOUTH", label: "Tây Nam" },
                    { value: "WEST", label: "Tây" },
                    { value: "WEST_NORTH", label: "Tây Bắc" },
                  ]}
                  value={directionProperty}
                  onChange={(value) => setDirectionProperty(value)}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <Select
                  withAsterisk
                  label="Trạng thái sổ đỏ"
                  data={[
                    { value: "HAVE", label: "Đã có sổ" },
                    { value: "WAIT", label: "Đang chờ" },
                    { value: "HAVE_NOT", label: "Không có" },
                  ]}
                  value={legalDocument}
                  onChange={(value) => setLegalDocument(value)}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <NumberInput
                  withAsterisk
                  label="Chiều dài"
                  min={0}
                  suffix=" mét"
                  value={horizontal}
                  onChange={(value) => setHorizontal(value)}
                  decimalScale={1}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <NumberInput
                  withAsterisk
                  label="Chiều rộng"
                  min={0}
                  suffix=" mét"
                  value={vertical}
                  onChange={(value) => setVertical(value)}
                  decimalScale={1}
                />
              </Grid.Col>
            </Grid>

            {typeProperty === "APARTMENT" && (
              <Grid>
                <Grid.Col span={6}>
                  <NumberInput
                    withAsterisk
                    label="Số phòng ngủ"
                    min={0}
                    value={bedroom}
                    onChange={(value) => setBedroom(value)}
                    allowDecimal={false}
                  />
                </Grid.Col>

                <Grid.Col span={6}>
                  <NumberInput
                    withAsterisk
                    label="Số phòng tắm"
                    min={0}
                    value={bathroom}
                    onChange={(value) => setBathroom(value)}
                    allowDecimal={false}
                  />
                </Grid.Col>
              </Grid>
            )}

            {typeProperty === "HOUSE" && (
              <Grid>
                <Grid.Col span={6}>
                  <NumberInput
                    withAsterisk
                    label="Số tầng"
                    min={0}
                    value={floor}
                    onChange={(value) => setFloor(value)}
                    allowDecimal={false}
                  />
                </Grid.Col>

                <Grid.Col span={6}>
                  <NumberInput
                    withAsterisk
                    label="Số phòng ngủ"
                    min={0}
                    value={bedroom}
                    onChange={(value) => setBedroom(value)}
                    allowDecimal={false}
                  />
                </Grid.Col>

                <Grid.Col span={6}>
                  <NumberInput
                    withAsterisk
                    label="Số phòng tắm"
                    min={0}
                    value={bathroom}
                    onChange={(value) => setBathroom(value)}
                    allowDecimal={false}
                  />
                </Grid.Col>

                <Grid.Col span={6}>
                  <NumberInput
                    withAsterisk
                    label="Có thể đậu bao nhiêu xe ô tô"
                    min={0}
                    value={parking}
                    onChange={(value) => setParking(value)}
                    allowDecimal={false}
                  />
                </Grid.Col>
              </Grid>
            )}
            <small>Thông tin khác</small>
            <CKEditor
              style={{ height: "300px" }}
              editor={ClassicEditor}
              data={description}
              onReady={(editor) => {
                // You can store the "editor" and use when it is needed.
                // console.log('Editor is ready to use!', editor);
              }}
              onChange={(event, editor) => {
                // console.log( event );
                setDescription(editor.getData());
              }}
              onBlur={(event, editor) => {
                console.log("Blur.", editor);
              }}
              onFocus={(event, editor) => {
                console.log("Focus.", editor);
              }}
            />

            <Group
              w={"100%"}
              style={{
                margin: "10px 0",
              }}
              justify="center"
              mt={"xl"}
            >
              <Button
                variant="default"
                onClick={() => handleStepChange(active - 1)}
              >
                Trước
              </Button>
              <Button onClick={nextStep3}>Sau</Button>
            </Group>
          </Box>
        </Stepper.Step>

        <Stepper.Step
          label="Xem Trước"
          allowStepSelect={shouldAllowSelectStep(3)}
        >
          <InfoPostSell propertyDetails={post} />
          <Group
            w={"100%"}
            style={{
              margin: "10px 0",
            }}
            justify="center"
            mt={"xl"}
          >
            <Button
              variant="default"
              onClick={() => handleStepChange(active - 1)}
            >
              Trước
            </Button>
            <Button onClick={nextStep4} loading={isLoadingUpload}>
              Đăng bài
            </Button>
          </Group>
        </Stepper.Step>
      </Stepper>

      {/*<ToastContainer*/}
      {/*  position="top-left"*/}
      {/*  autoClose={3000}*/}
      {/*  hideProgressBar={false}*/}
      {/*  newestOnTop={false}*/}
      {/*  closeOnClick*/}
      {/*  rtl={false}*/}
      {/*  pauseOnFocusLoss*/}
      {/*  draggable*/}
      {/*  pauseOnHover*/}
      {/*/>*/}
    </div>
  );
};

export default AddPostSell;
