import React, { useEffect, useState } from "react";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";

export const MyAssetScreen = () => {
  const [assets, setAssets] = useState([]);
  const [locations, setLocations] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [types, setTypes] = useState([]);

  const getMyAssets = async () => {
    request("get", "/asset/get-by-user", (res) => {
      setAssets(res.data);
    }).then();
  };

  const getAllLocations = async () => {
    request("get", "/location/get-all", (res) => {
      setLocations(res.data);
    }).then();
  };

  const getAllVendors = async () => {
    request("get", "/vendor/get-all", (res) => {
      setVendors(res.data);
    }).then();
  };

  const getAllTypes = async () => {
    request("get", "/asset-type/get-all", (res) => {
      setTypes(res.data);
    }).then();
  };

  useEffect(() => {
    getAllLocations();
    getAllVendors();
    getAllTypes();
    getMyAssets();
  }, []);

  const columns = [
    {
      title: "Name",
      field: "name",
    },
    {
      title: "Admin Id",
      field: "admin_id",
    },
    {
      title: "Code",
      field: "code",
    },
    {
      title: "Type",
      render: (rowData) => {
        let found = types.find((typ) => typ.id === rowData.type_id);
        if (found) {
          return <div>{found.name}</div>;
        } else return ``;
      },
    },
    {
      title: "Location",
      field: "location",
      render: (rowData) => {
        let found = locations.find((loc) => loc.id === rowData.location_id);
        if (found) {
          return <div>{found.name}</div>;
        } else return ``;
      },
    },
    {
      title: "Vendor",
      field: "vendor",
      render: (rowData) => {
        let found = vendors.find((v) => v.id === rowData.vendor_id);
        if (found) {
          return <div>{found.name}</div>;
        } else return ``;
      },
    },
  ];

  return (
    <div>
      <StandardTable
        title="My Assets"
        columns={columns}
        data={assets}
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
};
