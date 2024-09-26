package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private String ID;
    private String earliestTime; // 2023-02-03 10:30:00
    private String latestTime; // 2023-02-03 11:00:00
    private String locationID;
    private int weight;
}
