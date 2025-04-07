import React, { useEffect, useState, useMemo } from "react";
import { Autocomplete, TextField, CircularProgress } from "@mui/material";
import { request } from "@/api";
import debounce from "lodash/debounce";

const SearchSelect = ({
                        label,
                        fetchUrl,
                        mapFunction = (data) => data,
                        value,
                        onChange,
                        getOptionLabel = (option) => option?.name || "",
                        disabled = false,
                        size = 20,
                        multiple = false,
                        textFieldProps = {},
                      }) => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [inputValue, setInputValue] = useState("");

  const fetchItems = async (search) => {
    setLoading(true);
    await request(
      "get",
      fetchUrl,
      (res) => {
        const raw = res.data?.data || [];
        setItems(mapFunction(raw));
      },
      {
        onError: (err) => console.error("Fetch error:", err),
      },
      null,
      {
        params: {
          departmentName: search || null,
          "pageableRequest.page": 0,
          "pageableRequest.pageSize": size,
          sortBy: "id",
          order: "asc",
        },
      }
    );
    setLoading(false);
  };

  const debouncedFetch = useMemo(() => debounce(fetchItems, 400), []);

  useEffect(() => {
    fetchItems("");
  }, [fetchUrl]);

  return (
    <Autocomplete
      multiple={multiple}
      options={items}
      getOptionLabel={getOptionLabel}
      value={value || (multiple ? [] : null)}
      onChange={(e, val) => onChange(val)}
      onInputChange={(e, val) => {
        setInputValue(val);
        debouncedFetch(val);
      }}
      disabled={disabled}
      loading={loading}
      filterSelectedOptions
      renderInput={(params) => (
        <TextField
          {...params}
          {...textFieldProps}
          label={label}
          variant="outlined"
          InputProps={{
            ...params.InputProps,
            ...textFieldProps.InputProps,
            endAdornment: (
              <>
                {loading ? <CircularProgress size={20} /> : null}
                {params.InputProps.endAdornment}
              </>
            ),
          }}
        />
      )}
    />
  );
};

export default SearchSelect;
