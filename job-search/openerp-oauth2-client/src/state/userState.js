import { createState } from '@hookstate/core';
import { request } from 'api';
import { user } from 'config/menuconfig/user';

const fetchUserState =  () => {
// Initialize your global state
let userData = null
const userState = createState({
  user: null, // Initialize with null (or any default value)
});

// Fetch user data from the server
request("get", "/user/get-user-data", (res) => {
  userData = res.data
}).then(res => userData = res.data);

return userData
}
export default fetchUserState;