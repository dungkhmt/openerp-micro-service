import { Route, Switch, useRouteMatch } from "react-router";
import { MyAssetScreen } from "views/assetManagement/home/MyAssetScreen";
import MyRequestScreen from "views/assetManagement/home/MyRequestScreen";
import AssetDetail from "views/assetManagement/management/AssetDetail";
import AssetsScreen from "views/assetManagement/management/AssetsScreen";
import RequestsScreen from "views/assetManagement/operation/RequestsScreen";
import RequestDetail from "views/assetManagement/operation/request/RequestDetail";
import AssetReport from "views/assetManagement/reports/AssetReport";
import RequestReport from "views/assetManagement/reports/RequestReport";
import LocationDetail from "views/assetManagement/settings/LocationDetail";
import { LocationScreen } from "views/assetManagement/settings/LocationScreen";
import TypeDetail from "views/assetManagement/settings/TypeDetail";
import { TypeScreen } from "views/assetManagement/settings/TypeScreen";
import VendorDetail from "views/assetManagement/settings/VendorDetail";
import { VendorScreen } from "views/assetManagement/settings/VendorScreen";

export default function AssetManagementRouter() {
  let { path } = useRouteMatch();

  return (
    <>
      <Switch>
        <Route component={MyAssetScreen} exact path={`${path}/my-asset`} />
        <Route component={MyRequestScreen} exact path={`${path}/my-request`} />
        <Route component={LocationScreen} exact path={`${path}/locations`} />
        <Route component={LocationDetail} exact path={`${path}/location/:id`} />
        <Route component={VendorScreen} exact path={`${path}/vendors`} />
        <Route component={VendorDetail} exact path={`${path}/vendor/:id`} />
        <Route component={TypeScreen} exact path={`${path}/types`} />
        <Route component={TypeDetail} exact path={`${path}/type/:id`} />
        <Route component={AssetsScreen} exact path={`${path}/assets`} />
        <Route component={AssetDetail} exact path={`${path}/asset/:id`} />
        <Route component={RequestsScreen} exact path={`${path}/requests`} />
        <Route component={RequestDetail} exact path={`${path}/request/:id`} />
        <Route component={AssetReport} exact path={`${path}/reports/asset`} />
        <Route
          component={RequestReport}
          exact
          path={`${path}/reports/request`}
        />
      </Switch>
    </>
  );
}
