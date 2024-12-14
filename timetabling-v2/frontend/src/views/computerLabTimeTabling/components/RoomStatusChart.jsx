import { Tooltip } from "@mui/material";
import * as d3 from "d3";
import { useEffect, useRef } from "react";

const RoomStatusChart = ({ data, classList, roomList }) => {
  const svgRef = useRef();

  useEffect(() => {
    const cell_width = 20;
    const cell_height = 30;
    const margin = { top: 50, right: 50, bottom: 50, left: 50 };
    const width = cell_width * 12 * 7;
    const height = cell_height * roomList.length;

    const svg = d3
      .select(svgRef.current)
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom);
    const svgInner = svg
      .append("g")
      .attr("transform", `translate(${margin.left},${margin.top})`);

    const x = d3
      .scaleBand()
      .range([0, width])
      .domain(data.map((d) => d.x).filter((x) => x != 0));

    const y = d3
      .scaleBand()
      .range([height, 0])
      .domain(data.map((d) => d.y));

    var popup = d3
      .select("#div_template")
      .append("div")
      .style("opacity", 0)
      .attr("class", "tooltip")
      .style("position", "absolute")
      .style("background-color", "white")
      .style("border", "solid")
      .style("border-width", "2px")
      .style("border-radius", "5px")
      .style("padding", "5px");

    const max = d3.max(data, (d) => d.z.value);
    const color = d3
      .scaleSequential(d3.interpolateRgb("white", "blue"))
      .domain([0, max]);
    svgInner
      .selectAll()
      .data(data, (d) => `${d.x}:${d.y}`)
      .enter()
      .append("rect")
      .attr("x", (d) => x(d.x))
      .attr("y", (d) => y(d.y))
      .attr("width", cell_width)
      .attr("height", cell_height)
      .style("fill", (d) => (d.z.value === 0 ? "white" : color(d.z.value)))
      .style("opacity", 1)
      .on("mouseover", function (d, i) {
        if (i.z.value != 0) {
          console.log(classList[i.z.class_index]?.note);
        }
        popup.style("opacity", 1);
        d3.select(this).style("stroke", "black").style("opacity", 1);
      })
      .on("mousemove", function (d, i) {
        const rect = document
          .getElementById("div_template")
          .getBoundingClientRect();
        if (i.z.value == 0) {
          popup.style("opacity", 0);
          return;
        }
        var text = `${classList[i.z.class_index]?.note} - SL: ${i.z.value}`;
        popup
          .html(text)
          .style("opacity", 1)
          .style("left", d3.pointer(d)[0] + rect.x + 70 + "px")
          .style("top", d3.pointer(d)[1] + rect.y + window.scrollY + "px");
      })
      .on("mouseleave", function (d, i) {
        popup.style("opacity", 0);
        d3.select(this).style("stroke", "none");
      });

    function convertToDay(num) {
      const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
      num -= 1;
      if (num % 12 == 0) return days[Math.floor(num / 12)];
      if ((num % 12) + 1 > 6) return (num % 12) + 1 - 6;
      return (num % 12) + 1;
    }
    // Add X Axis
    svgInner
      .append("g")
      .attr("transform", `translate(0,${height})`)
      .call(d3.axisBottom(x).tickFormat((x) => convertToDay(x)));

    // Add Y Axis
    svgInner
      .append("g")
      .call(d3.axisLeft(y).tickFormat((d) => roomList[d].name));
  }, [data]);

  return (
    <div className="svg-container" id="div_template" style={svgStyle}>
      <svg ref={svgRef}></svg>
    </div>
  );
};
const svgStyle = {
  width: "100%",
  overflowX: "auto",
};
export default RoomStatusChart;
