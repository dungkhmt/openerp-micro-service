import { Row, Col, Button } from "react-bootstrap";
import { FaLinkedin, FaGithubSquare, FaFacebookSquare } from "react-icons/fa";
import "./CvTemplate1.css";
import { formatDateToMMYYYY } from "../../../utils/formatDateToMMYYYY";
import parse from "html-react-parser";

const CvTemplate1 = (props) => {
  const renderWorkingExperience = (
    workingExperiences
  ) => {
    if (workingExperiences && workingExperiences.length) {
      return (
        <Row
          className="border-top border-dark w-100 px-4"
          style={{ marginLeft: 0 }}
        >
          <h1
            className="text-center text-dark w-100 pt-4 font-weight-bold"
            style={{ fontWeight: 700, fontSize: "25px" }}
          >
            Professional Info
          </h1>
          <Col className="py-4 mx-4">
            {workingExperiences.map((uWE, id) => {
              return (
                <div>
                  <h3 style={{ fontWeight: 700, fontSize: "25px" }}>
                    {id + 1}.{uWE.workingPosition}
                  </h3>
                  <h6
                    className="text-dark font-weight-bold"
                    style={{ fontWeight: 700, fontSize: "25px" }}
                  >
                    {uWE.companyName}
                  </h6>
                  <p
                    className="text-dark font-weight-bold"
                    style={{ fontWeight: 700 }}
                  >
                    {uWE.startingTime && formatDateToMMYYYY(uWE.startingTime)}
                    {uWE.startingTime && uWE.endingTime ? "-" : ""}
                    {uWE.endingTime && formatDateToMMYYYY(uWE.endingTime)}
                  </p>
                  <p
                    className="text-dark font-weight-normal"
                    style={{ whiteSpace: "pre-line" }}
                  >
                    {parse(uWE.responsibility || "")}
                  </p>
                </div>
              );
            })}
          </Col>
        </Row>
      );
    }
  };

  const renderEducation = (educations) => {
    if (educations && educations.length) {
      return (
        <Row
          className="border-top border-dark w-100 px-4"
          style={{ marginLeft: 0 }}
        >
          <h1
            className="text-center text-dark w-100 pt-4 font-weight-bold"
            style={{ fontWeight: 700, fontSize: "25px" }}
          >
            Educational Info
          </h1>
          <Col className="py-4 mx-4">
            {educations.map((education, id) => {
              return (
                <div>
                  <h3
                    className="text-dark font-weight-bold"
                    style={{ fontWeight: 700, fontSize: "25px" }}
                  >
                    {id + 1}.{education.schoolName}
                  </h3>
                  <h6
                    className="text-dark font-weight-bold"
                    style={{ fontWeight: 700 }}
                  >
                    {education.major}
                  </h6>
                  <p
                    className="text-dark font-weight-bold"
                    style={{ fontWeight: 700 }}
                  >
                    {education.startingTime &&
                      formatDateToMMYYYY(education.startingTime)}
                    {education.startingTime && education.endingTime ? "-" : ""}
                    {education.endingTime &&
                      formatDateToMMYYYY(education.endingTime)}
                  </p>
                  <p
                    className="text-dark font-weight-normal"
                    style={{ whiteSpace: "pre-line" }}
                  >
                    {parse(education.description || "")}
                  </p>
                </div>
              );
            })}
          </Col>
        </Row>
      );
    }
  };

  return (
    <div className="mt-4 mb-2">
      <main id="fileToPrint" className="resume-section">
        <Row className="w-100" style={{ marginLeft: 0 }}>
          <Col sm={12} md={12} className="py-4 px-4">
            <h1
              className="text-center font-weight-bold"
              style={{ fontWeight: "700", fontSize: "30px" }}
            >
              {props.name}
            </h1>
            <h5 className="text-center">{props.profession}</h5>
            <div className="w-100 d-flex flex-column justify-content-between user-social-icons">
              {props.linkedInLink && (
                <div className="mt-2">
                  <FaLinkedin size={28} />
                  <a href={props.linkedInLink} className="ms-2 text-dark">
                    {
                      props.linkedInLink.split("/")[
                      props.linkedInLink.split("/").length - 2
                      ]
                    }
                  </a>
                </div>
              )}
              {props.facebookLink && (
                <div className="mt-2">
                  <FaFacebookSquare size={28} />
                  <a href={props.facebookLink} className="ms-2 text-dark">
                    {
                      props.facebookLink.split("/")[
                      props.facebookLink.split("/").length - 1
                      ]
                    }
                  </a>
                </div>
              )}
              {props.gitHubLink && (
                <div className="mt-2">
                  <FaGithubSquare size={28} />
                  <a href={props.gitHubLink} className="ms-2 text-dark">
                    {
                      props.gitHubLink.split("/")[
                      props.gitHubLink.split("/").length - 1
                      ]
                    }
                  </a>
                </div>
              )}
            </div>
          </Col>
          {props.profileDescription && (
            <Col sm={12} md={6}>
              <div className="py-4 text-dark">
                {parse(props.profileDescription)}
              </div>
            </Col>
          )}
        </Row>
        <Row
          className="border-top border-dark w-100 px-4 "
          style={{ marginLeft: 0 }}
        >
          <h1
            className="text-center text-dark w-100 pt-4 font-weight-bold"
            style={{ fontWeight: 700, fontSize: "25px" }}
          >
            General Info
          </h1>
          <Col sm={12} md={6} className="my-4">
            <ul>
              {!!props.email && (
                <li className="d-flex align-items-center justify-content-start">
                  <div style={{ fontWeight: 700 }}>Email:</div>
                  <div className="ms-2">{props.email}</div>
                </li>
              )}
              {!!props.location && (
                <li className="d-flex align-items justify-content-start mt-3">
                  <div style={{ fontWeight: 700 }}>Location:</div>
                  <div className="ms-2">{props.location}</div>
                </li>
              )}
            </ul>
          </Col>
          <Col sm={12} md={6} className="my-4">
            <ul>
              {!!props.gender && (
                <li className="d-flex align-items justify-content-start">
                  <div style={{ fontWeight: 700, marginLeft: 400}}>Gender:</div>
                  <div className="ms-2">{props.gender}</div>
                </li>
              )}
              {!!props.mobilePhone && (
                <li className="d-flex align-items-center justify-content-start mt-3">
                  <div style={{ fontWeight: 700, marginLeft: 400 }}>Phone Number:</div>
                  <div className="ms-2">{props.mobilePhone}</div>
                </li>
              )}
            </ul>
          </Col>
        </Row>
        {renderWorkingExperience(props.workingExperience)}
        {renderEducation(props.education)}
        {props.skillDescription && (
          <Row
            className="px-2 border-top border-dark w-100"
            style={{ marginLeft: 0 }}
          >
            <h1
              className="text-center text-dark w-100 pt-4 font-weight-bold"
              style={{
                fontWeight: 700,
                whiteSpace: "pre-line",
                fontSize: "25px",
              }}
            >
              Skills Info
            </h1>
            <Col className="py-4 mx-5">
            {props.skillDescription.map((skill, id) => (
              <>
                <h3
                  className="text-dark font-weight-bold"
                  style={{ fontWeight: 700, fontSize: "25px" }}
                >
                  {id + 1}.{skill.skillName}
                </h3>
                <div className="py-3" style={{ whiteSpace: "pre-line" }}>
                  {" score: " + skill.score}
                </div>
              </>
            )
            )}
            </Col>
          </Row>
        )}
      </main>
      <Row className="d-flex justify-content-around">
        <Col sm={12} md={5} className="my-4">
          <Button
            variant="success"
            className="py-2 my-3 text-white font-weight-bold"
            style={{ width: "100%" }}
            onClick={props.handleSaveCVButton}
          >
            Save
          </Button>
        </Col>
        <Col sm={12} md={5} className="my-4">
          <Button
            variant="dark"
            className="py-2 my-3 text-white font-weight-bold"
            style={{ width: "100%" }}
            onClick={props.handleBackToEditButton}
          >
            Back to edit
          </Button>
        </Col>
      </Row>
    </div>
  );
};

export default CvTemplate1;