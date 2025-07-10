import React, { useState, useEffect } from "react";
import { Card, TextField, Button } from "@mui/material";
import Map from "../../components/Map";
import { useNavigate } from "react-router-dom";
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { request } from '../../api';
import { toast, Toaster } from "react-hot-toast";
import { InputAdornment, Tooltip, IconButton } from "@mui/material";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";

const NewAddress = () => {
  const navigate = useNavigate();
  const [coordinates, setCoordinates] = useState(null);
  const [addressName, setAddressName] = useState("");
  const [isAutoPan, setIsAutoPan] = useState(false);

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
        }
      }, {
        onError: (e) => {
          toast.error(e?.response?.data || "Error occured!");
        }
      }, coordinates);
  }, [coordinates]);

  const handleSelectLocation = (coords) => {
    setCoordinates(coords);
    setIsAutoPan(false);
  };

  const handleUseCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const coords = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          };
          setCoordinates(coords);
          setIsAutoPan(true);
        },
        (error) => {
          toast.error("Failed to get current location.");
        }
      );
    } else {
      toast.error("Geolocation is not supported by this browser.");
    }
  };


  const handleSubmit = () => {
    if (!coordinates) {
      toast.error("Please select a location.");
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
        alert("Add new address successfully!");
        navigate("/customer/cart/checkout");
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      }
    }, payload);


  };

  return (
    <div className="flex justify-start p-6">
      <Toaster />
      <div className="w-full space-y-6">
        <BreadcrumbsCustom paths={breadcrumbPaths} />
        <Card className="p-6 space-y-4">
          <h3 className="text-lg font-semibold text-center">Select Your Position</h3>
          <Map enableSelection={true} onSelectLocation={handleSelectLocation}
            selectedCoordinates={coordinates} shouldAutoPan={isAutoPan} />
          {/* Nút dùng vị trí hiện tại */}
          <div className="flex justify-end">
            <Button
              variant="outlined"
              onClick={handleUseCurrentLocation}
            >
              Current Location
            </Button>
          </div>

          <TextField
            label="Selected Address"
            fullWidth
            value={addressName}
            onChange={(e) => setAddressName(e.target.value)}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <Tooltip title="Sometimes the house number is missing from the map. You can adjust it manually.">
                    <IconButton edge="end">
                      <InfoOutlinedIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </InputAdornment>
              )
            }}
          />
        </Card>
        <div className="flex justify-center">
          <Button variant="contained" color="primary" sx={{
            width: '40%',
            margin: 'auto',
            backgroundColor: '#6fd649',
            color: '#fff',
            '&:hover': {
              backgroundColor: '#4caf50',
            },
            '&:active': {
              backgroundColor: '#3e8e41',
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
