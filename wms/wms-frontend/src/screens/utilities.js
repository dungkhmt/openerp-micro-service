import { Chip } from '@material-ui/core';
import React from 'react'
import currency from 'currency.js';
import moment from 'moment';

export function setCanvasSize(size, scale) {
  return (size * scale);
}

export function getQuantity(quantity){
  if(quantity == null) return 0 ;
  return quantity;
}

export function formatDate(date){
  if(date == null) return "" ;
  return moment(date).format('DD/MM/YYYY');
}

export function formatCurrency(curr){
  if(curr ==  0 || curr == null) return "" ;
  return currency(curr, { symbol : "",separator: ',', precision: 0,fromCents: false }).format();
}
export function getType(type){
  switch(type) {
    case "normal":
      return "Sản phẩm thường"
      break;
    case "lots":
      return "Sản phẩm theo lô"
      break;
    default:
      "Sản phẩm thường"
  }
}
export function getImportStatus(stt, height){
  switch(stt) {
    case "complete":
      return <Chip label="Hoàn thành" style={{borderColor:'#2e7d32', color: "#2e7d32", height : height? height : 32}}   variant="outlined" />
      break;
    case "process":
      return <Chip label="Đang xử lý" style={{borderColor:'#F0CB67', color: "#F0CB67", height : height? height : 32}}   variant="outlined" />
      break;
    case "init":
      return <Chip label="Khởi tạo" style={{borderColor:'#1976d2', color: "#1976d2", height : height? height : 32}}   variant="outlined" />
      break;
    default:
      <Chip label="Khởi tạo" style={{borderColor:'#1976d2', color: "#1976d2", height : height? height : 32}}   variant="outlined" />
  }
}

export function getStatus(stt, height){
  if(stt){
    return <Chip label="Đang giao dịch" style={{borderColor:'#2e7d32', color: "#2e7d32", height : height? height : 32}}   variant="outlined" />
  } else return <Chip label="Ngừng kinh doanh" style={{borderColor:'#d32f2f', color: "#d32f2f",  height : height? height : 32}} variant="outlined" />
}

export function readWeightProduct(val, unit){
  if(val && unit ){
    return `${val}  ${unit}`
  }
  else{
    return ""
  }
}

export function readPropProduct(val){
  if(val){
    return val
  }
  else{
    return ""
  }
}