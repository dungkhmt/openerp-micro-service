import parse from "html-react-parser";

export function parseHTMLToString(value){
  if(value){
    return parse(value)
  }
  return ''
}
