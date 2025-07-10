import React, { useState, useEffect } from 'react';
import {
  Box, Modal, Typography
} from '@mui/material';
import { MaterialReactTable } from 'material-react-table';

const ProductsReport = () => {
  const [productsTableData, setProductsTableData] = useState([]);
  const [productHistoryMap, setProductHistoryMap] = useState({});
  const [productHistory, setProductHistory] = useState([]);
  const [isOpenModal, setOpenModal] = useState(false);

  useEffect(() => {
    const mockProducts = [
      {
        productId: 1,
        productName: 'Áo thun',
        totalQuantity: 100,
        totalImportPrice: '10,000,000đ',
        totalExportPrice: '15,000,000đ',
        price: '150,000đ'
      },
      {
        productId: 2,
        productName: 'Quần jean',
        totalQuantity: 50,
        totalImportPrice: '7,000,000đ',
        totalExportPrice: '11,000,000đ',
        price: '220,000đ'
      }
    ];
    const mockHistoryMap = {
      1: [
        {
          productName: 'Áo thun',
          quantity: 30,
          effectiveDateStr: '2024-01-01',
          type: 'Nhập kho',
        },
        {
          productName: 'Áo thun',
          quantity: 20,
          effectiveDateStr: '2024-02-01',
          type: 'Xuất kho',
        },
      ],
      2: [
        {
          productName: 'Quần jean',
          quantity: 50,
          effectiveDateStr: '2024-01-15',
          type: 'Nhập kho',
        }
      ]
    };

    setProductsTableData(mockProducts);
    setProductHistoryMap(mockHistoryMap);
  }, []);

  const productColumns = [
    { accessorKey: 'productName', header: 'Tên sản phẩm' },
    { accessorKey: 'totalQuantity', header: 'Số lượng hàng tồn' },
    { accessorKey: 'totalImportPrice', header: 'Tổng giá trị hàng khi nhập' },
    { accessorKey: 'totalExportPrice', header: 'Tổng giá trị hàng khi bán' },
    { accessorKey: 'price', header: 'Giá bán hiện tại' },
    {
      id: 'actions',
      header: 'Thao tác',
      Cell: ({ row }) => (
        <button
          onClick={() => {
            const id = row.original.productId;
            setProductHistory(productHistoryMap[id] || []);
            setOpenModal(true);
          }}
        >
          Lịch sử
        </button>
      ),
    },
  ];

  const historyColumns = [
    { accessorKey: 'productName', header: 'Sản phẩm' },
    { accessorKey: 'quantity', header: 'Số lượng' },
    { accessorKey: 'effectiveDateStr', header: 'Thời điểm' },
    { accessorKey: 'type', header: 'Hành động' },
  ];

  return (
    <>
      <Typography variant="h6" gutterBottom>Danh sách hàng tồn kho</Typography>
      <MaterialReactTable
        columns={productColumns}
        data={productsTableData}
        enableSorting
        enableGlobalFilter
        paginationDisplayMode="pages"
      />

      <Modal
        open={isOpenModal}
        onClose={() => setOpenModal(false)}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: '80%',
          maxHeight: '80%',
          overflowY: 'auto',
          bgcolor: 'background.paper',
          border: '2px solid #000',
          boxShadow: 24,
          p: 4,
        }}>
          <Typography variant="h6" gutterBottom>Lịch sử sản phẩm</Typography>
          <MaterialReactTable
            columns={historyColumns}
            data={productHistory}
            enableSorting
            enableGlobalFilter
            paginationDisplayMode="pages"
          />
        </Box>
      </Modal>
    </>
  );
};

export default ProductsReport;
