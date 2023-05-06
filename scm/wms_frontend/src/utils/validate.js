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
