import { Button } from "@mui/material";
import { useRef } from "react";
import FileUploadOutlinedIcon from '@mui/icons-material/FileUploadOutlined'

function SelectFileButton({disabled, onClick, onChange}) {
    const ref = useRef();
    const on_click = (e) => {
        ref.current?.click();
        e.preventDefault();
        if (onClick!=null) onClick();
    }
    const on_change = (e) =>{
        const file = ref.current?.files[0]
        console.log(file)
        if (onChange!=null) onChange(file)
    }
  return (
    <div>
      <Button
        color="success"
        variant="outlined"
        onClick={on_click}
        disabled={disabled}
      >
        <FileUploadOutlinedIcon/>
        Nhập từ excel
      </Button>
      {/* ref: https://stackoverflow.com/questions/39484895/how-to-allow-input-type-file-to-select-the-same-file-in-react-component */}
      <input
        onClick={(event) => {
          event.target.value = null;
        }}
        onChange={on_change}
        accept=".xlsx,.xls"
        style={{ display: "none" }}
        ref={ref}
        type="file"
      />
    </div>
  );
}

export default SelectFileButton;