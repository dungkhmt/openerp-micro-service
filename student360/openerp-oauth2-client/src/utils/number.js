export function formatDecimal(number, decimal) {
  const roundedNumber = Math.round(number); 
  const isDecimalZero = number % 1 === 0; 

  if (isDecimalZero) {
    return roundedNumber.toFixed(0); 
  } else {
    return number.toFixed(decimal); 
  }
}
