import { ReactNode } from "react";
import { Flex } from "@chakra-ui/react";


export default function MainLayout(props) {
  return (
    <>
      <div>
        <title>MBTI Personality Test</title>
        <meta
          name="description"
          content="MBTI Personality Test"
        />
        <meta
          name="viewport"
          content="width=device-width, initial-scale=1"
        />
        <link
          rel="icon"
          href="/favicon.ico"
        />
      </div>
      <Flex
        as="main"
        w="100%"
        minH="calc(100vh - 80px)"
        justifyContent="center"
        alignItems="center"
      >
        {props.children}
      </Flex>
    </>
  );
}
