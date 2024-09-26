const calculateCosineSimilarityFromRawString = require("./CosineSimilarity");

const calculateTitle = (companyJobTitle, userJobtitle) => {
  return calculateCosineSimilarityFromRawString(companyJobTitle, userJobtitle);
};

const calculateAddress = (companyAddress, userAddress) => {
  try {
    if (companyAddress.includes(userAddress)) return 1;
    return 0;
  } catch (exception) {
    return 0;
  }
};

const calculateSalary = (companySalary, userSalary) => {
  if (companySalary >= userSalary) return 1;
  if (companySalary === "0") return 0.5;
  if (companySalary === "" ) return 0;
  return 1 - Math.abs(companySalary - userSalary) / userSalary;
};

const calculateLevel = (companyLevel, userLevel) => {
  return Math.abs(companyLevel - userLevel);
};

const calculateExperience = (companyExperience, userExperience) => {
  return Math.abs(companyExperience - userExperience);
};

const calculateTimeDistance = (postedTime) => {
  const targetDateObject = new Date(
    parseInt(postedTime.split("-")[2]),
    parseInt(postedTime.split("-")[1]) - 1,
    parseInt(postedTime.split("-")[0])
  );
  const currentDate = new Date();
  const timeDifference = targetDateObject - currentDate;
  const daysDifference = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));

  return Math.abs(daysDifference) > 49 ? 49 : Math.abs(daysDifference);
};

const calculateSkillDistance = (userSkills, companySkills) => {
  let matchedSkill = 0
  for (let userSkill of userSkills) {
    for (let companySkill of companySkills) {
      if (userSkill.toLowerCase() == companySkill.toLowerCase()) {
        matchedSkill++
      } 
    }
  }
  return matchedSkill/(companySkills.length)
}

module.exports = {
  calculateAddress,
  calculateExperience,
  calculateTimeDistance,
  calculateLevel,
  calculateTitle,
  calculateSalary,
  calculateSkillDistance
};
