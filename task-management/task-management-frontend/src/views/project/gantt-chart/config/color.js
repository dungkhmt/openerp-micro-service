export const colors = [
  "#E74C3C",
  "#DA3C78",
  "#7E349D",
  "#0077C0",
  "#07ABA0",
  "#0EAC51",
  "#F1892D",
];

export function getRandomColor() {
  return colors[Math.floor(Math.random() * colors.length)];
}
