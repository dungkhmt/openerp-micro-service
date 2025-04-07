import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { request } from "@/api";
import AddStaffModal from "./modals/AddStaffModal";
import EditIcon from "@mui/icons-material/Edit";
import { Divider, Tab, Tabs } from "@mui/material";
import Timeline from "@/components/item/TimelineItem";
import SalaryTab from "@/components/tab/SalaryTab";

const EmployeeDetails = () => {
  const { staffCode } = useParams(); 
  const [employeeDetails, setEmployeeDetails] = useState(null);
  const [jobHistory, setJobHistory] = useState([]);
  const [departmentHistory, setDepartmentHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [openEditModal, setOpenEditModal] = useState(false); 
  const [selectedTab, setSelectedTab] = useState("profile"); 

  useEffect(() => {
    const fetchDetails = async () => {
      const payload = { staff_code: staffCode };
      try {
        request(
          "post",
          "/staff/get-staff-info",
          (res) => {
            const data = res.data?.data;
            setEmployeeDetails(data);
            fetchJobHistory(data?.user_login_id);
            fetchDepartmentHistory(data?.user_login_id);
          },
          {
            onError: (err) => {
              console.error(err);
              setError(true);
            },
          },
          payload
        );
      } catch (err) {
        console.error(err);
        setError(true);
      } finally {
        setLoading(false);
      }
    };

    const fetchJobHistory = async (userLoginId) => {
      if (!userLoginId) return;
      const payload = { user_login_id: userLoginId };
      try {
        request(
          "post",
          "/job/get-job-position-history",
          (res) => {
            const history = res.data?.data || [];
            setJobHistory(
              history.map((job) => ({
                title: job.job_position?.name || "N/A",
                fromDate: formatDate(job.from_date),
                thruDate: formatDate(job.thru_date),
              }))
            );
          },
          { onError: (err) => console.error(err) },
          payload
        );
      } catch (err) {
        console.error(err);
      }
    };

    const fetchDepartmentHistory = async (userLoginId) => {
      if (!userLoginId) return;
      const payload = { user_login_id: userLoginId };
      try {
        request(
          "post",
          "/department/get-department-history",
          (res) => {
            const history = res.data?.data || [];
            setDepartmentHistory(
              history.map((dept) => ({
                title: dept.department_model?.department_name || "N/A",
                fromDate: formatDate(dept.from_date),
                thruDate: formatDate(dept.thru_date),
              }))
            );
          },
          { onError: (err) => console.error(err) },
          payload
        );
      } catch (err) {
        console.error(err);
      }
    };

    fetchDetails();
  }, [staffCode]);

  const handleEdit = () => {
    setOpenEditModal(true); // Open the edit modal
  };

  const formatDate = (date) => {
    if (!date) return "Present";
    const options = { year: "numeric", month: "short", day: "numeric" };
    return new Date(date).toLocaleDateString(undefined, options);
  };

  if (loading) return <p>Loading...</p>;
  if (error || !employeeDetails) return <p>Error: Unable to fetch details.</p>;

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
        <div style={{ display: "flex", alignItems: "flex-start", gap: "20px" }}>
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

      {selectedTab === "profile" && (
        <div style={{ display: "flex", gap: "20px" }}>
          <Timeline title="Job History" items={jobHistory} />
          <Timeline title="Department History" items={departmentHistory} />
        </div>
      )}

      {selectedTab === "salary" && (
        <SalaryTab userLoginId={employeeDetails.user_login_id} />
      )}

      {openEditModal && (
        <AddStaffModal
          open={openEditModal}
          onClose={() => setOpenEditModal(false)}
          onSubmit={(updatedData) => {
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
