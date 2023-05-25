package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProblemSuggestionRequest {

    String course;
    String type;
    String level;
    String description;
    String creative;

    public String generateRequest(){
        StringBuilder question = new StringBuilder();

        question.append("I am a teacher for the " + course + " course.");
        question.append("Generate a new creative problem that my students can practice with topics: " + type + ".");
        question.append("Level: " + level + ".");
        if (description.length() > 0) {
            question.append("Description: " + description);
        }
        if (creative != null && creative.equals("yes")){
            question.append("The problem should be straightforward, but strange so that students can not googling");
        }

        return question.toString();
    }
}
