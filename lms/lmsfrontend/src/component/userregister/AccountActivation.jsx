import React, {useEffect, useState} from "react";
import {useParams} from "react-router";
import {request} from "../../api";
import {errorNoti} from "../../utils/notification";

export default function AccountActivation() {
  const [status, setStatus] = useState("successfully");
  let param = useParams();
  const activationId = param.id;
  console.log("Account Activation, id = ", activationId);

  function activateAccount() {
    request(
      "get",
      //`/user/approve-registration`,
      `/public/activate-account/` + activationId,
      {
        404: (e) => {
          if ("not existed" === e.response.data.error) {
            errorNoti(e.response.data.message);
          } else {
            errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
          }
          setStatus("Not successfully");
        },
        rest: () => {
          errorNoti("Rất tiếc! Đã có lỗi xảy ra. Vui lòng thử lại.");
          setStatus("Not successfully");
        },
      }
    );
  }
  useEffect(() => {
    activateAccount();
  }, []);
  return (
    <div>
      <h1>Activate Account {status}</h1>
    </div>
  );
}
