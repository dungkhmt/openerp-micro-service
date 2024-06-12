import {
  Avatar,
  Grid,
  Input,
  PasswordInput,
  Tabs,
  TextInput,
  Button,
  Group,
} from "@mantine/core";
import "./Profile.css";
import { useDispatch, useSelector } from "react-redux";
import { TbCameraUp } from "react-icons/tb";
import React, { useState } from "react";
import { handleDeleteImage, uploadImage } from "../../utils/common";
import { set_current_account } from "../../store/account";
import AccountRequest from "../../services/AccountRequest";
import { hasLength, useForm } from "@mantine/form";
import { IMaskInput } from "react-imask";
import { toast, ToastContainer } from "react-toastify";

const Profile = () => {
  const dispatch = useDispatch();
  const current_account = useSelector((state) => state.account.currentData);
  const [profile, setProfile] = useState(current_account);

  const formInfo = useForm({
    mode: "uncontrolled",
    initialValues: {
      name: profile.name,
      phone: profile.phone,
    },
    validate: {
      name: hasLength({ min: 2, max: 20 }, "Biệt danh chỉ dài 2 đến 20 ký tự"),
    },
  });

  const formPassword = useForm({
    mode: "uncontrolled",
    initialValues: {
      oldPassword: "",
      newPassword: "",
      confirmPassword: "",
    },
    validate: (values) => ({
      oldPassword:
        values.oldPassword.length <= 7 || values.oldPassword.length > 16
          ? "8 đến 16 ký tự"
          : null,
      newPassword:
        values.newPassword.length <= 7 || values.newPassword.length >= 16
          ? "Yêu cầu 8 đến 16 ký tự"
          : values.newPassword === values.oldPassword
            ? "Mật khẩu mới phải khác mật khẩu cũ"
            : null,
      confirmPassword:
        values.confirmPassword !== values.newPassword
          ? "Mật khẩu không trùng khớp"
          : null,
    }),
  });

  const updateProfile = async (new_current_account) => {
    dispatch(set_current_account(new_current_account));
    const accountRequest = new AccountRequest();
    accountRequest
      .update_info({
        avatar: new_current_account.avatar,
        name: new_current_account.name,
        phone: new_current_account.phone,
      })
      .then((response) => {
        const status = response.code;
        if (status === 200) {
          toast.success(response.data);
        } else {
          toast.error(response.data.message);
        }
      });
  };

  const handleChangeNamePhone = async (values) => {
    const name = values.name;
    const phone = values.phone?.replace(/-/g, "");
    const new_current_account = { ...profile, name, phone };
    await updateProfile(new_current_account);
    setProfile(new_current_account);
  };
  const handleChangeAvatar = async (e) => {
    const file = e.target.files[0];
    const newAvatar = await uploadImage(file);
    if (newAvatar) {
      await handleDeleteImage(profile.avatar); // Nếu bạn muốn sử dụng hàm này, hãy bỏ comment
    }
    const new_current_account = { ...profile, avatar: newAvatar };
    await updateProfile(new_current_account);
    setProfile(new_current_account);
  };

  const handleChangePassword = async (values) => {
    const accountRequest = new AccountRequest();
    accountRequest
      .update_password({
        oldPassword: values.oldPassword,
        newPassword: values.newPassword,
      })
      .then((response) => {
        if (response.code === 200) {
          toast.success(response.data);
          formPassword.setValues({
            oldPassword: "",
            newPassword: "",
            confirmPassword: "",
          });
        } else {
          toast.error(response.data.message);
        }
      });
  };

  return (
    <div className="profileContainer">
      <Tabs defaultValue="info" color="green" variant="pills">
        <Tabs.List grow>
          <Tabs.Tab value="info">Thiết Lập Thông Tin</Tabs.Tab>
          <Tabs.Tab value="password">Thiết Lập Mật Khẩu</Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="info">
          <h2>Thông tin cá nhân</h2>
          <Grid align="center">
            <Grid.Col span={6}>
              <Input.Wrapper label="Email">
                <Input
                  style={{ marginBottom: "10px" }}
                  defaultValue={profile.email}
                  disabled={true}
                />
              </Input.Wrapper>

              <form
                onSubmit={formInfo.onSubmit((values) =>
                  handleChangeNamePhone(values),
                )}
              >
                <TextInput
                  style={{ marginBottom: "10px" }}
                  label="Biệt danh"
                  key={formInfo.key("name")}
                  {...formInfo.getInputProps("name")}
                />

                <TextInput
                  style={{ marginBottom: "10px" }}
                  component={IMaskInput}
                  mask="0000-000-000"
                  label="Số điện thoại"
                  unmask={true}
                  key={formInfo.key("phone")}
                  {...formInfo.getInputProps("phone")}
                />

                <Button
                  style={{ backgroundColor: "rgb(224, 60, 49)" }}
                  type="submit"
                  mt="sm"
                >
                  Lưu Thay Đổi
                </Button>
              </form>
            </Grid.Col>
            <Grid.Col span={6}>
              <div className="flexColCenter">
                <Avatar src={profile.avatar} alt="" size="xl" />

                <label
                  className="flexStart facility"
                  style={{ cursor: "pointer" }}
                  htmlFor="file-profile"
                >
                  <TbCameraUp size={20} color="#1F3E72" />
                  <span>Thay đổi ảnh</span>
                  <input
                    id="file-profile"
                    type="file"
                    hidden
                    onChange={handleChangeAvatar}
                    accept="/image/*"
                  />
                </label>
              </div>
            </Grid.Col>
          </Grid>
        </Tabs.Panel>
        <Tabs.Panel value="password">
          <div
            style={{
              width: "50%",
              margin: "auto",
            }}
          >
            <form
              onSubmit={(event) => {
                event.preventDefault();
                formPassword.onSubmit(handleChangePassword)(event);
              }}
            >
              <TextInput
                style={{
                  marginBottom: "5px",
                }}
                label="Mật khẩu cũ"
                key={formPassword.key("oldPassword")}
                {...formPassword.getInputProps("oldPassword")}
              />
              <PasswordInput
                style={{
                  marginBottom: "5px",
                }}
                label="Mật khẩu mới"
                key={formPassword.key("newPassword")}
                {...formPassword.getInputProps("newPassword")}
              />

              <PasswordInput
                mt="sm"
                label="Nhập lại mật khẩu"
                key={formPassword.key("confirmPassword")}
                {...formPassword.getInputProps("confirmPassword")}
              />

              <Group justify="space-between" mt="md">
                <Button type="submit">Đổi Mật Khẩu</Button>
              </Group>
            </form>
          </div>
        </Tabs.Panel>
      </Tabs>

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

export default Profile;
