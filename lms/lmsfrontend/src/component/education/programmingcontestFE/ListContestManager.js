import React from "react";
import {ListContestManagerByRegistration} from "./ListContestManagerByRegistration";
import {ListContestAll} from "./ListContestAll";
export function ListContestManager() {
  return (
    <div>
      <ListContestManagerByRegistration/>
      <ListContestAll/>
    </div>
  );
}
