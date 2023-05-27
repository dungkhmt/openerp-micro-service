import {LoadingButton} from "@mui/lab";
import React, {useState} from "react";
import HustContainerCard from "../common/HustContainerCard";
import {Button} from "@mui/material";
import XLSX from "xlsx";

function UploadUser(){
  const [file, setFile] = useState(null);

  const [loading, setLoading] = useState(false);

  function onFileChange(event) {
    setFile(event.target.files[0]);
  }

  const handleUploadExcelUserList = () => {
    const reader = new FileReader();

    reader.onload = (e) => {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: 'array' });

      // Assuming the first sheet in the workbook is the one you want to read
      const worksheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

      // Assuming the column order is: Username, Email, Firstname, Lastname, Password
      const [headerRow, ...dataRows] = jsonData;

      dataRows.forEach((row) => {
        // Call your function for each row passing the extracted values
        // replace 'yourFunction' with the actual function you want to call
        addUser(row);
      });
    };

    reader.readAsArrayBuffer(file);
  }

  const addUser = () => {
    // const [username, email, firstname, lastname, password] = row;
    console.log(row);
  }

  return (
    <HustContainerCard>
      <input type="file" id="selected-upload-file" onChange={onFileChange}/>
      <Button onClick={handleUploadExcelUserList}>Upload</Button>
    </HustContainerCard>
  )
}

export default UploadUser;