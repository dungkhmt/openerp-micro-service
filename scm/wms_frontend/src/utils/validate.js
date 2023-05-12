import * as Yup from "yup";
export const productSchema = Yup.object().shape({
  brand: Yup.string().required(),
  categoryId: Yup.object().required(),
  name: Yup.string().required(),
  sku: Yup.string().required(),
  status: Yup.object().required(),
  unitId: Yup.object().required(),
  unitPerBox: Yup.number().required(),
});
export const customerSchema = Yup.object().shape({
  address: Yup.string().required(),
  contractTypeCode: Yup.string().required(),
  customerTypeCode: Yup.string().required(),
  latitude: Yup.string().required(),
  longitude: Yup.string().required(),
  name: Yup.string().required(),
  phone: Yup.string().required(),
  status: Yup.object().required(),
});
export const facilitySchema = Yup.object().shape({
  address: Yup.string().required(),
  managedBy: Yup.object().required(),
  name: Yup.string().required(),
});

export const purchaseOrderSchema = Yup.object().shape({
  address: Yup.string().required(),
  managedBy: Yup.object().required(),
  name: Yup.string().required(),
});

export const saleOrderSchema = Yup.object().shape({
  address: Yup.string().required(),
  managedBy: Yup.object().required(),
  name: Yup.string().required(),
});
export const shipmentSchema = Yup.object().shape({
  endedDate: Yup.string().required(),
  startedDate: Yup.string().required(),
  title: Yup.string().required(),
  maxSize: Yup.number().required(),
});
export const tripSchema = Yup.object().shape({
  startDate: Yup.object().required(),
  facility: Yup.object().required(),
  userInCharge: Yup.object().required(),
});
export const distChannelSchema = Yup.object().shape({
  name: Yup.string().required(),
});

export const contractType = Yup.object().shape({
  name: Yup.string().required(),
  channelCode: Yup.object().required(),
});
export const customerTypeSchema = Yup.object().shape({
  name: Yup.string().required(),
});
export const productCategorySchema = Yup.object().shape({
  name: Yup.string().required(),
});
