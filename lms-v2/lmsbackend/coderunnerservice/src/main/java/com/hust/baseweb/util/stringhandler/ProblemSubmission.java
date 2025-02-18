package com.hust.baseweb.util.stringhandler;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProblemSubmission {

    String status;

    String compileOutput;

    Long runtime;

    Float memory;

    String testCasePass;

    int nbTestCasePass;

    int score;

    List<String> testCaseAns;

    List<String> participantAns; // List only for judging parallel, but currently only judge sequentially

    String stderr;

    String scoreEvaluationType;
}
