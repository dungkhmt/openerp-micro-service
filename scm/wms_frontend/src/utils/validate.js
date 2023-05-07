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
  brand: Yup.string().required(),
  categoryId: Yup.object().required(),
  name: Yup.string().required(),
  sku: Yup.string().required(),
  status: Yup.object().required(),
  unitId: Yup.object().required(),
  unitPerBox: Yup.number().required(),
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
  address: Yup.string().required(),
  managedBy: Yup.object().required(),
  name: Yup.string().required(),
});
export const tripSchema = Yup.object().shape({
  address: Yup.string().required(),
  managedBy: Yup.object().required(),
  name: Yup.string().required(),
});
