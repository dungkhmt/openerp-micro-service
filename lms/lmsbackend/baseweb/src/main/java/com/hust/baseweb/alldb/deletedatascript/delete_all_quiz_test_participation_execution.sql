delete from quiz_group_question_assignment;
delete from edu_test_quiz_group_participation_assignment;
delete from quiz_group_question_participation_execution;
delete from quiz_group_question_participation_execution_choice;

// xoa bo data lam bai trac nghiem cua quiz_test 131042_TEST_DEMO_TOAN_RR
delete from quiz_group_question_participation_execution_choice
where quiz_group_id in (select quiz_group_id from edu_test_quiz_group
where test_id = '131042_TEST_DEMO_TOAN_RR');

// xoa bo data phan participant cho de thi cua quiz test 131042_TEST_DEMO_TOAN_RR
delete from edu_test_quiz_group_participation_assignment
where  quiz_group_id in (select quiz_group_id from edu_test_quiz_group
where test_id = '131042_TEST_DEMO_TOAN_RR');

// select cac ban ghi phan question vao cac de thi trong quiz test 131042_TEST_DEMO_TOAN_RR
select * from quiz_group_question_assignment where quiz_group_id in (select quiz_group_id from edu_test_quiz_group
where test_id = '131042_TEST_DEMO_TOAN_RR');

// xoa cac ban ghi phan question vao cac de thi trong quiz test 131042_TEST_DEMO_TOAN_RR
delete from quiz_group_question_assignment where quiz_group_id in (select quiz_group_id from edu_test_quiz_group
where test_id = '131042_TEST_DEMO_TOAN_RR');

