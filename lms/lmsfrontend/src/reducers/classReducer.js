const initState = {
  classId: "",
};

const setClassId = (classId) => ({ type: "SET_CLASS_ID", payload: classId });

const classReducer = (state = initState, action) => {
  switch (action.type) {
    case "SET_CLASS_ID":
      return { ...state, classId: action.payload };

    default:
      return state;
  }
};

export { setClassId };

export default classReducer;
