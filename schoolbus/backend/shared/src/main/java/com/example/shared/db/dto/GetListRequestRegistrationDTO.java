package com.example.shared.db.dto;

import com.example.shared.db.entities.Parent;
import com.example.shared.db.entities.RequestRegistration;
import com.example.shared.db.entities.Student;

public interface GetListRequestRegistrationDTO {
    Student getStudent();
    Parent getParent();
    RequestRegistration getRequestRegistration();
}
