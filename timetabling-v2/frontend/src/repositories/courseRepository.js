import { request } from "api";

export const courseService = {
  getAllCourses: () => 
    request("get", "/course/get-all"),

  createCourse: (courseData) =>
    request("post", "/course/create", null, null, courseData),

  updateCourse: (courseData) =>
    request("post", "/course/update", null, null, courseData),

  deleteCourse: (id) =>
    request("delete", `/course/delete?id=${id}`)
};
