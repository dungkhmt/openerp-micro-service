import React, { useState } from 'react';
import './TagInput.css'; // Optional, for styling purposes

const TagInput = ({ onTagsChange, tags, placeholder }) => {
  const [inputValue, setInputValue] = useState('');

  const handleInputChange = (e) => {
    setInputValue(e.target.value);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && inputValue.trim()) {
      const newTags = [...tags, inputValue.trim()];
      setInputValue('');
      onTagsChange(newTags); // Gọi hàm callback khi tags thay đổi
    }
  };

  const handleRemoveTag = (index) => {
    const newTags = tags.filter((_, i) => i !== index);
    onTagsChange(newTags); // Gọi hàm callback khi tags thay đổi
  };

  return (
    <div className="tag-input-container">
      {tags.map((tag, index) => (
        <div key={index} className="tag">
          {tag}
          <button className="tag-remove-button" onClick={() => handleRemoveTag(index)}>×</button>
        </div>
      ))}
      <input
        type="text"
        value={inputValue}
        onChange={handleInputChange}
        onKeyDown={handleKeyDown}
        placeholder={placeholder}
        className="tag-input"
      />
    </div>
  );
};

export default TagInput;
