import {
  Box,
  Button,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  OutlinedInput,
} from "@mui/material";
import { useState } from "react";
import { AppImages } from "../../shared/AppImages";

const NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search?";
const params = {
  q: "",
  format: "json",
  addressdetails: "addressdetails",
};

export default function SearchBoxMap({ selectPosition, setSelectPosition }) {
  const [searchText, setSearchText] = useState("");
  const [listPlace, setListPlace] = useState([]);

  return (
    <div style={{}}>
      <div style={{ display: "flex" }}>
        <div style={{ flex: 1 }}>
          <OutlinedInput
            style={{ width: "100%", height: 40 }}
            value={searchText}
            onChange={(event) => {
              setSearchText(event.target.value);
            }}
          />
        </div>
        <div
          style={{ display: "flex", alignItems: "center", padding: "0px 10px" }}
        >
          <Button
            // variant="contained"
            variant="outlined"
            // color="primary"
            onClick={() => {
              // Search
              const params = {
                q: searchText,
                format: "json",
                addressdetails: 1,
                polygon_geojson: 0,
              };
              const queryString = new URLSearchParams(params).toString();
              const requestOptions = {
                method: "GET",
                redirect: "follow",
              };
              fetch(`${NOMINATIM_BASE_URL}${queryString}`, requestOptions)
                .then((response) => response.text())
                .then((result) => {
                  console.log(JSON.parse(result));
                  setListPlace(JSON.parse(result));
                })
                .catch((err) => console.log("err: ", err));
            }}
          >
            Search
          </Button>
        </div>
      </div>
      <div>
        <List component="nav" aria-label="main mailbox folders">
          {listPlace.map((item) => {
            return (
              <div key={item?.place_id}>
                <ListItem
                  button
                  onClick={() => {
                    setSelectPosition({
                      lat: item?.lat,
                      lng: item?.lon,
                    });
                  }}
                >
                  <ListItemIcon>
                    <Box
                      component="img"
                      sx={{
                        height: 30,
                        width: 30,
                        maxHeight: { xs: 233, md: 167 },
                        maxWidth: { xs: 350, md: 250 },
                      }}
                      alt="Location"
                      src={AppImages.blue_location}
                    />
                  </ListItemIcon>
                  <ListItemText primary={item?.display_name} />
                </ListItem>
                <Divider />
              </div>
            );
          })}
        </List>
      </div>
    </div>
  );
}
