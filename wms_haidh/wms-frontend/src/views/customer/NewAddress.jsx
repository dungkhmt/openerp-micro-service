import React, { useState, useEffect } from "react";
import { Card, TextField, Button } from "@mui/material";
import Map from "../../components/Map";
import { useNavigate } from "react-router-dom";
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { request } from '../../api';

const NewAddress = () => {
  const navigate = useNavigate();
  const [coordinates, setCoordinates] = useState(null);
  const [addressName, setAddressName] = useState("");

  const breadcrumbPaths = [
    { label: "Home", link: "/" },
    { label: "Cart", link: "/customer/cart" },
    { label: "Checkout", link: "/customer/cart/checkout" },
    { label: "Add new address", link: "/customer/cart/checkout/new-address" },
  ];

  useEffect(() => {
    if (coordinates)
      request("post", `/geocoding/address`, (res) => {
        if (res.status === 200) {
          setAddressName(res.data.formattedAddress);
        } else {
          alert("Error occcured !");
        }
      }, {}, coordinates);
  }, [coordinates]);

  const handleSelectLocation = (coords) => {
    setCoordinates(coords);
  };

  const handleSubmit = () => {
    if (!coordinates) {
      alert("Please select a location.");
      return;
    }

    const payload = {
      addressName,
      longitude: coordinates.lng,
      latitude: coordinates.lat
    };
    request("post", `/customer-addresses`, (res) => {
      if (res.status === 201) {
        // Quay lại trang checkout
        navigate("/customer/cart/checkout");
      } else {
        alert("Error occcured !");
      }
    }, {}, payload);


  };

  return (
    <div className="flex justify-start p-6">
      <div className="w-full space-y-6">
        <BreadcrumbsCustom paths={breadcrumbPaths} />
        <Card className="p-6 space-y-4">
          <h3 className="text-lg font-semibold text-center">Select Your Address</h3>
          <Map enableSelection={true} onSelectLocation={handleSelectLocation} />
          <TextField
            label="Selected Address"
            fullWidth
            value={addressName ? addressName : ""}
            disabled
          />
        </Card>
        <div className="flex justify-center">
          <Button variant="contained" color="primary" sx={{
            width: "100%",
            height: "45px",
            backgroundColor: "black",
            color: "white",
            fontSize: "16px",
            "&:hover": {
              backgroundColor: "black",
              opacity: 0.75,
            },
          }} onClick={handleSubmit}>
            Save Address
          </Button>
        </div>

      </div>
    </div>
  );
};

export default NewAddress;
