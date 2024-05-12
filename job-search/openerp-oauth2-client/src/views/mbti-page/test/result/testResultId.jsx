import { useEffect, useState } from "react";
import { Option, AsyncData, Result } from "@swan-io/boxed";
import { Flex, Text } from "@chakra-ui/react";
import { useParams } from 'react-router-dom';

import MainLayout from "../../../../components/layout/main-layout";
import TestResult from "../../../../components/test/test-result";
import TestResultTableOfContent from "../../../../components/test/test-result-table-of-content";
import TestResultStats from "../../../../components/test/test-result-stats";
import {
  TestResult as ITestResult,
  getSavedTestResult,
} from "../../../../lib/personality-test";

export default function TestResultPage() {
  let { id } = useParams();
  let testResultId = id
  const [testResult, setTestResult] = useState(
    AsyncData.NotAsked()
  );

  useEffect(() => {
    setTestResult(AsyncData.Loading());
    const id = parseInt(testResultId);
    getSavedTestResult(id)
    .tap((result) => {
      setTestResult(AsyncData.Done(result))
    }
    );
  }, [testResultId]);

  return (
    <MainLayout>
      {testResult.match({
        NotAsked: () => <Text>Loading</Text>,
        Loading: () => <Text>Loading</Text>,
        Done: (result) =>
          result.match({
            Error: () => <Text>Something went wrong! Please refresh!</Text>,
            Ok: (value) =>
              value.match({
                Some: (data) => (
                  <Flex
                    h="full"
                    direction={{
                      base: "column",
                      lg: "row",
                    }}
                  >
                    <TestResultStats testResult={data} />
                    <TestResult testResult={data} />
                    <TestResultTableOfContent />
                  </Flex>
                ),
                None: () => <Text>No Data</Text>,
              }),
          }),
      })}
    </MainLayout>
  );
}
