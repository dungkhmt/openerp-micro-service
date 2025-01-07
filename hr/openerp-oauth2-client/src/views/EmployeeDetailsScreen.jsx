import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { request } from "../api";
import AddStaffModal from "./modals/AddStaffModal";
import EditIcon from "@mui/icons-material/Edit";
import { Button, Divider, Tab, Tabs } from "@mui/material";

const EmployeeDetails = () => {
  const { staffCode } = useParams(); // Get staffCode from URL
  const [employeeDetails, setEmployeeDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [openEditModal, setOpenEditModal] = useState(false); // Control modal visibility
  const [selectedTab, setSelectedTab] = useState("profile"); // Tab state

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

  const handleEdit = () => {
    setOpenEditModal(true); // Open the edit modal
  };

  const formatDate = (date) => {
    const options = { year: "numeric", month: "long", day: "numeric" };
    return new Date(date).toLocaleDateString(undefined, options);
  };

  return (
    <div style={{ margin: "auto" }}>
      <div
        style={{
          position: "relative",
          border: "1px solid #ddd",
          borderRadius: "8px",
          padding: "20px",
          boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
          backgroundColor: "#fff",
        }}
      >
        {/* Edit Button */}
        <EditIcon
          onClick={handleEdit}
          style={{
            position: "absolute",
            top: "15px",
            right: "15px",
            cursor: "pointer",
            fontSize: "26px",
            color: "#007bff",
          }}
        />

        {/* Profile Section */}
        <div style={{ display: "flex", alignItems: "flex-start", gap: "20px" }}>
          {/* Avatar */}
          <img
            src={`https://ui-avatars.com/api/?name=${encodeURIComponent(
              employeeDetails.fullname
            )}&background=random&size=100`}
            alt="User Avatar"
            style={{
              borderRadius: "50%",
              border: "2px solid #ddd",
              width: "100px",
              height: "100px",
              objectFit: "cover",
            }}
          />

          {/* Name & Info */}
          <div style={{ flex: 1 }}>
            <h2 style={{ margin: "0 0 10px 0", fontSize: "1.5rem" }}>
              {employeeDetails.fullname}
            </h2>
            <p style={{ margin: "5px 0", fontSize: "1rem", color: "#555" }}>
              {employeeDetails.job_position?.job_position_name || "N/A"}
            </p>
            <Divider style={{ margin: "10px 0" }} />
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 2fr",
                gap: "8px",
              }}
            >
              <p style={{ margin: "5px 0", fontSize: "0.95rem", color: "#555" }}>
                <strong>Email:</strong> {employeeDetails.email}
              </p>
              <p style={{ margin: "5px 0", fontSize: "0.95rem", color: "#555" }}>
                <strong>Department:</strong>{" "}
                {employeeDetails.department?.department_name || "N/A"}
              </p>
              <p style={{ margin: "5px 0", fontSize: "0.95rem", color: "#555" }}>
                <strong>Employee ID:</strong> {employeeDetails.staff_code}
              </p>
              <p style={{ margin: "5px 0", fontSize: "0.95rem", color: "#555" }}>
                <strong>Date of Join:</strong>{" "}
                {formatDate(employeeDetails.date_of_join)}
              </p>
            </div>
          </div>
        </div>
        {/* Status */}
        <p
          style={{
            margin: "15px 0 0",
            fontWeight: "bold",
            color: employeeDetails.status === "ACTIVE" ? "green" : "red",
            textAlign: "right",
          }}
        >
          Status: {employeeDetails.status}
        </p>
      </div>

      <Divider style={{ margin: "20px 0" }} />
      <Tabs
        value={selectedTab}
        onChange={(e, value) => setSelectedTab(value)}
        indicatorColor="primary"
        textColor="primary"
      >
        <Tab label="Profile" value="profile" />
        <Tab label="Salary" value="salary" />
      </Tabs>
      <Divider style={{ margin: "10px 0" }} />
      <div style={{ padding: "10px" }}>
        {selectedTab === "profile" && (
          <div>
            <p>
              <strong>Department:</strong>{" "}
              {employeeDetails.department?.department_name || "N/A"}
            </p>
            <p>
              <strong>Job Position:</strong>{" "}
              {employeeDetails.job_position?.job_position_name || "N/A"}
            </p>
          </div>
        )}
        {selectedTab === "salary" && (
          <div>
            <h3>Salary Information</h3>
            <p>Salary details are currently unavailable.</p>
          </div>
        )}
      </div>

      {/* Edit Staff Modal */}
      {openEditModal && (
        <AddStaffModal
          open={openEditModal}
          onClose={() => setOpenEditModal(false)}
          onSubmit={(updatedData) => {
            // Update employee details after editing
            setEmployeeDetails((prev) => ({ ...prev, ...updatedData }));
            setOpenEditModal(false);
          }}
          initialValues={employeeDetails}
        />
      )}
    </div>
  );
};

export default EmployeeDetails;
