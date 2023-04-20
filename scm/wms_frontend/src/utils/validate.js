import * as Yup from "yup";
export const brandSchema = Yup.object().shape({
  code: Yup.string().required(),
  name: Yup.string().required(),
});
