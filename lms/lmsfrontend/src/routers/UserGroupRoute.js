import React from "react";
import {Route, Switch, useRouteMatch} from "react-router";
// import Approve from "../component/userregister/Approve";
import Approve from "../views/UserRegister/Approve";
import SendMail2Users from "../component/admin/SendMail2Users";

export default function UserGroupRoute() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={Approve} path={`${path}/user/approve-register`} />
        <Route component={SendMail2Users} path={`${path}/user/send-mail`} />
      </Switch>
    </div>
  );
}
