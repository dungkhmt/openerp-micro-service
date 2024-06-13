import {
  Box,
  Button,
  Checkbox,
  Grid,
  Group,
  MultiSelect,
  NumberInput,
  RangeSlider,
  Select,
  TextInput,
} from "@mantine/core";
import React, { useContext, useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import { transferPrice } from "../../utils/common";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import { PostBuyRequest } from "../../services/PostBuyRequest";
import { useNavigate } from "react-router-dom";
import DistrictRequest from "../../services/DistrictRequest";
import "./AddPostBuy.css";
import { AccountContext } from "../../context/AccountContext";
import CardBuy from "../../components/CardBuy/CardBuy";

const AddPostBuy = () => {
  const navigate = useNavigate();
  const { account } = useContext(AccountContext);

  const [post, setPost] = useState({});
  const [isPreview, setIsPreview] = useState(true);

  const [filterAcreage, setFilterAcreage] = useState([0, 100]);
  const [minAcreage, setMinAcreage] = useState(0);
  const [maxAcreage, setMaxAcreage] = useState(100);
  const [checkMaxAcreage, setCheckMaxAcreage] = useState(false);

  const [filterPrice, setFilterPrice] = useState([0, 10000000000]);
  const [minPrice, setMinPrice] = useState(0);
  const [maxPrice, setMaxPrice] = useState(10000000000);
  const [checkMaxPrice, setCheckMaxPrice] = useState(false);

  const [title, setTitle] = useState(null);
  const [description, setDescription] = useState("");

  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);

  const [provinceId, setProvinceId] = useState();
  const [nameProvince, setNameProvince] = useState();
  const [districtIds, setDistrictIds] = useState([]);

  const [typeProperty, setTypeProperty] = useState();
  const [directionProperties, setDirectionProperties] = useState([]);

  const [minHorizontal, setMinHorizontal] = useState(0);
  const [minVertical, setMinVertical] = useState(0);

  const [minBedroom, setMinBedroom] = useState(0);
  const [minBathroom, setMinBathroom] = useState(0);
  const [minParking, setMinParking] = useState(0);
  const [minFloor, setMinFloor] = useState(0);
  const [legalDocuments, setLegalDocuments] = useState([]);

  const optionsProvince = provinces?.map((item) => ({
    value: item.provinceId,
    label: item.nameProvince,
    // key: item.provinceId,
  }));

  const optionsDistrict = districts?.map((item) => ({
    value: item.districtId,
    label: item.nameDistrict,
    // key: item.districtId
  }));

  const marksPrice = [
    { value: 2500000000, label: "2,5 tỷ" },
    { value: 5000000000, label: "5 tỷ" },
    { value: 7500000000, label: "7,5 tỷ" },
  ];

  const marksAcreage = [
    { value: 20, label: "20 m²" },
    { value: 40, label: "40 m²" },
    { value: 60, label: "60 m²" },
    { value: 80, label: "80 m²" },
  ];

  const handleSubmit = (e) => {
    e.preventDefault();
    if (
      provinceId === null ||
      districtIds.length === 0 ||
      title.length < 20 ||
      title.length > 70 ||
      typeProperty === null ||
      legalDocuments.length === 0 ||
      directionProperties.length === 0
    ) {
      toast.error("Yêu cầu điền thông tin đầy đủ!");
    } else {
      const request = new PostBuyRequest();
      const nameDistricts = districtIds
        .map((id) => {
          const district = districts.find((d) => d.districtId === id);
          return district ? district.nameDistrict : null;
        })
        .filter((label) => label !== null);
      const data = {
        title: title,
        description: description,
        provinceId: provinceId,
        nameProvince: nameProvince,
        districtIds: districtIds,
        nameDistricts: nameDistricts,
        minAcreage: minAcreage,
        maxAcreage: maxAcreage,
        minPrice: minPrice,
        maxPrice: maxPrice,
        minFloor: minFloor,
        minBedroom: minBedroom,
        minBathroom: minBathroom,
        minParking: minParking,
        legalDocuments: legalDocuments,
        directionProperties: directionProperties,
        typeProperty: typeProperty,

        minHorizontal: minHorizontal,
        minVertical: minVertical,
      };

      // console.log(data);
      request.add_post(data).then((response) => {
        const status = response.code;
        if (status === 200) {
          toast.success(response.data);
          navigate("/buy/properties", { replace: true });
        } else {
          toast.error(response.data.message);
        }
      });
    }
  };

  const handleToPreview = () => {
    if (
      provinceId === null ||
      districtIds.length === 0 ||
      title.length < 20 ||
      title.length > 70 ||
      typeProperty === null ||
      legalDocuments.length === 0 ||
      directionProperties.length === 0
    ) {
      toast.error("Yêu cầu điền thông tin đầy đủ!");
    } else {
      const nameDistricts = districtIds
        .map((id) => {
          const district = districts.find((d) => d.districtId === id);
          return district ? district.nameDistrict : null;
        })
        .filter((label) => label !== null);
      setPost({
        authorId: account.accountId,
        nameAuthor: account.name,
        avatarAuthor: account.avatar,
        phoneAuthor: account.phone,
        emailAuthor: account.email,
        title: title,
        description: description,
        provinceId: provinceId,
        nameProvince: nameProvince,
        districtIds: districtIds,
        nameDistricts: nameDistricts,
        minAcreage: minAcreage,
        maxAcreage: maxAcreage,
        minPrice: minPrice,
        maxPrice: maxPrice,
        minFloor: minFloor,
        minBedroom: minBedroom,
        minBathroom: minBathroom,
        minParking: minParking,
        legalDocuments: legalDocuments,
        directionProperties: directionProperties,
        typeProperty: typeProperty,

        minHorizontal: minHorizontal,
        minVertical: minVertical,
      });
      setIsPreview(true);
    }
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
    const fetchPublicDistrict = async () => {
      const districtRequest = new DistrictRequest();
      districtRequest
        .get_districts({
          provinceId,
        })
        .then((response) => {
          if (response.code === 200) {
            setDistricts(response.data);
          }
        });
    };
    provinceId && fetchPublicDistrict();
  }, [provinceId]);

  useEffect(() => {
    if (checkMaxAcreage === false) {
      setMinAcreage(filterAcreage[0]);
      setMaxAcreage(filterAcreage[1]);
    } else {
      setMinAcreage(100);
      setMaxAcreage(0);
    }
  }, [filterAcreage, checkMaxAcreage]);

  useEffect(() => {
    if (checkMaxPrice === false) {
      setMinPrice(filterPrice[0]);
      setMaxPrice(filterPrice[1]);
    } else {
      setMinPrice(10000000000);
      setMaxPrice(0);
    }
  }, [filterPrice, checkMaxPrice]);

  return (
    <div className="postBuy-wrapper">
      {isPreview && Object.keys(post).length > 0 ? (
        <div>
          <CardBuy item={post} />
          <Group
            justify="center"
            style={{
              margin: "10px 0",
              paddingBottom: "10px",
            }}
          >
            <Button
              variant="default"
              style={{
                margin: "0 15px",
              }}
              onClick={() => setIsPreview(false)}
            >
              Chỉnh sửa
            </Button>
            <Button onClick={handleSubmit}>Đăng bài</Button>
          </Group>
        </div>
      ) : (
        <Box
          maw="50%"
          mx="auto"
          my="md"
          style={{
            marginTop: "20px",
          }}
        >
          <TextInput
            // w={"100%"}
            withAsterisk
            label="Tiêu đề"
            value={title}
            onChange={(event) => setTitle(event.currentTarget.value)}
            style={{
              paddingTop: "20px",
            }}
            // style={{ paddingBottom: "30px" }}
            error={
              title?.length > 70 || title?.length < 20
                ? "Nhập 20 đến 70 ký tự"
                : ""
            }
          />

          <Select
            w={"100%"}
            withAsterisk
            label="Tỉnh"
            clearable
            searchable
            data={optionsProvince}
            value={provinceId}
            onChange={(value, option) => {
              setProvinceId(option?.value);
              setNameProvince(option?.label);
            }}
          />

          <MultiSelect
            w={"100%"}
            withAsterisk
            label="Quận/ Huyện"
            clearable
            searchable
            data={optionsDistrict}
            onChange={(value) => {
              setDistrictIds(value);
            }}
            value={districtIds}
          />

          <RangeSlider
            style={{
              width: "100%",
              margin: "15px 0",
            }}
            label={(value) => value + "m²"}
            marks={marksAcreage}
            min={0}
            max={100}
            minRange={10}
            step={10}
            value={filterAcreage}
            onChange={setFilterAcreage}
            disabled={checkMaxAcreage}
          />
          <Checkbox
            style={{
              margin: "25px auto 5px auto",
            }}
            label="Trên 100 m²"
            checked={checkMaxAcreage}
            onChange={(event) =>
              setCheckMaxAcreage(event.currentTarget.checked)
            }
          />

          <RangeSlider
            style={{
              width: "100%",
              margin: "15px 0",
            }}
            label={(value) => transferPrice(value)}
            marks={marksPrice}
            min={0}
            max={10000000000}
            minRange={500000000}
            step={500000000}
            value={filterPrice}
            onChange={setFilterPrice}
            disabled={checkMaxPrice}
          />
          <Checkbox
            style={{
              margin: "25px auto 5px auto",
            }}
            label="Trên 10 tỷ"
            checked={checkMaxPrice}
            onChange={(event) => setCheckMaxPrice(event.currentTarget.checked)}
          />

          <Select
            withAsterisk
            label="Kiểu bất sản muốn mua"
            data={[
              { value: "LAND", label: "Đất" },
              { value: "HOUSE", label: "Nhà ở" },
              { value: "APARTMENT", label: "Chung cư" },
            ]}
            onChange={(value) => setTypeProperty(value)}
            value={typeProperty}
          />

          <MultiSelect
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
            onChange={(value) => setDirectionProperties(value)}
            value={directionProperties}
          />

          <MultiSelect
            withAsterisk
            label="Trạng thái sổ đỏ"
            data={[
              { value: "HAVE", label: "Đã có sổ" },
              { value: "WAIT", label: "Đang chờ" },
              { value: "HAVE_NOT", label: "Không có" },
            ]}
            value={legalDocuments}
            onChange={(value) => setLegalDocuments(value)}
          />

          <Grid>
            <Grid.Col span={6}>
              <NumberInput
                label="Chiều dài tối thiểu"
                min={0}
                value={minHorizontal}
                onChange={(value) => setMinHorizontal(value)}
                allowDecimal={false}
              />
            </Grid.Col>

            <Grid.Col span={6}>
              <NumberInput
                label="Chiều rộng tối thiểu"
                min={0}
                value={minVertical}
                onChange={(value) => setMinVertical(value)}
                allowDecimal={false}
              />
            </Grid.Col>
          </Grid>

          {typeProperty === "APARTMENT" && (
            <Grid>
              <Grid.Col span={6}>
                <NumberInput
                  label="Số phòng ngủ tối thiểu"
                  min={0}
                  value={minBedroom}
                  onChange={(value) => setMinBedroom(value)}
                  allowDecimal={false}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <NumberInput
                  label="Số phòng tắm tối thiểu"
                  min={0}
                  value={minBathroom}
                  onChange={(value) => setMinBathroom(value)}
                  allowDecimal={false}
                />
              </Grid.Col>
            </Grid>
          )}

          {typeProperty === "HOUSE" && (
            <Grid>
              <Grid.Col span={6}>
                <NumberInput
                  label="Số tầng tối thiểu"
                  min={0}
                  value={minFloor}
                  onChange={(value) => setMinFloor(value)}
                  allowDecimal={false}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <NumberInput
                  label="Số phòng ngủ tối thiểu"
                  min={0}
                  value={minBedroom}
                  onChange={(value) => setMinBedroom(value)}
                  allowDecimal={false}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <NumberInput
                  label="Số phòng tắm tối thiểu"
                  min={0}
                  value={minBathroom}
                  onChange={(value) => setMinBathroom(value)}
                  allowDecimal={false}
                />
              </Grid.Col>

              <Grid.Col span={6}>
                <NumberInput
                  label="Có thể đậu ít nhất bao nhiêu xe ô tô"
                  min={0}
                  value={minParking}
                  onChange={(value) => setMinParking(value)}
                  allowDecimal={false}
                />
              </Grid.Col>
            </Grid>
          )}

          <small>Yêu cầu khác</small>
          <CKEditor
            style={{ height: "400px" }}
            editor={ClassicEditor}
            data={description}
            onReady={(editor) => {}}
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
          <Button
            onClick={handleToPreview}
            className="flexColCenter"
            style={{ margin: "1rem 42%" }}
          >
            Xem trước
          </Button>
        </Box>
      )}
    </div>
  );
};

export default AddPostBuy;
