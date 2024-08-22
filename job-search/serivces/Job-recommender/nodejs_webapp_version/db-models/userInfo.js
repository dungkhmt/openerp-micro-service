const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userJobSchema = new Schema({
  title: String,
  address: String,
  salary: String,
  experience: String,
  typeOfCompany: String,
  level: String,
  jobType: String,
  major: String,
  skills: [String],
  refreshedTime: String,
  detailURL: String
});

// Tạo model từ schema
const Job = mongoose.model('userJobs', userJobSchema);
module.exports = Job