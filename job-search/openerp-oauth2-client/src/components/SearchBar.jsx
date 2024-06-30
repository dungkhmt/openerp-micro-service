import React, { useState } from 'react';
import { TextField, IconButton, InputAdornment } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

function SearchBar({ items, setResult }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredItems, setFilteredItems] = useState(items);

  const handleSearch = (event) => {
    if (event.key === 'Enter') {
    const filtered = items.filter((item) => {
      const lowerCaseItem = item;
      return lowerCaseItem.title.toLowerCase().includes(searchTerm);
    });

    setFilteredItems(filtered);
    setResult(filtered)
  }
  };

  const handleSearchTermChange = (e) => {
    setSearchTerm(e.target.value.toLowerCase())
  }

  return (
    <TextField
      variant="outlined"
      placeholder="Searching jobs here"
      value={searchTerm}
      onChange={handleSearchTermChange}
      onKeyDown={handleSearch}
      InputProps={{
        endAdornment: (
          <InputAdornment position="end">
            <IconButton>
              <SearchIcon />
            </IconButton>
          </InputAdornment>
        ),
      }}
      style={{ width: '85%',  marginBottom: '50px', borderRadius: '50px'}}
    />
  );
}

export default SearchBar;
