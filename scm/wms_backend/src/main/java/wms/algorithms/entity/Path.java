package wms.algorithms.entity;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Path {
    double distance;
    double weight;
    double time;
    double transfers;
    boolean points_encoded;
    ArrayList<?> bbox;
    ArrayList<?> instructions;
    ArrayList<?> legs;
    LinkedTreeMap<?, ?> details;
    double ascend;
    double descend;
}
