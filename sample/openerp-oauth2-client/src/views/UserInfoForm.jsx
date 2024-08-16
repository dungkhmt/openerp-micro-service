import React, { useState } from 'react';
import { toast, ToastContainer } from 'react-toastify';
import { successNoti,errorNoti } from "../utils/notification";
import axios from 'axios'; 
import {request} from "../api";
import PrimaryButton from 'components/button/PrimaryButton';
import { TextField } from '@mui/material';
import Box from '@mui/material/Box';


function UserInfoForm() {
  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    email: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (validateForm()) {
      try {
        await request(
          "post",
          'http://localhost:8080/api/user/save',
          (res) => {
            successNoti(`Thông tin người dùng đã được lưu:
              Họ tên: ${formData.fullName}
              Số điện thoại: ${formData.phoneNumber}`, 2000);
          },
          {
            onError: (error) => {
              errorNoti('Có lỗi xảy ra khi lưu thông tin. Vui lòng thử lại.');
              console.error('Error saving user info:', error);
            }
          },
          formData
        );
      
      } catch (error) {
        successNoti(`Thông tin người dùng đã được lưu:
          Họ tên: ${formData.fullName}
          Số điện thoại: ${formData.phoneNumber}
          Email: ${formData.email}`, 2000);
      }
    }
  };

  const validateForm = () => {
    if (!formData.fullName.trim()) {
      toast.error('Vui lòng nhập họ tên');
      return false;
    }
    if (!formData.phoneNumber.trim()) {
      toast.error('Vui lòng nhập số điện thoại');
      return false;
    }
    if (!isValidEmail(formData.email)) {
      toast.error('Email không hợp lệ');
      return false;
    }
    return true;
  };

  const isValidEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  return (
    <div>
    <h2>Nhập thông tin người dùng</h2>
    <form onSubmit={handleSubmit}>
      <Box mb={4}>
        <TextField 
          variant="outlined" 
          label="Họ và Tên" 
          type="text" 
          id="fullName"
          name="fullName"
          value={formData.fullName}
          onChange={handleChange}
        />
      </Box>
      <Box mb={4}>
        <TextField 
          variant="outlined" 
          label="Số điện thoại" 
          type="tel" 
          id="phoneNumber"
          name="phoneNumber"
          value={formData.phoneNumber}
          onChange={handleChange}
        />
      </Box>
      <Box mb={4} width={400}>
        <TextField 
          variant="outlined" 
          label="Email" 
          type="email" 
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
        />
      </Box>
      <PrimaryButton type="submit">Gửi</PrimaryButton>
    </form>
  </div>

  );
}

export default UserInfoForm;