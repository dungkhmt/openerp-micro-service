import { DataGrid } from '@mui/x-data-grid'
import React from 'react'
import { usePlanTableConfig } from './usePlanTableConfig'

const ClassOpenPlanTable = () => {
  return (
    <DataGrid 
        onRowClick={()=>{}}
        rowSelection={true}
        columns={usePlanTableConfig()}
        rows={[]}
        sx={{height: 550}}
    />
  )
}

export default ClassOpenPlanTable