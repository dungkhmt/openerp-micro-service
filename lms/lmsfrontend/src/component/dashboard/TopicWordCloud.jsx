import {CardContent, CardHeader} from "@mui/material";
import Card from "@mui/material/Card";
import Typography from "@mui/material/Typography";
import React, {useCallback} from "react";
import WordCloud from "react-d3-cloud"

export default function TopicWordCloud() {

  const words = [
    {text: 'Data Structures', value: 5000},
    {text: 'Linked List', value: 120},
    {text: 'Stack', value: 220},
    {text: 'Queue', value: 240},
    {text: 'Heap', value: 160},
    {text: 'Tree', value: 380},
    {text: 'Graph', value: 80},
    {text: 'Matrix', value: 80},
    {text: 'Searching', value: 1600},
    {text: 'BFS', value: 150},
    {text: 'DFS', value: 240},
    {text: 'NP-hard', value: 290},
    {text: 'Bitwise', value: 30},
    {text: 'Divide & Conquer', value: 140},
    {text: 'Greedy', value: 120},
    {text: 'Sorting', value: 920},
    {text: 'HeapSort', value: 80},
    {text: 'MergeSort', value: 180},
    {text: 'QuickSort', value: 140},
    {text: 'BubbleSort', value: 30},
    {text: 'SelectionSort', value: 120},
    {text: 'Shortest Path', value: 480},
    {text: 'Spanning Tree', value: 20},
    {text: 'Tree Traversal', value: 280},
    {text: 'Bellman-Ford', value: 10},
    {text: 'Dijkstra', value: 40},
    {text: 'Backtracking', value: 160},
    {text: 'Dynamic Programming', value: 360},
    {text: 'Java', value: 520},
    {text: 'C++', value: 580},
    {text: 'Python', value: 210},
    {text: 'ReactJS', value: 160},
    {text: 'Hashing', value: 280},
    {text: 'Software Development', value: 340},
    {text: 'Optimization', value: 820},
    {text: 'Game Theory', value: 40},
    {text: 'Binary Tree', value: 190},
    {text: 'Array', value: 520},
    {text: 'Discrete Math', value: 380},
    {text: 'Probability', value: 120},
    {text: 'OOP', value: 420},
    {text: 'Security', value: 50},
    {text: 'AI', value: 90},
    {text: 'Database', value: 140},
    {text: 'Networking', value: 40},
    {text: 'Operating System', value: 50},
    {text: 'Complexity', value: 390},
    {text: 'User Interface', value: 60},
  ]

  const fontSize = useCallback((word) => Math.log2(word.value) * 4, []);

  return <Card elevation={5} sx={{borderRadius: "18px"}}>
    <CardHeader
      title={<Typography variant="h6" color="#00acc1">Popular Keywords</Typography>}
      sx={{paddingBottom: "4px"}}
    />
    <CardContent sx={{padding: "0 !important"}}>
      <WordCloud
        data={words}
        rotate={0}
        font="Roboto"
        fontWeight="bold"
        fontSize={fontSize}
        padding={5}
        spiral="archimedean"
      />
    </CardContent>
  </Card>;
}