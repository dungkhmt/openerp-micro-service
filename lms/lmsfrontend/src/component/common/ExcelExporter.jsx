import React from "react"
import PropTypes from "prop-types";
import ReactExport from "react-data-export";
import {Button} from "@mui/material";


const ExcelFile = ReactExport.ExcelFile;
const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;

export default function ExcelExporter(props) {
  const { filename, sheets, element } = props;

  return (
    <ExcelFile
      filename={filename}
      element={element}
    >
      {sheets.map((sheet, index) => (
        <ExcelSheet key={index}
                    dataSet={sheet.dataSet}
                    name={sheet.name}/>
      ))}
    </ExcelFile>
  )
}

ExcelExporter.propTypes = {
  filename: PropTypes.string.isRequired,
  sheets: PropTypes.array.isRequired,
  element: PropTypes.element
}

const DefaultExcelExportElement = (
  <Button
    variant="contained"
    style={{ marginLeft: "0px" }}>
    Xuất Excel
  </Button>
)

ExcelExporter.defaultProps = {
  element: DefaultExcelExportElement
}