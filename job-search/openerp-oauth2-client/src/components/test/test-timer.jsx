import { useState, useEffect } from "react";
import { Flex, Text } from "@chakra-ui/react";
import { FiClock } from "react-icons/fi";
import dayjs from "dayjs";

const SECOND_IN_MILLISECONDS = 1000;

export default function TestTimer() {
  const [elapsedTime, setElapsedTime] = useState(dayjs().minute(0).second(0).millisecond(0));

  useEffect(() => {
    const intervalId = setInterval(() => {
      setElapsedTime(prevTime => prevTime.add(1, 'second'));
    }, SECOND_IN_MILLISECONDS);

    return () => clearInterval(intervalId);
  }, []);

  return (
    <Flex
      width={100}
      px={2}
      columnGap={2}
      justifyContent="center"
      alignItems="center"
      borderColor="blackAlpha.300"
      rounded="md"
    >
      <FiClock size={20} />
      <Text fontWeight="bold">
        {elapsedTime.format('mm : ss')}
      </Text>
    </Flex>
  );
}
