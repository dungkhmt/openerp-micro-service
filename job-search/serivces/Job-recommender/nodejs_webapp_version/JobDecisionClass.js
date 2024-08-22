class JobDecisionClass {
  constructor(
    title_score,
    address_score,
    salary_score,
    level_score,
    experience_score,
    postedTime_score,
    skill_score,
    url,
    score = 0
  ) {
    this.title_score = title_score;
    this.address_score = address_score;
    this.salary_score = salary_score;
    this.level_score = level_score;
    this.experience_score = experience_score;
    this.postedTime_score = postedTime_score;
    this.skill_score = skill_score;
    this.url = url;
    this.score = score;
  }

  displayDetails() {
    console.log(`Title: ${this.title}`);
    console.log(`Address: ${this.address}`);
    console.log(`Salary: ${this.salary}`);
    console.log(`Level: ${this.level}`);
    console.log(`Experience: ${this.experience}`);
    console.log(`Posted Time: ${this.postedTime}`);
    console.log(`Skill: ${this.skill}`)
    console.log(`URL: ${this.url}`);
  }
}

module.exports = JobDecisionClass;
