import React, { useState } from "react";
import { OutlinedInput } from "@mui/material";
import {Button} from "@mui/material";
import {List} from "@mui/material";
import {ListItem} from "@mui/material";
import {ListItemIcon} from "@mui/material";
import {ListItemText} from "@mui/material";
import { Divider } from "@mui/material";
import { PLACE_HOLDER_ICON_URL } from "components/constants";

const NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search?";

export default function SearchBox(props) {
  const { selectPosition, setSelectPosition } = props;
  const [searchText, setSearchText] = useState("");
  const [listPlace, setListPlace] = useState([]);

  return (
    <div style={{ display: "flex", flexDirection: "column", width: '100%', height: '100%' }}>
      <div style={{ display: "flex" }}>
        <div style={{ flex: 1 }}>
          <OutlinedInput
            style={{ width: "100%" }}
            value={searchText}
            onChange={(event) => {
              setSearchText(event.target.value);
            }}
          />
        </div>
        <div
          style={{ display: "flex", alignItems: "center", padding: "0px 20px" }}
        >
          <Button
            variant="contained"
            color="primary"
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
                  setListPlace(JSON.parse(result));
                })
                .catch((err) => console.log("err: ", err));
            }}
          >
            Tìm kiếm
          </Button>
        </div>
      </div>
      <div style={{ height: '85%' }}>
          <List component="nav" aria-label="main mailbox folders" style={{maxHeight: '100%', overflow: 'auto'}}>
            {listPlace.map((item) => {
              return (
                <div key={item?.place_id}>
                  <ListItem
                    button
                    onClick={() => {
                      console.log("List element clicked: ", item);
                      setSelectPosition(item);
                    }}
                  >
                    <ListItemIcon>
                      <img
                        src={ PLACE_HOLDER_ICON_URL }
                        alt="Placeholder"
                        style={{ width: 38, height: 38 }}
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
