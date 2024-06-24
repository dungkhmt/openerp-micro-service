const mongoose = require('mongoose');
require('dotenv').config();
const dbHost = process.env.DB_HOST;

async function connect() {
    try {
        await mongoose.connect(dbHost);
        console.log('connect successfully');
    } catch (error) {
        console.log('connect failed');
    }
}

module.exports = { connect };