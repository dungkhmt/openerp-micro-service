import React from 'react';
import styled from "styled-components";
import GradeIcon from "@mui/icons-material/Grade";
import LocalAtmIcon from "@mui/icons-material/LocalAtm";
import FmdGoodIcon from "@mui/icons-material/FmdGood";
import WorkIcon from "@mui/icons-material/Work";
import SchoolIcon from "@mui/icons-material/School";
import BusinessIcon from "@mui/icons-material/Business";
import WorkspacePremiumIcon from "@mui/icons-material/WorkspacePremium";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import SportsScoreIcon from "@mui/icons-material/SportsScore";
const Container = styled.div`
  height: 36rem;
  width: 87rem;
  box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;
  border-radius: 2%;
  padding: 0.3rem;
  display: flex;
`;
const Img = styled.img`
  object-fit: cover;
  width: 30%;
  height: 100%;
`;
const Show = styled.div`
width: 60%;
margin-left: 2rem;
margin-top: 1rem;

`
const ShowInfo = styled.div`
 display: flex;
 align-items: center;
 position: relative;
`
const ShowInfoIcon = styled.div`
  font-size: 1.2rem;
  color: #2467bf;
  margin-right: 1rem;
  margin-bottom: 1.3rem;
`;

const ShowInfoTitle = styled.span`
 font-size:1.2rem;
 overflow: auto;
 margin-bottom: 0.5rem;
 height: 2.5rem;
`;
const ShowInfoLink = styled.a`
position: absolute;
right: -2rem;
top: -3.5rem;
font-size: 1.2rem;
color: #2467BF;

`

const apiUrl = process.env.JOB_SEARCH_HOST;

const Item = ({job}) => {

    return (
      <Container>
        <Img src="https://play-lh.googleusercontent.com/bJcGJX4Tv-xmeMbomvRC5A-Zcp3zW2bJ3KmWVHk1hSnIhCETcQlgwzTG0lpM8PrW4Ac" />
        <Show>
          <ShowInfo>
            <ShowInfoIcon>
              <GradeIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>Title : {job.title}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <LocalAtmIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>
              Salary : {job.salary ? job.salary : "Chưa có thông tin"}
            </ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <FmdGoodIcon />
            </ShowInfoIcon>
            <ShowInfoTitle style={{height: "60px"}}>Address : {job.address}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <SchoolIcon />
            </ShowInfoIcon>
            <ShowInfoTitle  style={{height: "120px"}} >Yêu cầu : {job.experience}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <BusinessIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>Loại Công Ty : {job.typeOfCompany}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <WorkspacePremiumIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>Trình độ : {job.level}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <WorkIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>Hình Thức Làm Việc : {job.jobType}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <ManageAccountsIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>Skill keyword : {job.major}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoIcon>
              <SportsScoreIcon />
            </ShowInfoIcon>
            <ShowInfoTitle>Score : {job.score}</ShowInfoTitle>
          </ShowInfo>
          <ShowInfo>
            <ShowInfoLink target='_blank' href={job.detailURL}>Link Job Here.</ShowInfoLink>
          </ShowInfo>
        </Show>
      </Container>
    );
}

export default Item;
