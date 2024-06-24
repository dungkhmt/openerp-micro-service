
function convertSalary(amountString) {
    const numericAmount = parseFloat(amountString.replace(/[^0-9.-]+/g, ""));

    // Assuming VND if more than 6 characters, USD if 3 or 4 characters
    if (amountString.length > 6) {
      // Already in VND
      return numericAmount;
    } else if (amountString.length >= 2 && amountString.length <= 4) {
      // Convert from USD to VND (assuming 1 USD = 23,000 VND, change as needed)
      const vndAmount = numericAmount * 23000;
      return vndAmount;
    } else {
      // Unknown format, return NaN or handle accordingly
      return 0;
    }
  }

const levelConstant = {
    "internship" : 1,
    "fresher": 2,
    "junior": 3,
    "middle": 4,
    "senior": 5
}

function convertLevel(level) {
    let returnValue = 0;
    const levelArray = level.toLowerCase().split(", ");
    for(let i of levelArray) {
        if (levelConstant[i]) {
            returnValue += levelConstant[i];
          } 
    }
    return returnValue*1.0/levelArray.length;
}

function convertExperience(exp) {
  const firstDigitMatch = exp.match(/\d/); // Use \d to match a single digit
  const result = firstDigitMatch ? Number(firstDigitMatch[0]) : 0;
  return result;
}


module.exports = {
  convertLevel,
  convertExperience,
  convertSalary,
};