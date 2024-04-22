import React from 'react';
import styled from "styled-components";

const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  background-color:#78BCC4;

`;
const Title = styled.h1`
  font-size: 2.5rem;
  font-weight: 800;
  color: #F7F8F3;
`

const Header = () => {
    return (
      <Container>
        <Title>Get Jobs</Title>
        <img
          src="https://cdn-icons-png.flaticon.com/512/4017/4017739.png"
          alt="a"
          style={{ width: "5%", color: "#F7F8F3" }}
        />
      </Container>
    );
}

export default Header;
