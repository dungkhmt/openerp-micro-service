const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Define a schema for the nested address object
const addressSchema = new Schema({
    address_region_ids: [String],
    address_region_list: String,
    address_region_array: [String],
    full_addresses: [String],
    sort_addresses: String,
    collection_addresses: [{
        id: Number,
        ward: {
            id: String,
            value: String
        },
        province: {
            id: String,
            value: String
        },
        district: {
            id: String,
            value: String
        },
        postal_code: String,
        full_address: String,
        latitude: Number,
        longitude: Number,
        street: String
    }]
});

// Define the main schema
const jobSchema = new Schema({
    id: Number,
    title: String,
    content: String,
    owned_id: Number,
    company: {
        id: Number,
        display_name: String,
        image_logo: String,
        slug: String,
        detail_url: String,
        image_cover: String,
        image_galleries: [{
            id: Number,
            url: String,
            path: String,
            name: String,
            uploaded: {
                date: String,
                datetime: String,
                since: String,
                timestamp: Number
            },
            source: String
        }],
        benefits: [{
            value: String,
            icon: String
        }],
        skills_arr: [String],
        industries_arr: [String],
        industries_str: String,
        is_followed: Boolean
    },
    extra_skills: [String],
    skills_arr: [String],
    skills_ids: [Number],
    job_types_str: String,
    job_levels_str: String,
    job_levels_arr: [String],
    job_levels_ids: [Number],
    addresses: addressSchema,
    detail_url: String,
    job_url: String,
    slug: String,
    salary: {
        is_negotiable: Number,
        unit: String,
        min: String,
        max: String,
        currency: String,
        min_estimate: Number,
        max_estimate: Number,
        currency_estimate: String,
        value: String
    },
    features: [String],
    packages: [String],
    is_free: Boolean,
    is_basic: Boolean,
    is_basic_plus: Boolean,
    is_distinction: Boolean,
    is_salary_visible: Boolean,
    published: {
        date: String,
        datetime: String,
        since: String
    },
    refreshed: {
        date: String,
        datetime: String,
        since: String
    },
    applied: Schema.Types.Mixed,
    candidate: Schema.Types.Mixed,
    is_applied: Boolean,
    is_followed: Boolean,
    is_blacklisted: Boolean,
    recalled_at: String,
    is_remove_cv: Boolean,
    is_viewed: Boolean,
    requirements_arr: [{
        value: [String],
        icon: String
    }]
});

// Create a model using the schema
const JobModel = mongoose.model('Job', jobSchema);
module.exports = JobModel;

