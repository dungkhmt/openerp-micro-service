// import React from "react";
// import {useParams} from "react-router";
// import {Button, Grid} from "@material-ui/core";
// import {request} from "../../../api";
//
// export default function SubmitSolutionOutput() {
//   const params = useParams();
//   const contestId = params.contestId;
//   const problemId = params.problemId;
//   const testCaseId = params.testCaseId;
//   const [filename, setFilename] = React.useState("");
//   const [isProcessing, setIsProcessing] = React.useState(false);
//   const [score, setScore] = React.useState(0);
//
//   function onFileChange(event) {
//     setFilename(event.target.files[0]);
//     console.log(event.target.files[0].name);
//   }
//
//   const onInputChange = (event) => {
//     let name = event.target.value;
//     setFilename(name);
//   };
//
//   const handleFormSubmit = (event) => {
//     event.preventDefault();
//     let body = {
//       testCaseId: testCaseId,
//       problemId: problemId,
//       contestId: contestId,
//     };
//     let formData = new FormData();
//     formData.append("inputJson", JSON.stringify(body));
//     formData.append("file", filename);
//
//     const config = {
//       headers: {
//         "content-Type": "multipart/form-data",
//       },
//     };
//
//     request(
//       "post",
//       "/submissions/solution-output",
//       (res) => {
//         res = res.data;
//         setIsProcessing(false);
//         console.log("result submit = ", res);
//         setScore(res.score);
//       },
//       {
//         onError: (e) => {
//           setIsProcessing(false);
//           console.error(e);
//         },
//       },
//       formData,
//       config
//     );
//   };
//
//   return (
//     <div>
//       SubmitSolutionOutput {contestId}:{problemId}:{testCaseId}
//       <form onSubmit={handleFormSubmit}>
//         <Grid container spacing={1} alignItems="flex-end">
//           <Grid item xs={2}>
//             <Button
//               color="primary"
//               type="submit"
//               onChange={onInputChange}
//               width="100%"
//             >
//               UPLOAD
//             </Button>
//           </Grid>
//
//           <input
//             type="file"
//             id="selected-upload-file"
//             onChange={onFileChange}
//           />
//         </Grid>
//       </form>
//       Score: {score}
//     </div>
//   );
// }
