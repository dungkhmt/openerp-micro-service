import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { request } from "../api";

const EmployeeDetails = () => {
  const { staffCode } = useParams(); // Get staffCode from URL
  const [employeeDetails, setEmployeeDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    const fetchDetails = async () => {
      const payload = { staff_code: staffCode };
      try {
        request(
          "post",
          "/staff/get-staff-info",
          (res) => {
            setEmployeeDetails(res.data?.data);
            setLoading(false);
          },
          {
            onError: (err) => {
              console.error(err);
              setError(true);
              setLoading(false);
            },
          },
          payload
        );
      } catch (err) {
        console.error(err);
        setError(true);
        setLoading(false);
      }
    };

    fetchDetails();
  }, [staffCode]);

  if (loading) return <p>Loading...</p>;
  if (error || !employeeDetails) return <p>Error: Unable to fetch details.</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h1>Employee Details</h1>
      <h2>{employeeDetails.fullname}</h2>
      <p><strong>Email:</strong> {employeeDetails.email}</p>
      <p><strong>Status:</strong> {employeeDetails.status}</p>
      <p>
        <strong>Department:</strong>{" "}
        {employeeDetails.department?.department_name || "N/A"}
      </p>
      <p>
        <strong>Job Position:</strong>{" "}
        {employeeDetails.job_position?.job_position_name || "N/A"}
      </p>
    </div>
  );
};

export default EmployeeDetails;
