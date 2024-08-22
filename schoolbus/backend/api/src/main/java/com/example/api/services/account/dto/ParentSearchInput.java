package com.example.api.services.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSearchInput {
    private Long id;

    private String name;

    private String role;

    private String phoneNumber;

    private SearchBy searchBy;

    private Pageable pageable;

    private Long studentId;

    @Getter
    public enum SearchBy {
        PARENT_NAME("PARENT_NAME"),
        STUDENT_NAME("STUDENT_NAME"),
        PARENT_PHONE_NUMBER("PARENT_PHONE_NUMBER"),
        ;
        private final String value;

        SearchBy(String value) {
            this.value = value;
        }

        public static SearchBy fromValue(String value) {
            if(value == null) {
                return SearchBy.PARENT_NAME;
            }
            for (SearchBy searchBy : SearchBy.values()) {
                if (searchBy.value.equals(value)) {
                    return searchBy;
                }
            }
            return SearchBy.PARENT_NAME;
        }
    }
}
