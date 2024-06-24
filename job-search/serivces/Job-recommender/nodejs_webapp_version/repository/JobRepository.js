const Job = require("../db-models/jobs")

const getAllJobs = async (req, res) => {
    const jobsData = await Job.find({}).select("id title company skills_arr addresses job_types_str job_levels_str detail_url salary refreshed requirements_arr")
    return jobsData.map(job => convertOriginalJobObject(job._doc));
}

const convertOriginalJobObject = (job) => {
    let convertedJob = {}
    convertedJob.id = job.id
    convertedJob.title = job.title
    convertedJob.address = job.addresses.full_addresses[0]
    convertedJob.salary = job.salary.max
    convertedJob.experience = job.requirements_arr.flatMap(obj => Object.values(obj?.value)).join(" ");
    convertedJob.typeOfCompany = job.company.industries_str
    convertedJob.level = job.job_levels_str
    convertedJob.jobType = job.job_types_str // in office or online
    convertedJob.major = job.skills_arr.join(" ")
    convertedJob.refreshedTime = job.refreshed.date
    convertedJob.detailURL = job.detail_url
    convertedJob.skills = job.skills_arr
    convertedJob.score = -1;
    return convertedJob

} 

module.exports  = {getAllJobs}