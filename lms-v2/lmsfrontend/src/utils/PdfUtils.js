// import pdfMake from "pdfmake/build/pdfmake";
// import pdfFonts from "pdfmake/build/vfs_fonts";
// import {format} from "date-fns";
//
// pdfMake.vfs = pdfFonts.pdfMake.vfs;
//
// function header(orderCode, orderDate) {
//   return [
//     {
//       text: "Công ty TNHH Daily Opt",
//       bold: true,
//       fontSize: 16,
//       alignment: "center",
//       margin: [0, 0, 0, 20],
//     },
//
//     [
//       {
//         text: "ĐƠN HÀNG",
//         style: "invoiceTitle",
//         width: "*",
//       },
//       {
//         stack: [
//           {
//             columns: [
//               {
//                 text: "Mã đơn hàng:",
//                 style: "invoiceSubTitle",
//                 width: 110,
//               },
//               {
//                 text: orderCode,
//                 style: "invoiceSubValue",
//                 width: "*",
//               },
//               {
//                 text: "",
//                 width: 250,
//               },
//             ],
//           },
//           {
//             columns: [
//               {
//                 text: "Ngày đơn hàng:",
//                 style: "invoiceSubTitle",
//                 width: 110,
//               },
//               {
//                 text: orderDate,
//                 style: "invoiceSubValue",
//                 width: "*",
//               },
//               {
//                 text: "",
//                 width: 250,
//               },
//             ],
//           },
//         ],
//       },
//     ],
//   ];
// }
//
// function customerAndVendorInfo(
//   vendorId,
//   vendorName,
//   customerCode,
//   customerName
// ) {
//   return {
//     columns: [
//       [
//         {
//           text: "Mã nhà cung cấp",
//           style: "invoiceBillingTitle",
//         },
//         {
//           text: vendorId + "",
//           style: "invoiceBillingDetails",
//         },
//         {
//           text: "Tên nhà cung cấp",
//           style: "invoiceBillingAddressTitle",
//         },
//         {
//           text: vendorName + "",
//           style: "invoiceBillingAddress",
//         },
//       ],
//       [
//         {
//           text: "Mã khách hàng",
//           style: "invoiceBillingTitle",
//         },
//         {
//           text: customerCode + "",
//           style: "invoiceBillingDetails",
//         },
//         {
//           text: "Tên khách hàng",
//           style: "invoiceBillingAddressTitle",
//         },
//         {
//           text: customerName + "",
//           style: "invoiceBillingAddress",
//         },
//       ],
//     ],
//   };
// }
//
// function orderItems(orderItems) {
//   return {
//     table: {
//       // headers are automatically repeated if the table spans over multiple pages
//       // you can declare how many rows should be treated as headers
//       headerRows: 1,
//       widths: ["*", 40, "auto", 40, "auto", 0],
//
//       body: [
//         // Table Header
//         [
//           {
//             text: "Sản phẩm",
//             style: "itemsHeader",
//           },
//           {
//             text: "Số lượng",
//             style: ["itemsHeader", "center"],
//           },
//           {
//             text: "UOM",
//             style: ["itemsHeader", "center"],
//           },
//           {
//             text: "Giá",
//             style: ["itemsHeader", "center"],
//           },
//           {
//             text: "Tổng tiền",
//             style: ["itemsHeader", "center"],
//           },
//         ],
//         // Items
//         // Item 1
//         ...orderItems.map((orderItem) => [
//           [
//             {
//               text: orderItem["orderItemId"],
//               style: "itemTitle",
//             },
//             {
//               text: orderItem["productName"],
//               style: "itemSubTitle",
//             },
//           ],
//           {
//             text: orderItem["quantity"],
//             style: "itemNumber",
//           },
//           {
//             text: orderItem["uom"],
//             style: "itemText",
//           },
//           {
//             text: orderItem["unitPrice"],
//             style: "itemNumber",
//           },
//           {
//             text: orderItem["totalItemPrice"],
//             style: "itemTotal",
//           },
//         ]),
//       ],
//     },
//   };
// }
//
// function totalCost(totalCost) {
//   return {
//     table: {
//       // headers are automatically repeated if the table spans over multiple pages
//       // you can declare how many rows should be treated as headers
//       headerRows: 0,
//       widths: ["*", 80],
//
//       body: [
//         // Total
//         [
//           {
//             text: "Tổng tiền",
//             style: "itemsFooterTotalTitle",
//           },
//           {
//             text: totalCost,
//             style: "itemsFooterTotalValue",
//           },
//         ],
//       ],
//     }, // table
//     layout: "lightHorizontalLines",
//   };
// }
//
// let styles = {
//   // Document Header
//   documentHeaderLeft: {
//     fontSize: 10,
//     margin: [5, 5, 5, 5],
//     alignment: "left",
//   },
//   documentHeaderCenter: {
//     fontSize: 10,
//     margin: [5, 5, 5, 5],
//     alignment: "center",
//   },
//   documentHeaderRight: {
//     fontSize: 10,
//     margin: [5, 5, 5, 5],
//     alignment: "right",
//   },
//   // Document Footer
//   documentFooterLeft: {
//     fontSize: 10,
//     margin: [5, 5, 5, 5],
//     alignment: "left",
//   },
//   documentFooterCenter: {
//     fontSize: 10,
//     margin: [5, 5, 5, 5],
//     alignment: "center",
//   },
//   documentFooterRight: {
//     fontSize: 10,
//     margin: [5, 5, 5, 5],
//     alignment: "right",
//   },
//   // Invoice Title
//   invoiceTitle: {
//     fontSize: 24,
//     bold: true,
//     alignment: "center",
//     margin: [0, 0, 0, 40],
//   },
//   // Invoice Details
//   invoiceSubTitle: {
//     fontSize: 12,
//     alignment: "left",
//   },
//   invoiceSubValue: {
//     fontSize: 12,
//     alignment: "right",
//   },
//   // Billing Headers
//   invoiceBillingTitle: {
//     fontSize: 14,
//     bold: true,
//     alignment: "left",
//     margin: [0, 20, 0, 5],
//   },
//   // Billing Details
//   invoiceBillingDetails: {
//     alignment: "left",
//   },
//   invoiceBillingAddressTitle: {
//     margin: [0, 7, 0, 3],
//     bold: true,
//   },
//   invoiceBillingAddress: {},
//   // Items Header
//   itemsHeader: {
//     margin: [0, 5, 0, 5],
//     bold: true,
//     alignment: "center",
//   },
//   // Item Title
//   itemTitle: {
//     bold: true,
//   },
//   itemSubTitle: {
//     italics: true,
//     fontSize: 11,
//   },
//   itemNumber: {
//     margin: [0, 5, 0, 5],
//     alignment: "center",
//   },
//   itemText: {
//     margin: [0, 5, 0, 5],
//     alignment: "center",
//   },
//   itemTotal: {
//     margin: [0, 5, 0, 5],
//     bold: true,
//     alignment: "center",
//   },
//
//   // Items Footer (Subtotal, Total, Tax, etc)
//   itemsFooterSubTitle: {
//     margin: [0, 5, 0, 5],
//     bold: true,
//     alignment: "right",
//   },
//   itemsFooterSubValue: {
//     margin: [0, 5, 0, 5],
//     bold: true,
//     alignment: "center",
//   },
//   itemsFooterTotalTitle: {
//     margin: [0, 5, 0, 5],
//     bold: true,
//     alignment: "right",
//   },
//   itemsFooterTotalValue: {
//     margin: [0, 5, 0, 5],
//     bold: true,
//     alignment: "center",
//   },
//   signaturePlaceholder: {
//     margin: [0, 70, 0, 0],
//   },
//   signatureName: {
//     bold: true,
//     alignment: "center",
//   },
//   signatureJobTitle: {
//     italics: true,
//     fontSize: 10,
//     alignment: "center",
//   },
//   notesTitle: {
//     fontSize: 10,
//     bold: true,
//     margin: [0, 50, 0, 3],
//   },
//   notesText: {
//     fontSize: 10,
//   },
//   center: {
//     alignment: "center",
//   },
// };
//
// export function printOrderPdf(order) {
//   const docDefinition = {
//     content: [
//       ...header(
//         order["orderId"],
//         format(new Date(order["orderDate"]), "dd/MM/yyyy HH:mm:ss")
//       ),
//       customerAndVendorInfo(
//         order["vendorId"],
//         order["vendorName"],
//         order["customerId"],
//         order["customerName"]
//       ),
//
//       "\n\n",
//       orderItems(order["orderItems"]),
//       totalCost(
//         order["orderItems"].reduce((acc, val) => acc + val["totalItemPrice"], 0)
//       ),
//     ],
//     styles: styles,
//     defaultStyle: {
//       columnGap: 20,
//     },
//   };
//   pdfMake
//     .createPdf(docDefinition)
//     .download("order_" + order["orderId"] + ".pdf");
// }
