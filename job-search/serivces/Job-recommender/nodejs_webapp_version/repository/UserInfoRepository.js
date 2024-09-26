const UserJob = require("../db-models/userInfo")

const saveUserInfo = async (req, res) => {
    newUserInfo = new UserJob(req.body)
    await newUserInfo.save().then(savedJob => {
        console.log('Đã lưu công việc người dùng nhập: ', savedJob);
      })
      .catch(error => {
        console.error('Lỗi khi lưu công việc:', error);
      });
}

module.exports = {saveUserInfo}