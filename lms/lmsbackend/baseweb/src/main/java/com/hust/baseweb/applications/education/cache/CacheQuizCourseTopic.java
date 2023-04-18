package com.hust.baseweb.applications.education.cache;

import com.hust.baseweb.applications.education.entity.QuizCourseTopic;

import java.util.HashMap;
import java.util.UUID;

public class CacheQuizCourseTopic {
    private HashMap<String, QuizCourseTopic> mId2QuizCourseTopic;
    public CacheQuizCourseTopic(){
        mId2QuizCourseTopic = new HashMap();
    }
    public void setmId2QuizCourseTopic(HashMap mId2QuizCourseTopic){
        this.mId2QuizCourseTopic = mId2QuizCourseTopic;
    }
    public synchronized void put(String id, QuizCourseTopic q){
        if(mId2QuizCourseTopic == null) return;
        mId2QuizCourseTopic.put(id,q);
    }
    public QuizCourseTopic get(String id){
        if(mId2QuizCourseTopic == null) return null;
        return mId2QuizCourseTopic.get(id);
    }
}
