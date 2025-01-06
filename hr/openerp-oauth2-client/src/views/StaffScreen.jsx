import React, { useState } from "react";
import { Button } from "@mui/material";
import AddStaffModal from "./modals/AddStaffModal"; // Import the AddStaffModal

const EmployeeManagement = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleAddEmployee = (employeeDetails) => {
    console.log("Employee added:", employeeDetails);
    // Perform API call to save the employee data
    setIsModalOpen(false); // Close modal after submission
  };

  return (
    <div className="employee-container">
      <div className="header">
        <h2>Employee Management</h2>
        <Button
          variant="contained"
          color="success"
          onClick={() => setIsModalOpen(true)}
          style={{ marginLeft: "auto" }} // Align to the right
        >
          + Add Employee
        </Button>
      </div>

      {/* Add Employee Modal */}
      <AddStaffModal
        open={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleAddEmployee}
      />
    </div>
  );
};

export default EmployeeManagement;
